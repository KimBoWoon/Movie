package com.bowoon.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bowoon.common.Result
import com.bowoon.common.asResult
import com.bowoon.data.repository.TMDBRepository
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.MovieDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteVM @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val tmdbRepository: TMDBRepository
) : ViewModel() {
    companion object {
        private const val TAG = "FavoriteVM"
    }

    val favoriteMovies = userDataRepository.userData
        .map { it.favoriteMovies }
        .asResult()
        .map {
            when (it) {
                is Result.Loading -> FavoriteMoviesState.Loading
                is Result.Success -> FavoriteMoviesState.Success(it.data)
                is Result.Error -> FavoriteMoviesState.Error(it.throwable)
            }
        }.stateIn(
            scope = viewModelScope,
            initialValue = FavoriteMoviesState.Loading,
            started = SharingStarted.WhileSubscribed(5_000)
        )

    fun updateFavoriteMovies(movie: MovieDetail) {
        viewModelScope.launch {
            userDataRepository.updateFavoriteMovies(movie)
        }
    }
}

sealed interface FavoriteMoviesState {
    data object Loading : FavoriteMoviesState
    data class Success(val data: List<MovieDetail>) : FavoriteMoviesState
    data class Error(val throwable: Throwable) : FavoriteMoviesState
}