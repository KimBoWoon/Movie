package com.bowoon.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bowoon.common.Result
import com.bowoon.common.asResult
import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.data.util.SyncManager
import com.bowoon.model.MainMenu
import com.bowoon.model.MovieDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeVM @Inject constructor(
    private val syncManager: SyncManager,
    private val userDataRepository: UserDataRepository,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {
    companion object {
        private const val TAG = "HomeVM"
    }

    val favoriteMovies = databaseRepository.getMovies()
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
    val isSyncing = syncManager.isSyncing
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )
    val mainMenu = userDataRepository.userData
        .map { it.mainMenu }
        .asResult()
        .map {
            when (it) {
                is Result.Loading -> DailyBoxOfficeState.Loading
                is Result.Success -> DailyBoxOfficeState.Success(it.data)
                is Result.Error -> DailyBoxOfficeState.Error(it.throwable)
            }
        }.stateIn(
            scope = viewModelScope,
            initialValue = DailyBoxOfficeState.Loading,
            started = SharingStarted.WhileSubscribed(5_000)
        )
}

sealed interface DailyBoxOfficeState {
    data object Loading : DailyBoxOfficeState
    data class Success(val mainMenu: MainMenu) : DailyBoxOfficeState
    data class Error(val throwable: Throwable) : DailyBoxOfficeState
}

sealed interface FavoriteMoviesState {
    data object Loading : FavoriteMoviesState
    data class Success(val favoriteMovies: List<MovieDetail>) : FavoriteMoviesState
    data class Error(val throwable: Throwable) : FavoriteMoviesState
}