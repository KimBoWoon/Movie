package com.cheeke.surfy.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.map
import com.cheeke.surfy.common.Log
import com.cheeke.surfy.common.di.ActivityRetainedScopeCoroutine
import com.cheeke.surfy.data.model.asExternalModel
import com.cheeke.surfy.data.repository.MovieDataBaseRepository
import com.cheeke.surfy.data.repository.PagingRepository
import com.cheeke.surfy.data.util.DataManager
import com.cheeke.surfy.data.util.NetworkMonitor
import com.cheeke.surfy.database.model.NowPlayingMovieEntity
import com.cheeke.surfy.database.model.UpComingMovieEntity
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.model.TrendingMediaResult
import com.cheeke.surfy.navigation.HomeScreen
import com.cheeke.surfy.navigation.LocalRootNavigator
import com.cheeke.surfy.navigation.goToMovie
import com.cheeke.surfy.navigation.goToPeople
import com.cheeke.surfy.navigation.goToTv
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ActivityRetainedScoped
class HomeRepository @Inject constructor(
    @param:ActivityRetainedScopeCoroutine private val scope: CoroutineScope,
    private val dataManager: DataManager,
    private val movieDataBaseRepository: MovieDataBaseRepository,
    private val pagingRepository: PagingRepository,
    private val networkMonitor: NetworkMonitor
) {
    companion object {
        private const val TAG = "HomeRepository"
        private const val PAGE_SIZE = 20
        private const val PREFETCH_DISTANCE = 5
    }

    private val pagingConfig = PagingConfig(
        pageSize = PAGE_SIZE,
        prefetchDistance = PREFETCH_DISTANCE
    )
    private val localeState = dataManager.localeFlow
        .map { locale -> "${locale.language}-${locale.region}" }
        .distinctUntilChanged()
    val nowPlayingMoviePaging = Pager(
        config = PagingConfig(pageSize = 20, prefetchDistance = 5),
        pagingSourceFactory = { movieDataBaseRepository.getNowPlayingMovies() }
    ).flow.map { pagingData ->
        pagingData.map(transform = NowPlayingMovieEntity::asExternalModel)
    }.cachedIn(scope = scope)
    val upComingMoviePaging = Pager(
        config = PagingConfig(pageSize = 20, prefetchDistance = 5),
        pagingSourceFactory = { movieDataBaseRepository.getUpComingMovies() }
    ).flow.map { pagingData ->
        pagingData.map(transform = UpComingMovieEntity::asExternalModel)
    }.cachedIn(scope = scope)
    private val trendingMovieTimeWindow = MutableStateFlow(value = TimeWindow.DAY)
    @OptIn(ExperimentalCoroutinesApi::class)
    val trendingMoviePaging =
        trendingMovieTimeWindow
            .flatMapLatest { timeWindow ->
                createTrending(timeWindow, pagingRepository::getTrendingMovie)
            }.cachedIn(scope)
    private val trendingPeopleTimeWindow = MutableStateFlow(value = TimeWindow.DAY)
    @OptIn(ExperimentalCoroutinesApi::class)
    val trendingPeoplePaging =
        trendingPeopleTimeWindow
            .flatMapLatest { timeWindow ->
                createTrending(timeWindow, pagingRepository::getTrendingPeople)
            }.cachedIn(scope)
    private val trendingTvTimeWindow = MutableStateFlow(value = TimeWindow.DAY)
    @OptIn(ExperimentalCoroutinesApi::class)
    val trendingTvPaging =
        trendingTvTimeWindow
            .flatMapLatest { timeWindow ->
                createTrending(timeWindow, pagingRepository::getTrendingTv)
            }.cachedIn(scope)

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun <T : Any> createTrending(
        timeWindow: TimeWindow,
        factory: (String, String) -> PagingSource<Int, T>
    ): Flow<PagingData<T>> =
        combine(localeState, networkMonitor.isOnline) { language, _ -> language }
            .flatMapLatest { language ->
                Pager(
                    config = pagingConfig,
                    pagingSourceFactory = { factory(timeWindow.label, language) }
                ).flow
            }.cachedIn(scope)

    suspend fun getPopularMovies() = movieDataBaseRepository.getPopularMovies()

    fun updateTrendingMovieTimeWindow(timeWindow: TimeWindow) {
        trendingMovieTimeWindow.value = timeWindow
    }

    fun updateTrendingPeopleTimeWindow(timeWindow: TimeWindow) {
        trendingPeopleTimeWindow.value = timeWindow
    }

    fun updateTrendingTvTimeWindow(timeWindow: TimeWindow) {
        trendingTvTimeWindow.value = timeWindow
    }
}

class HomePresenter @AssistedInject constructor(
    private val homeRepository: HomeRepository
) : Presenter<HomeState> {
    @Composable
    override fun present(): HomeState {
        val rootNavigator = LocalRootNavigator.current
        var trendingMovieTimeWindow by rememberSaveable { mutableStateOf(value = TimeWindow.DAY) }
        var trendingPeopleTimeWindow by rememberSaveable { mutableStateOf(value = TimeWindow.DAY) }
        var trendingTvTimeWindow by rememberSaveable { mutableStateOf(value = TimeWindow.DAY) }
        val nowPlayingMovies = homeRepository.nowPlayingMoviePaging.collectAsLazyPagingItems()
        val upComingMovies = homeRepository.upComingMoviePaging.collectAsLazyPagingItems()
        val trendingMoviePaging = homeRepository.trendingMoviePaging.collectAsLazyPagingItems()
        val trendingPeoplePaging = homeRepository.trendingPeoplePaging.collectAsLazyPagingItems()
        val trendingTvPaging = homeRepository.trendingTvPaging.collectAsLazyPagingItems()
        var status by rememberRetained { mutableStateOf<HomeStatus>(value = HomeStatus.Loading) }

        LaunchedEffect(key1 = Unit) {
            runCatching {
                val popularMovies = homeRepository.getPopularMovies()
                status = HomeStatus.Success(
                    homeUiState = HomeUiState(
                        popularMovies = popularMovies,
                        nowPlayingMovies = nowPlayingMovies,
                        upComingMovies = upComingMovies,
                        trendingMovieTimeWindow = trendingMovieTimeWindow,
                        trendingPeopleTimeWindow = trendingPeopleTimeWindow,
                        trendingTvTimeWindow = trendingTvTimeWindow,
                        trendingMoviePaging = trendingMoviePaging,
                        trendingPeoplePaging = trendingPeoplePaging,
                        trendingTvPaging = trendingTvPaging
                    )
                )
            }.onFailure { e ->
                status = HomeStatus.Error(throwable = e)
            }
        }

        return HomeState(
            homeStatus = status,
            eventSink = { event ->
                Log.d("HomePresenter", "$event")
                when (event) {
                    is HomeEvent.GoToMovie -> rootNavigator.goToMovie(id = event.id)
                    is HomeEvent.GoToPeople -> rootNavigator.goToPeople(id = event.id)
                    is HomeEvent.GoToTv -> rootNavigator.goToTv(id = event.id)
                    is HomeEvent.UpdateTrendingMovieTimeWindow -> {
                        trendingMovieTimeWindow = event.timeWindow
                        homeRepository.updateTrendingMovieTimeWindow(event.timeWindow)
                    }
                    is HomeEvent.UpdateTrendingPeopleTimeWindow -> {
                        trendingPeopleTimeWindow = event.timeWindow
                        homeRepository.updateTrendingPeopleTimeWindow(event.timeWindow)
                    }
                    is HomeEvent.UpdateTrendingTvTimeWindow -> {
                        trendingTvTimeWindow = event.timeWindow
                        homeRepository.updateTrendingTvTimeWindow(event.timeWindow)
                    }
                }
            }
        )
    }

    @CircuitInject(screen = HomeScreen::class, scope = ActivityRetainedComponent::class)
    @AssistedFactory
    interface Factory {
        fun create(): HomePresenter
    }
}

data class HomeUiState(
    val popularMovies: List<Movie>,
    val nowPlayingMovies: LazyPagingItems<Movie>,
    val upComingMovies: LazyPagingItems<Movie>,
    val trendingMovieTimeWindow: TimeWindow,
    val trendingPeopleTimeWindow: TimeWindow,
    val trendingTvTimeWindow: TimeWindow,
    val trendingMoviePaging: LazyPagingItems<TrendingMediaResult>,
    val trendingPeoplePaging: LazyPagingItems<TrendingMediaResult>,
    val trendingTvPaging: LazyPagingItems<TrendingMediaResult>
)

sealed interface HomeEvent : CircuitUiEvent {
    data class GoToMovie(val id: Int) : HomeEvent
    data class GoToPeople(val id: Int) : HomeEvent
    data class GoToTv(val id: Int) : HomeEvent
    data class UpdateTrendingMovieTimeWindow(val timeWindow: TimeWindow) : HomeEvent
    data class UpdateTrendingPeopleTimeWindow(val timeWindow: TimeWindow) : HomeEvent
    data class UpdateTrendingTvTimeWindow(val timeWindow: TimeWindow) : HomeEvent
}

data class HomeState(
    val homeStatus: HomeStatus,
    val eventSink: (HomeEvent) -> Unit
) : CircuitUiState

enum class TimeWindow(val label: String) {
    DAY(label = "day"), WEEK(label = "week")
}

sealed interface HomeStatus {
    object Loading : HomeStatus
    data class Success(val homeUiState: HomeUiState) : HomeStatus
    data class Error(val throwable: Throwable) : HomeStatus
}