package com.bowoon.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bowoon.common.Result
import com.bowoon.common.asResult
import com.bowoon.common.restartableStateIn
import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.data.repository.TMDBRepository
import com.bowoon.detail.navigation.DetailRoute
import com.bowoon.domain.GetMovieDetail
import com.bowoon.model.Movie
import com.bowoon.model.MovieDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailVM @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getMovieDetail: GetMovieDetail,
    private val databaseRepository: DatabaseRepository,
    private val tmdbRepository: TMDBRepository
) : ViewModel() {
    companion object {
        private const val TAG = "DetailVM"
    }

    private val id = savedStateHandle.toRoute<DetailRoute>().id
    val movieInfo = getMovieDetail(id)
        .asResult()
        .map {
            when (it) {
                is Result.Loading -> MovieDetailState.Loading
                is Result.Success -> MovieDetailState.Success(it.data)
                is Result.Error -> MovieDetailState.Error(it.throwable)
            }
        }.restartableStateIn(
            scope = viewModelScope,
            initialValue = MovieDetailState.Loading,
            started = SharingStarted.WhileSubscribed(5_000)
        )
    val similarMovies = MutableStateFlow<PagingData<Movie>>(PagingData.empty())

    fun getSimilarMovies() {
        viewModelScope.launch {
            tmdbRepository.getSimilarMovies(id)
                .cachedIn(viewModelScope)
                .collect {
                    similarMovies.value = it
                }
        }
    }

    fun restart() {
        movieInfo.restart()
    }

    fun insertMovie(movie: MovieDetail) {
        viewModelScope.launch {
            databaseRepository.insertMovie(movie)
        }
    }

    fun deleteMovie(movie: MovieDetail) {
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