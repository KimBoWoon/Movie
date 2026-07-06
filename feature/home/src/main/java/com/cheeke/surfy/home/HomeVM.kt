package com.cheeke.surfy.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import androidx.paging.map
import androidx.paging.rxjava3.cachedIn
import androidx.paging.rxjava3.flowable
import com.cheeke.surfy.data.model.asExternalModel
import com.cheeke.surfy.data.repository.MovieDataBaseRepository
import com.cheeke.surfy.data.repository.PagingRepository
import com.cheeke.surfy.data.util.DataManager
import com.cheeke.surfy.data.util.NetworkMonitor
import com.cheeke.surfy.database.model.NowPlayingMovieEntity
import com.cheeke.surfy.database.model.UpComingMovieEntity
import com.cheeke.surfy.model.Media
import com.cheeke.surfy.model.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import javax.inject.Inject

@HiltViewModel
class HomeVM @Inject constructor(
    private val dataManager: DataManager,
    movieDataBaseRepository: MovieDataBaseRepository,
    pagingRepository: PagingRepository,
    networkMonitor: NetworkMonitor
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
    val trendingMovieTimeWindow = BehaviorSubject.createDefault(TimeWindow.DAY)
    val trendingPeopleTimeWindow = BehaviorSubject.createDefault(TimeWindow.DAY)
    val trendingTvTimeWindow = BehaviorSubject.createDefault(TimeWindow.DAY)
    private val onlineState = networkMonitor.isOnline
        .distinctUntilChanged()
        .filter { it }
    val nowPlayingMoviePaging = Pager(
        config = PagingConfig(pageSize = 20, prefetchDistance = 5),
        pagingSourceFactory = { movieDataBaseRepository.getNowPlayingMovies() }
    ).flow.map { pagingData ->
        pagingData.map(transform = NowPlayingMovieEntity::asExternalModel)
    }.cachedIn(scope = viewModelScope)
    val upComingMoviePaging = Pager(
        config = PagingConfig(pageSize = 20, prefetchDistance = 5),
        pagingSourceFactory = { movieDataBaseRepository.getUpComingMovies() }
    ).flow.map { pagingData ->
        pagingData.map(transform = UpComingMovieEntity::asExternalModel)
    }.cachedIn(scope = viewModelScope)
    val trendingMoviePaging =
        createTrendingPaging(
            timeWindowFlow = trendingMovieTimeWindow,
            pagingSourceFactory = pagingRepository::getTrendingMovie
        ).asFlow()
    val trendingPeoplePaging =
        createTrendingPaging(
            timeWindowFlow = trendingPeopleTimeWindow,
            pagingSourceFactory = pagingRepository::getTrendingPeople
        ).asFlow()
    val trendingTvPaging =
        createTrendingPaging(
            timeWindowFlow = trendingTvTimeWindow,
            pagingSourceFactory = pagingRepository::getTrendingTv
        ).asFlow()
    val homeUiState: Observable<HomeState> = movieDataBaseRepository.getPopularMovies()
        .map<HomeState> { HomeState.Success(HomeUiState(popularMovies = it)) }
        .toObservable()
        .startWithItem(HomeState.Loading)
        .onErrorReturn { HomeState.Error(it) }
        .replay(1)
        .refCount()

    fun updateTrendingMovieTimeWindow(timeWindow: TimeWindow) {
        trendingMovieTimeWindow.onNext(timeWindow)
    }

    fun updateTrendingPeopleTimeWindow(timeWindow: TimeWindow) {
        trendingPeopleTimeWindow.onNext(timeWindow)
    }

    fun updateTrendingTvTimeWindow(timeWindow: TimeWindow) {
        trendingTvTimeWindow.onNext(timeWindow)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun <T : Any> createTrendingPaging(
        timeWindowFlow: Observable<TimeWindow>,
        pagingSourceFactory: (timeWindow: String, language: String) -> PagingSource<Int, T>
    ): Flowable<PagingData<T>> {
        return Flowable.combineLatest(
            timeWindowFlow.toFlowable(BackpressureStrategy.LATEST),
            dataManager.localeFlow,
            onlineState
        ) { timeWindow, language, _ ->
            TrendingRequest(timeWindow = timeWindow.label, language = "${language.language}-${language.region}")
        }.distinctUntilChanged()
            .flatMap { request ->
                Pager(
                    config = pagingConfig,
                    pagingSourceFactory = {
                        pagingSourceFactory(request.timeWindow, request.language)
                    }
                ).flowable
            }.cachedIn(scope = viewModelScope)
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