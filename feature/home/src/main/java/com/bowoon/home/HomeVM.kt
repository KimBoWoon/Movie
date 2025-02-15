package com.bowoon.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bowoon.common.Result
import com.bowoon.common.asResult
import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.data.util.SyncManager
import com.bowoon.model.MainMenu
import com.bowoon.model.Movie
import com.bowoon.model.MovieDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.threeten.bp.LocalDate
import org.threeten.bp.Period
import javax.inject.Inject

@HiltViewModel
class HomeVM @Inject constructor(
    savedStateHandle: SavedStateHandle,
    syncManager: SyncManager,
    userDataRepository: UserDataRepository,
    databaseRepository: DatabaseRepository
) : ViewModel() {
    companion object {
        private const val TAG = "HomeVM"
    }

    var isShowReleaseMoviesDialog by mutableStateOf(savedStateHandle.get<Boolean>("isShowReleaseMoviesDialog") ?: true)
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
    val mainMenu = userDataRepository.internalData
        .map { it.mainMenu }
        .asResult()
        .map {
            when (it) {
                is Result.Loading -> MainMenuState.Loading
                is Result.Success -> MainMenuState.Success(it.data)
                is Result.Error -> MainMenuState.Error(it.throwable)
            }
        }.stateIn(
            scope = viewModelScope,
            initialValue = MainMenuState.Loading,
            started = SharingStarted.WhileSubscribed(5_000)
        )

    fun getReleaseMovies(): Flow<List<Movie>> =
        favoriteMovies.map { state ->
            when (state) {
                is FavoriteMoviesState.Loading -> emptyList()
                is FavoriteMoviesState.Success -> state.favoriteMovies
                    .filter {
                        !it.releaseDate.isNullOrEmpty() &&
                                Period.between(
                                    LocalDate.now(),
                                    LocalDate.parse(it.releaseDate)
                                ).days in 0..1
                    }
                    .map { movieDetail ->
                        Movie(
                            id = movieDetail.id,
                            title = movieDetail.title,
                            posterPath = movieDetail.posterPath,
                            releaseDate = movieDetail.releaseDate
                        )
                    }
                is FavoriteMoviesState.Error -> emptyList()
            }
        }
}

sealed interface MainMenuState {
    data object Loading : MainMenuState
    data class Success(val mainMenu: MainMenu) : MainMenuState
    data class Error(val throwable: Throwable) : MainMenuState
}

sealed interface FavoriteMoviesState {
    data object Loading : FavoriteMoviesState
    data class Success(val favoriteMovies: List<MovieDetail>) : FavoriteMoviesState
    data class Error(val throwable: Throwable) : FavoriteMoviesState
}