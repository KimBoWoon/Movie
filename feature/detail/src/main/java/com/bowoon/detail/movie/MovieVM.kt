package com.bowoon.detail.movie

import androidx.compose.ui.util.trace
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.bowoon.common.Log
import com.bowoon.common.Result
import com.bowoon.common.asResult
import com.bowoon.common.restartableStateIn
import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.data.repository.PagingRepository
import com.bowoon.domain.GetMovieDetailUseCase
import com.bowoon.domain.GetRxMovieDetailUseCase
import com.bowoon.model.Movie
import com.bowoon.model.MovieDetailInfo
import com.bowoon.model.MovieReview
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.kotlin.subscribeBy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = MovieVM.Factory::class)
class MovieVM @AssistedInject constructor(
    @Assisted(value = "id") val id: Int,
    @Assisted(value = "initialTabIndex") val initialTabIndex: Int,
    private val getMovieDetail: GetMovieDetailUseCase,
    private val databaseRepository: DatabaseRepository,
    private val pagingRepository: PagingRepository,
    private val getRxMovieDetailUseCase: GetRxMovieDetailUseCase
) : ViewModel() {
    companion object {
        private const val TAG = "MovieVM"
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted(value = "id") id: Int,
            @Assisted(value = "initialTabIndex") initialTabIndex: Int
        ): MovieVM
    }

    val movie = trace("GetMovieDetail") {
        getMovieDetail(id = id)
    }.asResult()
        .map { result ->
            when (result) {
                is Result.Loading -> MovieState.Loading
                is Result.Success -> MovieState.Success(movieInfo = result.data)
                is Result.Error -> MovieState.Error(throwable = result.throwable)
            }
        }.restartableStateIn(
            scope = viewModelScope,
            initialValue = MovieState.Loading,
            started = SharingStarted.Lazily
        )
    val rxMovie = trace("GetRxMovieDetail") {
        getRxMovieDetailUseCase(id = id)
    }.subscribeBy(
        onNext = { Log.d("RxMovieState.Success ->", "${it.detail}") },
        onError = { Log.d("RxMovieState.Error ->", "${it.message}") },
        onComplete = { Log.d("RxMovieState.Complete") }
    )
    private val _tabIndex = MutableStateFlow(value = initialTabIndex)
    val tabIndex = _tabIndex.asStateFlow()
    val similarMovies = Pager(
        config = PagingConfig(pageSize = 1, initialLoadSize = 1, prefetchDistance = 5),
        initialKey = 1,
        pagingSourceFactory = { pagingRepository.getSimilarMoviePagingSource(id = id) }
    ).flow.cachedIn(scope = viewModelScope)
    val movieReviews = Pager(
        config = PagingConfig(pageSize = 1, initialLoadSize = 1, prefetchDistance = 5),
        initialKey = 1,
        pagingSourceFactory = { pagingRepository.getMovieReviews(movieId = id) }
    ).flow.map {
        it.map { review -> ReviewDataModel.Item(review = review) }
            .insertSeparators { before, after ->
                if (before != null && after != null) {
                    ReviewDataModel.Separator
                } else {
                    null
                }
            }
    }.cachedIn(scope = viewModelScope)

    fun restart() {
        movie.restart()
    }

    fun updateTabIndex(index: Int) {
        _tabIndex.value = index
    }

    fun insertMovie(movie: Movie) {
        viewModelScope.launch {
            databaseRepository.insertMovie(movie)
        }
    }

    fun deleteMovie(movie: Movie) {
        viewModelScope.launch {
            databaseRepository.deleteMovie(movie)
        }
    }

    override fun onCleared() {
        getMovieDetail.close(message = "DetailVM is destroy", cause = null)
    }
}

sealed interface MovieState {
    data object Loading : MovieState
    data class Success(val movieInfo: MovieDetailInfo) : MovieState
    data class Error(val throwable: Throwable) : MovieState
}

sealed interface ReviewDataModel {
    object Separator : ReviewDataModel
    data class Item(val review: MovieReview) : ReviewDataModel
}