package com.cheeke.surfy.detail.movie

import androidx.compose.ui.util.trace
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.cheeke.surfy.analytics.AnalyticsHelper
import com.cheeke.surfy.analytics.logSelectContent
import com.cheeke.surfy.common.Result
import com.cheeke.surfy.common.asResult
import com.cheeke.surfy.data.repository.MovieDataBaseRepository
import com.cheeke.surfy.data.repository.PagingRepository
import com.cheeke.surfy.data.repository.UserDataRepository
import com.cheeke.surfy.domain.GetMovieDetailUseCase
import com.cheeke.surfy.domain.MovieWithFavorite
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.network.model.SurfyNetworkException
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
    private val movieDataBaseRepository: MovieDataBaseRepository,
    private val pagingRepository: PagingRepository,
    private val userDataRepository: UserDataRepository,
    private val analyticsHelper: AnalyticsHelper
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
                is Result.Success -> {
                    analyticsHelper.logSelectContent(contentType = "movie", media = result.data.movie)
                    MovieState.Success(movie = result.data)
                }
                is Result.Error -> MovieState.Error(throwable = result.throwable as SurfyNetworkException)
            }
        }.stateIn(
            scope = viewModelScope,
            initialValue = MovieState.Loading,
            started = SharingStarted.Lazily
        )
    @OptIn(ExperimentalCoroutinesApi::class)
    val similarMovies = userDataRepository.internalData
        .map { it.language to it.region }
        .flatMapLatest {
            Pager(
                config = PagingConfig(pageSize = 1, initialLoadSize = 1, prefetchDistance = 5),
                initialKey = 1,
                pagingSourceFactory = {
                    pagingRepository.getSimilarMoviePagingSource(
                        id = id,
                        language = it.first,
                        region = it.second
                    )
                }
            ).flow
        }.cachedIn(scope = viewModelScope)
    val isCheatActive = userDataRepository.internalData
        .map { it.isCheatActive }
        .stateIn(
            scope = viewModelScope,
            initialValue = false,
            started = SharingStarted.Lazily
        )
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
            movieDataBaseRepository.insert(media = movie)
        }
    }

    fun deleteMovie(movie: Movie) {
        viewModelScope.launch {
            movieDataBaseRepository.delete(media = movie)
        }
    }
}

sealed interface MovieState {
    data object Loading : MovieState
    data class Success(val movie: MovieWithFavorite) : MovieState
    data class Error(val throwable: SurfyNetworkException) : MovieState
}