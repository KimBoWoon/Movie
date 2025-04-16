package com.bowoon.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.data.repository.DetailRepository
import com.bowoon.data.repository.PagingRepository
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.detail.navigation.Detail
import com.bowoon.domain.GetMovieDetailUseCase
import com.bowoon.model.Favorite
import com.bowoon.model.Movie
import com.bowoon.model.MovieDetail
import com.bowoon.model.MovieSeries
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.collectAsRetainedState
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.components.ActivityRetainedComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class DetailMovie @Inject constructor(
    private val detailRepository: DetailRepository
) {
    private val backgroundScope = CoroutineScope(Dispatchers.IO)
    private val _movieDetail = MutableStateFlow<MovieDetail?>(null)
    val movieDetail = _movieDetail.stateIn(
        scope = backgroundScope,
        started = SharingStarted.Lazily,
        initialValue = null
    )
    private val _movieSeries = MutableStateFlow<MovieSeries?>(null)
    val movieSeries = _movieSeries.stateIn(
        scope = backgroundScope,
        started = SharingStarted.Lazily,
        initialValue = null
    )

    fun getMovieDetail(id: Int) {
        backgroundScope.launch {
            detailRepository.getMovieDetail(id)
                .onEach { movieDetail ->
                    movieDetail.belongsToCollection?.id?.let { seriesId ->
                        getMovieSeries(seriesId)
                    }
                }
                .collect { _movieDetail.emit(it) }
        }
    }

    fun getMovieSeries(id: Int) {
        backgroundScope.launch {
            detailRepository.getMovieSeries(id).collect {
                _movieSeries.emit(it)
            }
        }
    }
}

//@Composable
//fun detailPresenter(
//    navigator: Navigator,
//    screen: Detail,
//    goToMovie: (Int) -> Unit,
//    goToPeople: (Int) -> Unit,
//    detailMovie: DetailMovie,
//    databaseRepository: DatabaseRepository,
//    pagingRepository: PagingRepository,
//    userDataRepository: UserDataRepository,
//): DetailState {
//    val scope = rememberCoroutineScope()
//    val internalData by userDataRepository.internalData.collectAsRetainedState(initial = null)
//    val movie by detailMovie.movieDetail.collectAsRetainedState(null)
//    val movieSeries by detailMovie.movieSeries.collectAsRetainedState()
//    val similarMovies = Pager(
//        config = PagingConfig(pageSize = 1, initialLoadSize = 1, prefetchDistance = 5),
//        initialKey = 1,
//        pagingSourceFactory = {
//            pagingRepository.getSimilarMovies(
//                id = screen.id,
//                language = "${internalData?.language}-${internalData?.region}"
//            )
//        }
//    ).flow.cachedIn(scope)
//
//    LaunchedEffect(key1 = screen.id) {
//        detailMovie.getMovieDetail(screen.id)
//    }
//
//    return DetailState(
//        movieDetail = movie,
//        movieSeries = movieSeries,
//        similarMovies = similarMovies
//    ) { event ->
//        when (event) {
//            is DetailEvent.GoToBack -> navigator.pop()
//            is DetailEvent.AddFavorite -> scope.launch { databaseRepository.insertMovie(event.favorite) }
//            is DetailEvent.RemoveFavorite -> scope.launch { databaseRepository.deleteMovie(event.favorite) }
//            is DetailEvent.GoToMovie -> goToMovie(event.id)
//            is DetailEvent.GoToPeople -> goToPeople(event.id)
//        }
//    }
//}

class DetailPresenter @AssistedInject constructor(
    @Assisted private val navigator: Navigator,
    @Assisted private val screen: Detail,
    @Assisted("goToMovie") private val goToMovie: (Int) -> Unit,
    @Assisted("goToPeople") private val goToPeople: (Int) -> Unit,
    private val getMovieDetail: GetMovieDetailUseCase,
    private val databaseRepository: DatabaseRepository,
    private val pagingRepository: PagingRepository,
    private val userDataRepository: UserDataRepository,
//    private val detailRepository: DetailRepository
    private val detailMovie: DetailMovie
) : Presenter<DetailState> {
    @Composable
    override fun present(): DetailState {
        val scope = rememberCoroutineScope()
        val internalData by userDataRepository.internalData.collectAsRetainedState(initial = null)
//        val movie by detailRepository.getMovieDetail(screen.id)
//            .map<MovieDetail, MovieDetailState> { movieDetail ->
//                movieDetail.belongsToCollection?.id?.let { seriesId ->
//                    movieSeries.emit(detailRepository.getMovieSeries(seriesId).first())
//                }
//                MovieDetailState.Success(movieDetail)
//            }
//            .catch { e -> emit(MovieDetailState.Error(e)) }
//            .collectAsRetainedState(initial = MovieDetailState.Loading)
        val movie by detailMovie.movieDetail.collectAsRetainedState()
        val movieSeries by detailMovie.movieSeries.collectAsRetainedState()
        val similarMovies = Pager(
            config = PagingConfig(pageSize = 1, initialLoadSize = 1, prefetchDistance = 5),
            initialKey = 1,
            pagingSourceFactory = {
                pagingRepository.getSimilarMovies(
                    id = screen.id,
                    language = "${internalData?.language}-${internalData?.region}"
                )
            }
        ).flow.cachedIn(scope)

        LaunchedEffect(key1 = detailMovie) {
            detailMovie.getMovieDetail(screen.id)
        }

        return DetailState(
            movieDetail = movie,
            movieSeries = movieSeries,
            similarMovies = similarMovies
        ) { event ->
            when (event) {
                is DetailEvent.GoToBack -> navigator.pop()
                is DetailEvent.AddFavorite -> scope.launch { databaseRepository.insertMovie(event.favorite) }
                is DetailEvent.RemoveFavorite -> scope.launch { databaseRepository.deleteMovie(event.favorite) }
                is DetailEvent.GoToMovie -> goToMovie(event.id)
                is DetailEvent.GoToPeople -> goToPeople(event.id)
            }
        }
    }

    @CircuitInject(Detail::class, ActivityRetainedComponent::class)
    @AssistedFactory
    interface Factory {
        fun create(
            navigator: Navigator,
            screen: Detail,
            @Assisted("goToMovie") goToMovie: ((Int) -> Unit) = {},
            @Assisted("goToPeople") goToPeople: ((Int) -> Unit) = {}
        ): DetailPresenter
    }
}

class DetailState(
    val movieDetail: MovieDetail?,
    val movieSeries: MovieSeries?,
    val similarMovies: Flow<PagingData<Movie>>,
    val eventSink: (DetailEvent) -> Unit
) : CircuitUiState

sealed interface DetailEvent : CircuitUiEvent {
    data object GoToBack : DetailEvent
    data class GoToMovie(val id: Int) : DetailEvent
    data class GoToPeople(val id: Int) : DetailEvent
    data class AddFavorite(val favorite: Favorite) : DetailEvent
    data class RemoveFavorite(val favorite: Favorite) : DetailEvent
}

sealed interface MovieDetailState {
    data object Loading : MovieDetailState
    data class Success(val movieDetail: MovieDetail) : MovieDetailState
    data class Error(val throwable: Throwable) : MovieDetailState
}