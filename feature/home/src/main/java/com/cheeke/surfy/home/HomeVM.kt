package com.cheeke.surfy.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.map
import com.cheeke.surfy.common.Log
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ActivityRetainedScoped
class HomeRepository @Inject constructor(
    dataManager: DataManager,
    databaseRepository: DatabaseRepository,
    pagingRepository: PagingRepository,
    networkMonitor: NetworkMonitor
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
    private val _trendingMovieTimeWindow = MutableStateFlow(value = TimeWindow.DAY)
    val trendingMovieTimeWindow = _trendingMovieTimeWindow.asStateFlow()
    private val _trendingPeopleTimeWindow = MutableStateFlow(value = TimeWindow.DAY)
    val trendingPeopleTimeWindow = _trendingPeopleTimeWindow.asStateFlow()
    private val _trendingTvTimeWindow = MutableStateFlow(value = TimeWindow.DAY)
    val trendingTvTimeWindow = _trendingTvTimeWindow.asStateFlow()
    private val onlineState = networkMonitor.isOnline
        .distinctUntilChanged()
        .filter { it }
    private val localeState = dataManager.localeFlow
        .map { locale -> "${locale.language}-${locale.region}" }
        .distinctUntilChanged()
    val nowPlayingMoviePaging = Pager(
        config = PagingConfig(pageSize = 20, prefetchDistance = 5),
        pagingSourceFactory = { databaseRepository.getNowPlayingMovies() }
    ).flow.map { pagingData ->
        pagingData.map(transform = NowPlayingMovieEntity::asExternalModel)
    }/*.cachedIn(scope = viewModelScope)*/
    val upComingMoviePaging = Pager(
        config = PagingConfig(pageSize = 20, prefetchDistance = 5),
        pagingSourceFactory = { databaseRepository.getUpComingMovies() }
    ).flow.map { pagingData ->
        pagingData.map(transform = UpComingMovieEntity::asExternalModel)
    }/*.cachedIn(scope = viewModelScope)*/
    val trendingMoviePaging: Flow<PagingData<TrendingMediaResult>> =
        createTrendingPaging(
            timeWindowFlow = trendingMovieTimeWindow,
            pagingSourceFactory = pagingRepository::getTrendingMovie
        )
    val trendingPeoplePaging: Flow<PagingData<TrendingMediaResult>> =
        createTrendingPaging(
            timeWindowFlow = trendingPeopleTimeWindow,
            pagingSourceFactory = pagingRepository::getTrendingPeople
        )
    val trendingTvPaging: Flow<PagingData<TrendingMediaResult>> =
        createTrendingPaging(
            timeWindowFlow = trendingTvTimeWindow,
            pagingSourceFactory = pagingRepository::getTrendingTv
        )
    val popularMovies = flow {
        emit(value = databaseRepository.getPopularMovies())
    }

    fun updateTrendingMovieTimeWindow(timeWindow: TimeWindow) {
        when (timeWindow) {
            TimeWindow.DAY -> _trendingMovieTimeWindow.value = TimeWindow.DAY
            TimeWindow.WEEK -> _trendingMovieTimeWindow.value = TimeWindow.WEEK
        }
    }

    fun updateTrendingPeopleTimeWindow(timeWindow: TimeWindow) {
        when (timeWindow) {
            TimeWindow.DAY -> _trendingPeopleTimeWindow.value = TimeWindow.DAY
            TimeWindow.WEEK -> _trendingPeopleTimeWindow.value = TimeWindow.WEEK
        }
    }

    fun updateTrendingTvTimeWindow(timeWindow: TimeWindow) {
        when (timeWindow) {
            TimeWindow.DAY -> _trendingTvTimeWindow.value = TimeWindow.DAY
            TimeWindow.WEEK -> _trendingTvTimeWindow.value = TimeWindow.WEEK
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun <T : Any> createTrendingPaging(
        timeWindowFlow: StateFlow<TimeWindow>,
        pagingSourceFactory: (timeWindow: String, language: String) -> PagingSource<Int, T>
    ): Flow<PagingData<T>> {
        return combine(
            timeWindowFlow,
            localeState,
            onlineState
        ) { timeWindow, language, _ ->
            TrendingRequest(timeWindow = timeWindow.label, language = language)
        }.distinctUntilChanged()
            .flatMapLatest { request ->
                Pager(
                    config = pagingConfig,
                    pagingSourceFactory = {
                        pagingSourceFactory(request.timeWindow, request.language)
                    }
                ).flow
            }/*.cachedIn(viewModelScope)*/
    }
}

class HomePresenter @AssistedInject constructor(
    @Assisted(value = "goToMovie") private val goToMovie: (Int) -> Unit,
    @Assisted(value = "goToPeople") private val goToPeople: (Int) -> Unit,
    @Assisted(value = "goToTv") private val goToTv: (Int) -> Unit,
    private val homeRepository: HomeRepository
) : Presenter<HomeUiState> {
    @Composable
    override fun present(): HomeUiState {
        val popularMovies by produceRetainedState(initialValue = emptyList()) {
            homeRepository.popularMovies.collect { popularMovies ->
                value = popularMovies
            }
        }
        val nowPlayingMovies = homeRepository.nowPlayingMoviePaging.collectAsLazyPagingItems()
        val upComingMovies = homeRepository.upComingMoviePaging.collectAsLazyPagingItems()
        val trendingMovieTimeWindow by produceRetainedState(initialValue = TimeWindow.DAY) {
            homeRepository.trendingMovieTimeWindow.collect {
                value = it
            }
        }
        val trendingPeopleTimeWindow by produceRetainedState(initialValue = TimeWindow.DAY) {
            homeRepository.trendingPeopleTimeWindow.collect {
                value = it
            }
        }
        val trendingTvTimeWindow by produceRetainedState(initialValue = TimeWindow.DAY) {
            homeRepository.trendingTvTimeWindow.collect {
                value = it
            }
        }
        val trendingMoviePaging = remember(key1 = trendingMovieTimeWindow) {
            homeRepository.trendingMoviePaging
        }.collectAsLazyPagingItems()
        val trendingPeoplePaging = remember(key1 = trendingPeopleTimeWindow) {
            homeRepository.trendingPeoplePaging
        }.collectAsLazyPagingItems()
        val trendingTvPaging = remember(key1 = trendingTvTimeWindow) {
            homeRepository.trendingTvPaging
        }.collectAsLazyPagingItems()

        return HomeUiState(
            popularMovies = popularMovies,
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
                is HomeEvent.UpdateTrendingMovieTimeWindow -> homeRepository.updateTrendingMovieTimeWindow(event.timeWindow)
                is HomeEvent.UpdateTrendingPeopleTimeWindow -> homeRepository.updateTrendingPeopleTimeWindow(event.timeWindow)
                is HomeEvent.UpdateTrendingTvTimeWindow -> homeRepository.updateTrendingTvTimeWindow(event.timeWindow)
            }
        }
    }

    @CircuitInject(HomeScreen::class, ActivityRetainedComponent::class)
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
) : CircuitUiState

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

private data class TrendingRequest(
    val timeWindow: String,
    val language: String
)

sealed interface HomeState {
    data object Loading : HomeState
    data class Success(val homeUiState: HomeUiState) : HomeState
    data class Error(val throwable: Throwable) : HomeState
}