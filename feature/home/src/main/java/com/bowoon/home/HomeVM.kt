package com.bowoon.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.bowoon.common.Result
import com.bowoon.common.asResult
import com.bowoon.data.model.asExternalModel
import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.data.repository.PagingRepository
import com.bowoon.data.util.DataManager
import com.bowoon.data.util.NetworkMonitor
import com.bowoon.database.model.NowPlayingMovieEntity
import com.bowoon.database.model.UpComingMovieEntity
import com.bowoon.model.Movie
import com.bowoon.model.TrendingMovieResult
import com.bowoon.model.TrendingPeopleResult
import com.bowoon.model.TrendingTvResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeVM @Inject constructor(
    dataManager: DataManager,
    databaseRepository: DatabaseRepository,
    pagingRepository: PagingRepository,
    networkMonitor: NetworkMonitor
) : ViewModel() {
    companion object {
        private const val TAG = "HomeVM"
    }

    private val _trendingMovieTimeWindow = MutableStateFlow(value = TimeWindow.DAY)
    val trendingMovieTimeWindow = _trendingMovieTimeWindow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = TimeWindow.DAY
        )
    private val _trendingPeopleTimeWindow = MutableStateFlow(value = TimeWindow.DAY)
    val trendingPeopleTimeWindow = _trendingPeopleTimeWindow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = TimeWindow.DAY
        )
    private val _trendingTvTimeWindow = MutableStateFlow(value = TimeWindow.DAY)
    val trendingTvTimeWindow = _trendingTvTimeWindow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = TimeWindow.DAY
        )

    private val trendingMovie = combine(
        _trendingMovieTimeWindow,
        dataManager.localeFlow,
        networkMonitor.isOnline.distinctUntilChanged().filter { it }
    ) { timeWindow, language, isOnline ->
        Pager(
            config = PagingConfig(pageSize = 20, prefetchDistance = 5),
            pagingSourceFactory = { pagingRepository.getTrendingMovie(timeWindow = timeWindow.label, language = "${language.language}-${language.region}") }
        ).flow.cachedIn(scope = viewModelScope)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = flowOf(value = PagingData.empty())
    )
    private val trendingPeople = combine(
        _trendingPeopleTimeWindow,
        dataManager.localeFlow,
        networkMonitor.isOnline.distinctUntilChanged().filter { it }
    ) { timeWindow, language, isOnline ->
        Pager(
            config = PagingConfig(pageSize = 20, prefetchDistance = 5),
            pagingSourceFactory = { pagingRepository.getTrendingPeople(timeWindow = timeWindow.label, language = "${language.language}-${language.region}") }
        ).flow.cachedIn(scope = viewModelScope)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = flowOf(value = PagingData.empty())
    )
    private val trendingTv = combine(
        _trendingTvTimeWindow,
        dataManager.localeFlow,
        networkMonitor.isOnline.distinctUntilChanged().filter { it }
    ) { timeWindow, language, isOnline ->
        Pager(
            config = PagingConfig(pageSize = 20, prefetchDistance = 5),
            pagingSourceFactory = { pagingRepository.getTrendingTv(timeWindow = timeWindow.label, language = "${language.language}-${language.region}") }
        ).flow.cachedIn(scope = viewModelScope)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = flowOf(value = PagingData.empty())
    )

    val mainMenu = combine(
        flowOf(
            value = Pager(
                config = PagingConfig(pageSize = 20, prefetchDistance = 5),
                pagingSourceFactory = { databaseRepository.getNowPlayingMovies() }
            ).flow.map { pagingData ->
                pagingData.map(transform = NowPlayingMovieEntity::asExternalModel)
            }.cachedIn(scope = viewModelScope)
        ),
        flowOf(
            value = Pager(
                config = PagingConfig(pageSize = 20, prefetchDistance = 5),
                pagingSourceFactory = { databaseRepository.getUpComingMovies() }
            ).flow.map { pagingData ->
                pagingData.map(transform = UpComingMovieEntity::asExternalModel)
            }.cachedIn(scope = viewModelScope)
        ),
        trendingMovie,
        trendingPeople,
        trendingTv
    ) { nowPlayingMovies, upComingMovies, trendingMovie, trendingPeople, trendingTv ->
        MainData(
            nowPlayingMoviePager = nowPlayingMovies,
            upComingMoviePager = upComingMovies,
            trendingMoviePager = trendingMovie,
            trendingPeoplePager = trendingPeople,
            trendingTvPager = trendingTv
        )
    }.asResult()
        .map { result ->
            when (result) {
                is Result.Loading -> MainMenuState.Loading
                is Result.Success -> MainMenuState.Success(
                    nowPlayingMoviePager = result.data.nowPlayingMoviePager,
                    upComingMoviePager = result.data.upComingMoviePager,
                    trendingMoviePager = result.data.trendingMoviePager,
                    trendingPeoplePager = result.data.trendingPeoplePager,
                    trendingTvPager = result.data.trendingTvPager
                )
                is Result.Error -> MainMenuState.Error(throwable = result.throwable)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = MainMenuState.Loading
        )

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
}

sealed interface MainMenuState {
    data object Loading : MainMenuState
    data class Success(
        val nowPlayingMoviePager: Flow<PagingData<Movie>>,
        val upComingMoviePager: Flow<PagingData<Movie>>,
        val trendingMoviePager: Flow<PagingData<TrendingMovieResult>>,
        val trendingPeoplePager: Flow<PagingData<TrendingPeopleResult>>,
        val trendingTvPager: Flow<PagingData<TrendingTvResult>>
    ) : MainMenuState
    data class Error(val throwable: Throwable) : MainMenuState
}

data class MainData(
    val nowPlayingMoviePager: Flow<PagingData<Movie>>,
    val upComingMoviePager: Flow<PagingData<Movie>>,
    val trendingMoviePager: Flow<PagingData<TrendingMovieResult>>,
    val trendingPeoplePager: Flow<PagingData<TrendingPeopleResult>>,
    val trendingTvPager: Flow<PagingData<TrendingTvResult>>
)

enum class TimeWindow(val label: String) {
    DAY(label = "day"), WEEK(label = "week")
}