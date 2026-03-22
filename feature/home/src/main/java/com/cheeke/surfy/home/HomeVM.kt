package com.cheeke.surfy.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import androidx.paging.map
import com.cheeke.surfy.common.Result
import com.cheeke.surfy.common.asResult
import com.cheeke.surfy.data.model.asExternalModel
import com.cheeke.surfy.data.repository.DatabaseRepository
import com.cheeke.surfy.data.repository.PagingRepository
import com.cheeke.surfy.data.repository.UserDataRepository
import com.cheeke.surfy.data.util.DataManager
import com.cheeke.surfy.data.util.NetworkMonitor
import com.cheeke.surfy.database.model.NowPlayingMovieEntity
import com.cheeke.surfy.database.model.UpComingMovieEntity
import com.cheeke.surfy.model.Media
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.model.TrendingMovieResult
import com.cheeke.surfy.model.TrendingPeopleResult
import com.cheeke.surfy.model.TrendingTvResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeVM @Inject constructor(
    dataManager: DataManager,
    databaseRepository: DatabaseRepository,
    pagingRepository: PagingRepository,
    networkMonitor: NetworkMonitor,
    private val userDataRepository: UserDataRepository
) : ViewModel() {
    companion object {
        private const val TAG = "HomeVM"
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
    }.cachedIn(scope = viewModelScope)
    val upComingMoviePaging = Pager(
        config = PagingConfig(pageSize = 20, prefetchDistance = 5),
        pagingSourceFactory = { databaseRepository.getUpComingMovies() }
    ).flow.map { pagingData ->
        pagingData.map(transform = UpComingMovieEntity::asExternalModel)
    }.cachedIn(scope = viewModelScope)
    val trendingMoviePaging: Flow<PagingData<TrendingMovieResult>> =
        createTrendingPaging(
            timeWindowFlow = trendingMovieTimeWindow,
            pagingSourceFactory = pagingRepository::getTrendingMovie
        )
    val trendingPeoplePaging: Flow<PagingData<TrendingPeopleResult>> =
        createTrendingPaging(
            timeWindowFlow = trendingPeopleTimeWindow,
            pagingSourceFactory = pagingRepository::getTrendingPeople
        )
    val trendingTvPaging: Flow<PagingData<TrendingTvResult>> =
        createTrendingPaging(
            timeWindowFlow = trendingTvTimeWindow,
            pagingSourceFactory = pagingRepository::getTrendingTv
        )
    private val _nextWeekReleaseMovies: MutableStateFlow<List<Media>> = MutableStateFlow(value = emptyList())
    private val dismissedThisSession: MutableStateFlow<Boolean> = MutableStateFlow(value = false)
    private val hiddenToday: StateFlow<Boolean> = flow {
        emit(value = userDataRepository.getShowNextReleaseMoviesDate())
    }.map { stored ->
        stored == LocalDate.now().toString()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = false
    )
    val shouldShowNextWeekReleaseDialog: StateFlow<Boolean> =
        combine(
            flow = _nextWeekReleaseMovies,
            flow2 = hiddenToday,
            flow3 = dismissedThisSession
        ) { snapshot: List<Media>?, hidden: Boolean, dismissed: Boolean ->
            !snapshot.isNullOrEmpty() && !hidden && !dismissed
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = false
        )
    val nextWeekReleaseDialogItems: StateFlow<List<Media>> =
        _nextWeekReleaseMovies
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = emptyList()
            )
    val homeUiState: StateFlow<HomeState> = combine(
        databaseRepository.getPopularMovies(),
        shouldShowNextWeekReleaseDialog,
        nextWeekReleaseDialogItems
    ) { popularMovies, isShowNextWeekReleaseMovieDialog, nextWeekReleaseMovies ->
        HomeUiState(
            popularMovies = popularMovies.shuffled(),
            isShowNextWeekReleaseMovieDialog = isShowNextWeekReleaseMovieDialog,
            nextWeekReleaseMovies = nextWeekReleaseMovies
        )
    }.asResult()
        .map { result ->
            when (result) {
                is Result.Loading -> HomeState.Loading
                is Result.Success -> HomeState.Success(homeUiState = result.data)
                is Result.Error -> HomeState.Error(result.throwable)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = HomeState.Loading
        )

    init {
        viewModelScope.launch {
            supervisorScope {
                _nextWeekReleaseMovies.emit(
                    value = combine(
                        flow = databaseRepository.getNextWeekReleaseMovies(),
                        flow2 = databaseRepository.getNextWeekReleaseTvs()
                    ) { movies: List<Media>, tvs: List<Media> ->
                        movies + tvs
                    }.first().sortedBy { it.releaseDate }
                )
            }
        }
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
            }.cachedIn(viewModelScope)
    }

    fun dismissNextWeekReleaseDialog() {
        dismissedThisSession.value = true
    }

    fun dontShowNextWeekReleaseDialogToday() {
        dismissedThisSession.value = true
        viewModelScope.launch {
            userDataRepository.updateShowNextReleaseMoviesDate(value = LocalDate.now().toString())
        }
    }
}

sealed interface HomeState {
    data object Loading : HomeState
    data class Success(val homeUiState: HomeUiState) : HomeState
    data class Error(val throwable: Throwable) : HomeState
}

data class HomeUiState(
    val popularMovies: List<Movie> = emptyList(),
    val isShowNextWeekReleaseMovieDialog: Boolean = false,
    val nextWeekReleaseMovies: List<Media> = emptyList(),
)

enum class TimeWindow(val label: String) {
    DAY(label = "day"), WEEK(label = "week")
}

private data class TrendingRequest(
    val timeWindow: String,
    val language: String
)