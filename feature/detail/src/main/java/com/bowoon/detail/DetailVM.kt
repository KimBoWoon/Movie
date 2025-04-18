package com.bowoon.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bowoon.common.Result
import com.bowoon.common.asResult
import com.bowoon.common.restartableStateIn
import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.data.repository.DetailRepository
import com.bowoon.data.repository.PagingRepository
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.detail.navigation.DetailRoute
import com.bowoon.domain.GetMovieDetailUseCase
import com.bowoon.model.Favorite
import com.bowoon.model.Movie
import com.bowoon.model.MovieDetail
import com.bowoon.model.MovieSeries
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailVM @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getMovieDetail: GetMovieDetailUseCase,
    userDataRepository: UserDataRepository,
    private val databaseRepository: DatabaseRepository,
    private val pagingRepository: PagingRepository,
    private val detailRepository: DetailRepository
) : ViewModel() {
    companion object {
        private const val TAG = "DetailVM"
    }

    private val internalData = userDataRepository.internalData
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )
    private val id = savedStateHandle.toRoute<DetailRoute>().id
    val movieInfo = getMovieDetail(id)
        .asResult()
        .onEach { state ->
            when (state) {
                is Result.Loading, is Result.Error -> {}
                is Result.Success -> {
                    state.data.belongsToCollection?.id?.let { seriesId ->
                        _movieSeries.emit(detailRepository.getMovieSeries(seriesId).first())
                    }
                    getSimilarMovies()
                }
            }
        }
        .map {
            when (it) {
                is Result.Loading -> MovieDetailState.Loading
                is Result.Success -> MovieDetailState.Success(it.data)
                is Result.Error -> MovieDetailState.Error(it.throwable)
            }
        }.restartableStateIn(
            scope = viewModelScope,
            initialValue = MovieDetailState.Loading,
            started = SharingStarted.Lazily
        )
    val similarMovies = MutableStateFlow<PagingData<Movie>>(PagingData.empty())
    private val _movieSeries = MutableStateFlow<MovieSeries?>(null)
    val movieSeries = _movieSeries.stateIn(
        scope = viewModelScope,
        initialValue = null,
        started = SharingStarted.Lazily
    )

    private fun getSimilarMovies() {
        viewModelScope.launch {
            Pager(
                config = PagingConfig(pageSize = 1, initialLoadSize = 1, prefetchDistance = 5),
                initialKey = 1,
                pagingSourceFactory = {
                    pagingRepository.getSimilarMovies(
                        id = id,
                        language = "${internalData.value?.language}-${internalData.value?.region}"
                    )
                }
            ).flow.cachedIn(viewModelScope).collect {
                similarMovies.emit(it)
            }
        }
    }

    fun restart() {
        movieInfo.restart()
    }

    fun insertMovie(movie: Favorite) {
        viewModelScope.launch {
            databaseRepository.insertMovie(movie)
        }
    }

    fun deleteMovie(movie: Favorite) {
        viewModelScope.launch {
            databaseRepository.deleteMovie(movie)
        }
    }
}

sealed interface MovieDetailState {
    data object Loading : MovieDetailState
    data class Success(val movieDetail: MovieDetail) : MovieDetailState
    data class Error(val throwable: Throwable) : MovieDetailState
}