package com.bowoon.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.PagingData
import com.bowoon.common.Result
import com.bowoon.common.asResult
import com.bowoon.common.restartableStateIn
import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.detail.navigation.DetailRoute
import com.bowoon.domain.GetMovieDetailUseCase
import com.bowoon.model.Favorite
import com.bowoon.model.Movie
import com.bowoon.model.MovieDetail
import com.bowoon.model.MovieSeries
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailVM @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getMovieDetail: GetMovieDetailUseCase,
    private val databaseRepository: DatabaseRepository,
) : ViewModel() {
    companion object {
        private const val TAG = "DetailVM"
    }

    private val id = savedStateHandle.toRoute<DetailRoute>().id
    val detail = getMovieDetail(id = id, scope = viewModelScope)
        .asResult()
        .map { result ->
            when (result) {
                is Result.Loading -> DetailState.Loading
                is Result.Success -> DetailState.Success(result.data.first, result.data.second, result.data.third)
                is Result.Error -> DetailState.Error(result.throwable)
            }
        }.restartableStateIn(
            scope = viewModelScope,
            initialValue = DetailState.Loading,
            started = SharingStarted.Lazily
        )

    fun restart() {
        detail.restart()
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

sealed interface DetailState {
    data object Loading : DetailState
    data class Success(
        val detail: MovieDetail?,
        val series: MovieSeries?,
        val similarMovies: Flow<PagingData<Movie>>
    ) : DetailState
    data class Error(val throwable: Throwable) : DetailState
}