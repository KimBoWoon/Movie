package com.bowoon.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.bowoon.common.Result
import com.bowoon.common.asResult
import com.bowoon.common.restartableStateIn
import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.data.repository.PagingRepository
import com.bowoon.detail.navigation.DetailRoute
import com.bowoon.domain.GetMovieDetailUseCase
import com.bowoon.model.Movie
import com.bowoon.model.MovieDetailInfo
import com.bowoon.model.MovieReview
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailVM @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMovieDetail: GetMovieDetailUseCase,
    private val databaseRepository: DatabaseRepository,
    private val pagingRepository: PagingRepository
) : ViewModel() {
    companion object {
        private const val TAG = "DetailVM"
    }

    private val id = savedStateHandle.toRoute<DetailRoute>().id
    val detail = getMovieDetail(id = id)
        .asResult()
        .map { result ->
            when (result) {
                is Result.Loading -> DetailState.Loading
                is Result.Success -> DetailState.Success(movieInfo = result.data)
                is Result.Error -> DetailState.Error(throwable = result.throwable)
            }
        }.restartableStateIn(
            scope = viewModelScope,
            initialValue = DetailState.Loading,
            started = SharingStarted.Lazily
        )
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
        it.map { ReviewDataModel.Item(review = it) }
            .insertSeparators { before, after ->
                if (before != null && after != null) {
                    ReviewDataModel.Separator
                } else {
                    null
                }
            }
    }.cachedIn(scope = viewModelScope)

    fun restart() {
        detail.restart()
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

sealed interface DetailState {
    data object Loading : DetailState
    data class Success(val movieInfo: MovieDetailInfo) : DetailState
    data class Error(val throwable: Throwable) : DetailState
}

sealed interface ReviewDataModel {
    object Separator : ReviewDataModel
    data class Item(val review: MovieReview) : ReviewDataModel
}