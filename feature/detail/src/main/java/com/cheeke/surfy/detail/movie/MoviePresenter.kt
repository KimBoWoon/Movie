package com.cheeke.surfy.detail.movie

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.util.trace
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.cheeke.surfy.analytics.AnalyticsHelper
import com.cheeke.surfy.analytics.logSelectContent
import com.cheeke.surfy.common.Log
import com.cheeke.surfy.common.Result
import com.cheeke.surfy.common.asResult
import com.cheeke.surfy.common.di.ActivityRetainedScopeCoroutine
import com.cheeke.surfy.data.repository.MovieDataBaseRepository
import com.cheeke.surfy.data.repository.PagingRepository
import com.cheeke.surfy.data.repository.UserDataRepository
import com.cheeke.surfy.domain.GetMovieDetailUseCase
import com.cheeke.surfy.feature.detail.R
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.model.SimilarMedia
import com.cheeke.surfy.navigation.MovieScreen
import com.cheeke.surfy.navigation.goToMovie
import com.cheeke.surfy.navigation.goToPeople
import com.cheeke.surfy.navigation.goToSeries
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.components.ActivityRetainedComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MovieRepository @AssistedInject constructor(
    @Assisted private val id: Int,
    @param:ActivityRetainedScopeCoroutine private val scope: CoroutineScope,
    private val getMovieDetail: GetMovieDetailUseCase,
    private val movieDataBaseRepository: MovieDataBaseRepository,
    private val pagingRepository: PagingRepository,
    private val userDataRepository: UserDataRepository,
    private val analyticsHelper: AnalyticsHelper
) {
    @AssistedFactory
    interface Factory {
        fun create(id: Int): MovieRepository
    }

    companion object {
        private const val TAG = "MovieRepository"
    }

    val isCheatActive = userDataRepository.internalData
        .map { it.isCheatActive }
        .stateIn(
            scope = scope,
            initialValue = false,
            started = SharingStarted.Lazily
        )
    private val reload = MutableSharedFlow<Unit>(replay = 1)
    @OptIn(ExperimentalCoroutinesApi::class)
    val movieState = combine(
        reload.flatMapLatest {
            trace(sectionName = "GetMovieDetail") { getMovieDetail(id = id) }.asResult()
        },
        movieDataBaseRepository.isFavorite(id = id),
        userDataRepository.internalData
    ) { movie, isFavorite, internalData ->
        Triple(movie, isFavorite, internalData.isAutoPlayTrailer)
    }.map { (result, isFavorite, isAutoPlayTrailer) ->
        when (result) {
            is Result.Loading -> MovieStatus.Loading
            is Result.Success -> {
                analyticsHelper.logSelectContent(contentType = "movie", media = result.data)
                MovieStatus.Success(
                    movie = MovieUiState(
                        movie = result.data,
                        isFavorite = isFavorite,
                        autoPlayTrailer = isAutoPlayTrailer
                    ),
                    isCheatActive = isCheatActive.value
                )
            }
            is Result.Error -> MovieStatus.Error(throwable = result.throwable)
        }
    }.stateIn(
        scope = scope,
        initialValue = MovieStatus.Loading,
        started = SharingStarted.Lazily
    )
    @OptIn(ExperimentalCoroutinesApi::class)
    val similarMovies = userDataRepository.internalData
        .map { it.language to it.region }
        .distinctUntilChanged()
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
        }.cachedIn(scope = scope)
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
        scope.launch {
            reload.emit(value = Unit)
        }
    }

    fun restart() {
        scope.launch {
            reload.emit(value = Unit)
        }
    }

    fun insertMovie(movie: Movie) {
        scope.launch {
            movieDataBaseRepository.insert(media = movie)
        }
    }

    fun deleteMovie(movie: Movie) {
        scope.launch {
            movieDataBaseRepository.delete(media = movie)
        }
    }
}

class MoviePresenter @AssistedInject constructor(
    @Assisted private val screen: MovieScreen,
    @Assisted private val navigator: Navigator,
    private val movieRepositoryFactory: MovieRepository.Factory
) : Presenter<MovieState> {
    @Composable
    override fun present(): MovieState {
        val effectFlow = remember { MutableSharedFlow<MovieEffect>() }
        val scope = rememberCoroutineScope()
        val movieRepository = rememberRetained(screen.id) {
            movieRepositoryFactory.create(screen.id)
        }
        val movieState by movieRepository.movieState.collectAsStateWithLifecycle()
        val similarMovies = movieRepository.similarMovies.collectAsLazyPagingItems()
        val insertFavoriteMessage = stringResource(id = R.string.insert_favorite_movie)
        val deleteFavoriteMessage = stringResource(id = R.string.delete_favorite_movie)

        return MovieState(
            movie = movieState,
            similarMovies = similarMovies,
            effect = effectFlow
        ) { event ->
            Log.d("MoviePresenter", "$event")
            when (event) {
                is MovieEvent.DeleteFavoriteMovie -> {
                    movieRepository.deleteMovie(movie = event.movie)
                    scope.launch {
                        effectFlow.emit(value = MovieEffect.ShowSnackbar(deleteFavoriteMessage))
                    }
                }
                is MovieEvent.GoToMovie -> navigator.goToMovie(id = event.id)
                is MovieEvent.GoToPeople -> navigator.goToPeople(id = event.id)
                is MovieEvent.InsertFavoriteMovie -> {
                    movieRepository.insertMovie(movie = event.movie)
                    scope.launch {
                        effectFlow.emit(value = MovieEffect.ShowSnackbar(insertFavoriteMessage))
                    }
                }
                is MovieEvent.Restart -> movieRepository.restart()
                is MovieEvent.GoToBack -> navigator.pop()
                is MovieEvent.GoToSeries -> navigator.goToSeries(id = event.id)
            }
        }
    }

    @CircuitInject(screen = MovieScreen::class, scope = ActivityRetainedComponent::class)
    @AssistedFactory
    interface Factory {
        fun create(
            screen: MovieScreen,
            navigator: Navigator
        ): MoviePresenter
    }
}

data class MovieUiState(
    val movie: Movie,
    val isFavorite: Boolean,
    val autoPlayTrailer: Boolean,
)

data class MovieState(
    val movie: MovieStatus,
    val similarMovies: LazyPagingItems<SimilarMedia>,
    val effect: Flow<MovieEffect>,
    val eventSink: (MovieEvent) -> Unit
) : CircuitUiState

sealed interface MovieEvent : CircuitUiEvent {
    object GoToBack : MovieEvent
    object Restart : MovieEvent
    data class GoToMovie(val id: Int) : MovieEvent
    data class GoToPeople(val id: Int) : MovieEvent
    data class GoToSeries(val id: Int) : MovieEvent
    data class InsertFavoriteMovie(val movie: Movie) : MovieEvent
    data class DeleteFavoriteMovie(val movie: Movie) : MovieEvent
}

sealed interface MovieEffect {
    data class ShowSnackbar(
        val message: String
    ) : MovieEffect
}

sealed interface MovieStatus {
    data object Loading : MovieStatus
    data class Success(val movie: MovieUiState, val isCheatActive: Boolean) : MovieStatus
    data class Error(val throwable: Throwable) : MovieStatus
}