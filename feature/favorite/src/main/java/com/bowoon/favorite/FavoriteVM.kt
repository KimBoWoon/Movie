package com.bowoon.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bowoon.common.Result
import com.bowoon.common.asResult
import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.domain.GetFavoriteMoviesUseCase
import com.bowoon.domain.GetFavoritePeopleUseCase
import com.bowoon.model.MovieDetail
import com.bowoon.model.PeopleDetailData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteVM @Inject constructor(
    getFavoriteMoviesUseCase: GetFavoriteMoviesUseCase,
    getFavoritePeopleUseCase: GetFavoritePeopleUseCase,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {
    companion object {
        private const val TAG = "FavoriteVM"
    }

    val favoriteMovies = getFavoriteMoviesUseCase()
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
    val favoritePeoples = getFavoritePeopleUseCase()
        .asResult()
        .map {
            when (it) {
                is Result.Loading -> FavoritePeoplesState.Loading
                is Result.Success -> FavoritePeoplesState.Success(it.data)
                is Result.Error -> FavoritePeoplesState.Error(it.throwable)
            }
        }.stateIn(
            scope = viewModelScope,
            initialValue = FavoritePeoplesState.Loading,
            started = SharingStarted.WhileSubscribed(5_000)
        )

    fun deleteMovie(movie: MovieDetail) {
        viewModelScope.launch {
            databaseRepository.deleteMovie(movie)
        }
    }

    fun deletePeople(people: PeopleDetailData) {
        viewModelScope.launch {
            databaseRepository.deletePeople(people)
        }
    }
}

sealed interface FavoriteMoviesState {
    data object Loading : FavoriteMoviesState
    data class Success(val data: List<MovieDetail>) : FavoriteMoviesState
    data class Error(val throwable: Throwable) : FavoriteMoviesState
}

sealed interface FavoritePeoplesState {
    data object Loading : FavoritePeoplesState
    data class Success(val data: List<PeopleDetailData>) : FavoritePeoplesState
    data class Error(val throwable: Throwable) : FavoritePeoplesState
}