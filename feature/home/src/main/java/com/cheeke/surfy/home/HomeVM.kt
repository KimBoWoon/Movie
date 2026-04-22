package com.cheeke.surfy.home

import androidx.compose.runtime.Composable
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
import com.cheeke.surfy.data.repository.DatabaseRepository
import com.cheeke.surfy.data.repository.PagingRepository
import com.cheeke.surfy.data.util.DataManager
import com.cheeke.surfy.data.util.NetworkMonitor
import com.cheeke.surfy.database.model.NowPlayingMovieEntity
import com.cheeke.surfy.database.model.UpComingMovieEntity
import com.cheeke.surfy.home.navigation.HomeScreen
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.model.TrendingMediaResult
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
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
    private val databaseRepository: DatabaseRepository,
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
        pagingSourceFactory = { databaseRepository.getNowPlayingMovies() }
    ).flow.map { pagingData ->
        pagingData.map(transform = NowPlayingMovieEntity::asExternalModel)
    }.cachedIn(scope = scope)
    val upComingMoviePaging = Pager(
        config = PagingConfig(pageSize = 20, prefetchDistance = 5),
        pagingSourceFactory = { databaseRepository.getUpComingMovies() }
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

    suspend fun getPopularMovies() = databaseRepository.getPopularMovies()

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
    @Assisted(value = "goToMovie") private val goToMovie: (Int) -> Unit,
    @Assisted(value = "goToPeople") private val goToPeople: (Int) -> Unit,
    @Assisted(value = "goToTv") private val goToTv: (Int) -> Unit,
    private val homeRepository: HomeRepository
) : Presenter<HomeState> {
    @Composable
    override fun present(): HomeState {
        var trendingMovieTimeWindow by rememberSaveable { mutableStateOf(value = TimeWindow.DAY) }
        var trendingPeopleTimeWindow by rememberSaveable { mutableStateOf(value = TimeWindow.DAY) }
        var trendingTvTimeWindow by rememberSaveable { mutableStateOf(value = TimeWindow.DAY) }
        val popularMovies by produceRetainedState<List<Movie>?>(initialValue = null) {
            value = homeRepository.getPopularMovies()
        }
        val nowPlayingMovies = homeRepository.nowPlayingMoviePaging.collectAsLazyPagingItems()
        val upComingMovies = homeRepository.upComingMoviePaging.collectAsLazyPagingItems()
        val trendingMoviePaging = homeRepository.trendingMoviePaging.collectAsLazyPagingItems()
        val trendingPeoplePaging = homeRepository.trendingPeoplePaging.collectAsLazyPagingItems()
        val trendingTvPaging = homeRepository.trendingTvPaging.collectAsLazyPagingItems()

        return when {
            popularMovies == null -> HomeState.Loading
            else -> HomeState.Success(
                homeUiState = HomeUiState(
                    popularMovies = popularMovies ?: emptyList(),
                    nowPlayingMovies = nowPlayingMovies,
                    upComingMovies = upComingMovies,
                    trendingMovieTimeWindow = trendingMovieTimeWindow,
                    trendingPeopleTimeWindow = trendingPeopleTimeWindow,
                    trendingTvTimeWindow = trendingTvTimeWindow,
                    trendingMoviePaging = trendingMoviePaging,
                    trendingPeoplePaging = trendingPeoplePaging,
                    trendingTvPaging = trendingTvPaging,
                ) { event ->
                    Log.d("HomePresenter", "$event")
                    when (event) {
                        is HomeEvent.GoToMovie -> goToMovie(event.id)
                        is HomeEvent.GoToPeople -> goToPeople(event.id)
                        is HomeEvent.GoToTv -> goToTv(event.id)
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
    }

    @CircuitInject(screen = HomeScreen::class, scope = ActivityRetainedComponent::class)
    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted(value = "goToMovie") goToMovie: ((Int) -> Unit) = {},
            @Assisted(value = "goToPeople") goToPeople: ((Int) -> Unit) = {},
            @Assisted(value = "goToTv") goToTv: ((Int) -> Unit) = {}
        ): HomePresenter
    }
}

class HomeUiState(
    val popularMovies: List<Movie>,
    val nowPlayingMovies: LazyPagingItems<Movie>,
    val upComingMovies: LazyPagingItems<Movie>,
    val trendingMovieTimeWindow: TimeWindow,
    val trendingPeopleTimeWindow: TimeWindow,
    val trendingTvTimeWindow: TimeWindow,
    val trendingMoviePaging: LazyPagingItems<TrendingMediaResult>,
    val trendingPeoplePaging: LazyPagingItems<TrendingMediaResult>,
    val trendingTvPaging: LazyPagingItems<TrendingMediaResult>,
    val eventSink: (HomeEvent) -> Unit
)

sealed interface HomeEvent {
    data class GoToMovie(val id: Int) : HomeEvent
    data class GoToPeople(val id: Int) : HomeEvent
    data class GoToTv(val id: Int) : HomeEvent
    data class UpdateTrendingMovieTimeWindow(val timeWindow: TimeWindow) : HomeEvent
    data class UpdateTrendingPeopleTimeWindow(val timeWindow: TimeWindow) : HomeEvent
    data class UpdateTrendingTvTimeWindow(val timeWindow: TimeWindow) : HomeEvent
}

enum class TimeWindow(val label: String) {
    DAY(label = "day"), WEEK(label = "week")
}

sealed interface HomeState : CircuitUiState {
    data object Loading : HomeState
    data class Success(val homeUiState: HomeUiState) : HomeState
    data class Error(val throwable: Throwable) : HomeState
}