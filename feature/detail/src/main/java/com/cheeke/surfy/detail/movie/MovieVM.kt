package com.cheeke.surfy.detail.movie

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.util.trace
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.cheeke.surfy.analytics.AnalyticsHelper
import com.cheeke.surfy.analytics.logSelectContent
import com.cheeke.surfy.common.Log
import com.cheeke.surfy.common.Result
import com.cheeke.surfy.common.asResult
import com.cheeke.surfy.common.di.ActivityRetainedScopeCoroutine
import com.cheeke.surfy.data.repository.DatabaseRepository
import com.cheeke.surfy.data.repository.PagingRepository
import com.cheeke.surfy.data.repository.UserDataRepository
import com.cheeke.surfy.detail.movie.navigation.MovieScreen
import com.cheeke.surfy.domain.GetMovieDetailUseCase
import com.cheeke.surfy.domain.MovieWithFavorite
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.model.SimilarMedia
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@ActivityRetainedScoped
class MovieRepository @Inject constructor(
    @param:ActivityRetainedScopeCoroutine private val scope: CoroutineScope,
    private val getMovieDetail: GetMovieDetailUseCase,
    private val databaseRepository: DatabaseRepository,
    private val pagingRepository: PagingRepository,
    private val userDataRepository: UserDataRepository,
    private val analyticsHelper: AnalyticsHelper
) {
    companion object {
        private const val TAG = "MovieRepository"
    }

    private val reload = MutableSharedFlow<Unit>(replay = 1)
    val isCheatActive = userDataRepository.internalData
        .map { it.isCheatActive }
        .stateIn(
            scope = scope,
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
        scope.launch {
            reload.emit(value = Unit)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getMovie(id: Int): StateFlow<MovieState> {
        return reload
            .flatMapLatest {
                trace(sectionName = "GetMovieDetail") { getMovieDetail(id = id) }.asResult()
            }.map { result ->
                when (result) {
                    is Result.Loading -> MovieState.Loading
                    is Result.Success -> {
                        analyticsHelper.logSelectContent(contentType = "movie", media = result.data.movie)
                        MovieState.Success(movie = result.data)
                    }
                    is Result.Error -> MovieState.Error(throwable = result.throwable)
                }
            }.stateIn(
                scope = scope,
                initialValue = MovieState.Loading,
                started = SharingStarted.Lazily
            )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getSimilarMovie(id: Int): Flow<PagingData<SimilarMedia>> {
        return userDataRepository.internalData
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
                ).flow.cachedIn(scope = scope)
            }
    }

    fun restart() {
        scope.launch {
            reload.emit(value = Unit)
        }
    }

    fun insertMovie(movie: Movie) {
        scope.launch {
            databaseRepository.insertMovie(movie)
        }
    }

    fun deleteMovie(movie: Movie) {
        scope.launch {
            databaseRepository.deleteMovie(movie)
        }
    }
}

class MoviePresenter @AssistedInject constructor(
    @Assisted(value = "navigator") private val navigator: Navigator,
    @Assisted(value = "screen") private val screen: MovieScreen,
    @Assisted(value = "goToMovie") private val goToMovie: (Int) -> Unit,
    @Assisted(value = "goToPeople") private val goToPeople: (Int) -> Unit,
    @Assisted(value = "goToSeries") private val goToSeries: (Int) -> Unit,
    private val movieRepository: MovieRepository
) : Presenter<MovieUiState> {
    @Composable
    override fun present(): MovieUiState {
        val movie by produceRetainedState<MovieState>(initialValue = MovieState.Loading) {
            movieRepository.getMovie(id = screen.id).collect { movieState ->
                value = movieState
            }
        }
        val similarMovies = movieRepository.getSimilarMovie(id = screen.id).collectAsLazyPagingItems()
        val isCheatActive by movieRepository.isCheatActive.collectAsStateWithLifecycle()

        return MovieUiState(
            movie = movie,
            similarMovies = similarMovies,
            isCheatActive = isCheatActive
        ) { event ->
            Log.d("HomePresenter", "$event")
            when (event) {
                is MovieEvent.DeleteFavoriteMovie -> movieRepository.deleteMovie(movie = event.movie)
                is MovieEvent.GoToMovie -> goToMovie(event.id)
                is MovieEvent.GoToPeople -> goToPeople(event.id)
                is MovieEvent.InsertFavoriteMovie -> movieRepository.insertMovie(movie = event.movie)
                is MovieEvent.Restart -> movieRepository.restart()
                is MovieEvent.GoToBack -> navigator.pop()
                is MovieEvent.GoToSeries -> goToSeries(event.id)
            }
        }
    }

    @CircuitInject(screen = MovieScreen::class, scope = ActivityRetainedComponent::class)
    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted(value = "navigator") navigator: Navigator,
            @Assisted(value = "screen") screen: MovieScreen,
            @Assisted(value = "goToMovie") goToMovie: ((Int) -> Unit) = {},
            @Assisted(value = "goToPeople") goToPeople: ((Int) -> Unit) = {},
            @Assisted(value = "goToSeries") goToSeries: ((Int) -> Unit) = {}
        ): MoviePresenter
    }
}

data class MovieUiState(
    val movie: MovieState,
    val similarMovies: LazyPagingItems<SimilarMedia>,
    val isCheatActive: Boolean,
    val eventSink: (MovieEvent) -> Unit
) : CircuitUiState

sealed interface MovieEvent {
    object GoToBack : MovieEvent
    object Restart : MovieEvent
    data class GoToMovie(val id: Int) : MovieEvent
    data class GoToPeople(val id: Int) : MovieEvent
    data class GoToSeries(val id: Int) : MovieEvent
    data class InsertFavoriteMovie(val movie: Movie) : MovieEvent
    data class DeleteFavoriteMovie(val movie: Movie) : MovieEvent
}

sealed interface MovieState {
    data object Loading : MovieState
    data class Success(val movie: MovieWithFavorite) : MovieState
    data class Error(val throwable: Throwable) : MovieState
}