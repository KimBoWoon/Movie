package com.bowoon.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bowoon.common.di.ApplicationScope
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
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.components.ActivityRetainedComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class DetailPresenter @AssistedInject constructor(
    @Assisted private val navigator: Navigator,
    @Assisted private val screen: Detail,
    @Assisted("goToMovie") private val goToMovie: (Int) -> Unit,
    @Assisted("goToPeople") private val goToPeople: (Int) -> Unit,
    @ApplicationScope private val appScope: CoroutineScope,
    private val getMovieDetail: GetMovieDetailUseCase,
    private val databaseRepository: DatabaseRepository,
    private val pagingRepository: PagingRepository,
    private val userDataRepository: UserDataRepository,
    private val detailRepository: DetailRepository
) : Presenter<DetailState> {
    private val movieSeries = MutableStateFlow<MovieSeries?>(null)

    @Composable
    override fun present(): DetailState {
        val scope = rememberCoroutineScope()
        val internalData by userDataRepository.internalData.collectAsRetainedState(initial = null)
        val movie by getMovieDetail(screen.id)
            .map<MovieDetail, MovieDetailState> {MovieDetailState.Success(it) }
            .catch { emit(MovieDetailState.Error(it)) }
            .onEach {
                when (it) {
                    is MovieDetailState.Loading, is MovieDetailState.Error -> {}
                    is MovieDetailState.Success -> {
                        it.movieDetail.belongsToCollection?.id?.let { seriesId ->
                            movieSeries.emit(detailRepository.getMovieSeries(seriesId).first())
                        }
                    }
                }
            }.collectAsRetainedState(initial = MovieDetailState.Loading)
        val similarMovies by produceRetainedState<Flow<PagingData<Movie>>>(initialValue = emptyFlow()) {
            value = Pager(
                config = PagingConfig(pageSize = 1, initialLoadSize = 1, prefetchDistance = 5),
                initialKey = 1,
                pagingSourceFactory = {
                    pagingRepository.getSimilarMovies(
                        id = screen.id,
                        language = "${internalData?.language}-${internalData?.region}"
                    )
                }
            ).flow.cachedIn(scope)
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
    val movieDetail: MovieDetailState,
    val movieSeries: Flow<MovieSeries?>,
    val similarMovies: Flow<PagingData<Movie>>,
    val eventSink: (DetailEvent) -> Unit
) : CircuitUiState

sealed interface DetailEvent {
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