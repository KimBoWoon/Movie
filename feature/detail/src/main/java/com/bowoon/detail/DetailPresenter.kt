package com.bowoon.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bowoon.common.Dispatcher
import com.bowoon.common.Dispatchers
import com.bowoon.common.Result
import com.bowoon.common.asResult
import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.data.repository.DetailRepository
import com.bowoon.data.repository.PagingRepository
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.InternalData
import com.bowoon.model.Movie
import com.bowoon.model.MovieDetail
import com.bowoon.model.MovieSeries
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.components.ActivityRetainedComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class DetailMovie @Inject constructor(
    private val detailRepository: DetailRepository,
    private val pagingRepository: PagingRepository,
    private val databaseRepository: DatabaseRepository,
    private val userDataRepository: UserDataRepository,
    @Dispatcher(Dispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) {
    private val backgroundScope = CoroutineScope(ioDispatcher)
    private val seriesId = MutableStateFlow<Int?>(null)
    private val userdata = userDataRepository.internalData.stateIn(
        scope = backgroundScope,
        started = SharingStarted.Eagerly,
        initialValue = InternalData()
    )

    fun getMovieDetail(id: Int, scope: CoroutineScope): Flow<MovieDetailState> =
        combine(
            detailRepository.getMovieDetail(id)
                .onEach { movieDetail ->
                    movieDetail.belongsToCollection?.id?.let { seriesId ->
                        this@DetailMovie.seriesId.emit(seriesId)
                    }
                },
            databaseRepository.getMovies(),
            seriesId.flatMapLatest { seriesId ->
                if (seriesId == null) {
                    flowOf(null)
                } else {
                    detailRepository.getMovieSeries(seriesId)
                }
            },
            flowOf(
                Pager(
                    config = PagingConfig(pageSize = 1, initialLoadSize = 1, prefetchDistance = 5),
                    initialKey = 1,
                    pagingSourceFactory = {
                        pagingRepository.getSimilarMovies(
                            id = id,
                            language = "${userdata.value.language}-${userdata.value.region}"
                        )
                    }
                ).flow.cachedIn(scope)
            )
        ) { movieDetail, favoriteMovies, movieSeries, similarMovies ->
            MovieInfo(
                movieDetail = movieDetail.copy(isFavorite = favoriteMovies.find { it.id == movieDetail.id } != null),
                movieSeries = movieSeries,
                similarMovies = similarMovies
            )
        }.asResult()
            .map { result ->
                when (result) {
                    is Result.Loading -> MovieDetailState.Loading
                    is Result.Success -> MovieDetailState.Success(result.data)
                    is Result.Error -> MovieDetailState.Error(result.throwable)
                }
            }
}

class DetailPresenter @AssistedInject constructor(
    @Assisted private val screen: DetailScreen,
    @Assisted private val navigator: Navigator,
    @Assisted("goToMovie") private val goToMovie: (Int) -> Unit,
    @Assisted("goToPeople") private val goToPeople: (Int) -> Unit,
    private val detailMovie: DetailMovie,
    private val databaseRepository: DatabaseRepository
) : Presenter<DetailScreen.State> {
    @Composable
    override fun present(): DetailScreen.State {
        val scope = rememberCoroutineScope()
        val movie by produceRetainedState<MovieDetailState>(MovieDetailState.Loading) {
            detailMovie.getMovieDetail(id = screen.id, scope = scope).collect { value = it }
        }

        return DetailScreen.State(
            state = movie,
            eventSink = { event ->
                when (event) {
                    is DetailScreen.Event.GoToBack -> navigator.pop()
                    is DetailScreen.Event.AddFavorite -> scope.launch { databaseRepository.insertMovie(event.favorite) }
                    is DetailScreen.Event.RemoveFavorite -> scope.launch { databaseRepository.deleteMovie(event.favorite) }
                    is DetailScreen.Event.GoToMovie -> goToMovie(event.id)
                    is DetailScreen.Event.GoToPeople -> goToPeople(event.id)
                }
            }
        )
    }

    @CircuitInject(DetailScreen::class, ActivityRetainedComponent::class)
    @AssistedFactory
    interface Factory {
        fun create(
            screen: DetailScreen,
            navigator: Navigator,
            @Assisted("goToMovie") goToMovie: ((Int) -> Unit) = {},
            @Assisted("goToPeople") goToPeople: ((Int) -> Unit) = {}
        ): DetailPresenter
    }
}

sealed interface MovieDetailState {
    data object Loading : MovieDetailState
    data class Success(val movieInfo: MovieInfo) : MovieDetailState
    data class Error(val throwable: Throwable) : MovieDetailState
}

data class MovieInfo(
    val movieDetail: MovieDetail,
    val movieSeries: MovieSeries?,
    val similarMovies: Flow<PagingData<Movie>>
)