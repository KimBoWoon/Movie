package com.bowoon.detail.movie

import androidx.compose.ui.util.trace
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.bowoon.common.Result
import com.bowoon.common.asResult
import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.data.repository.PagingRepository
import com.bowoon.domain.GetMovieDetailUseCase
import com.bowoon.domain.MovieWithFavorite
import com.bowoon.model.Movie
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = MovieVM.Factory::class)
class MovieVM @AssistedInject constructor(
    @Assisted(value = "id") val id: Int,
    private val getMovieDetail: GetMovieDetailUseCase,
    private val databaseRepository: DatabaseRepository,
    private val pagingRepository: PagingRepository
) : ViewModel() {
    companion object {
        private const val TAG = "MovieVM"
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted(value = "id") id: Int
        ): MovieVM
    }

    private val reload = MutableSharedFlow<Unit>(replay = 1)
    @OptIn(ExperimentalCoroutinesApi::class)
    val movie = reload
        .flatMapLatest {
            trace(sectionName = "GetMovieDetail") { getMovieDetail(id = id) }.asResult()
        }.map { result ->
            when (result) {
                is Result.Loading -> MovieState.Loading
                is Result.Success -> MovieState.Success(movie = result.data)
                is Result.Error -> MovieState.Error(throwable = result.throwable)
            }
        }.stateIn(
            scope = viewModelScope,
            initialValue = MovieState.Loading,
            started = SharingStarted.Lazily
        )

    val similarMovies = Pager(
        config = PagingConfig(pageSize = 1, initialLoadSize = 1, prefetchDistance = 5),
        initialKey = 1,
        pagingSourceFactory = { pagingRepository.getSimilarMoviePagingSource(id = id) }
    ).flow.cachedIn(scope = viewModelScope)
//    val movieReviews = Pager(
//        config = PagingConfig(pageSize = 1, initialLoadSize = 1, prefetchDistance = 5),
//        initialKey = 1,
//        pagingSourceFactory = { pagingRepository.getMovieReviews(movieId = id) }
//    ).flow.map {
//        it.map { review -> ReviewDataModel.Item(review = review) }
//            .insertSeparators { before, after ->
//                if (before != null && after != null) {
//                    ReviewDataModel.Separator
//                } else {
//                    null
//                }
//            }
//    }.cachedIn(scope = viewModelScope)

    init {
        viewModelScope.launch {
            reload.emit(value = Unit)
        }
    }

    fun restart() {
        viewModelScope.launch {
            reload.emit(value = Unit)
        }
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
}

sealed interface MovieState {
    data object Loading : MovieState
    data class Success(val movie: MovieWithFavorite) : MovieState
    data class Error(val throwable: Throwable) : MovieState
}