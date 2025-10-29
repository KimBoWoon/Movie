package com.bowoon.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bowoon.common.Result
import com.bowoon.common.asResult
import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.MainMenu
import com.bowoon.model.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.threeten.bp.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeVM @Inject constructor(
    userDataRepository: UserDataRepository,
    databaseRepository: DatabaseRepository
) : ViewModel() {
    companion object {
        private const val TAG = "HomeVM"
    }

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
            started = SharingStarted.Lazily,
            initialValue = MainMenuState.Loading
        )
    val tomorrowReleaseMovies = databaseRepository.getMovies()
        .map { movies ->
            movies
                .filter { !it.releaseDate?.trim().isNullOrEmpty() }
                .filter { LocalDate.parse(it.releaseDate ?: "").isEqual(LocalDate.now().minusDays(1)) }
        }.asResult()
        .map {
            when (it) {
                is Result.Loading -> TomorrowReleaseMoviesState.Loading
                is Result.Success -> TomorrowReleaseMoviesState.Success(it.data)
                is Result.Error -> TomorrowReleaseMoviesState.Error(it.throwable)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = TomorrowReleaseMoviesState.Loading
        )
    val isShowTomorrowReleaseMovie = mutableStateOf<Boolean>(value = false)
}

sealed interface MainMenuState {
    data object Loading : MainMenuState
    data class Success(val mainMenu: MainMenu) : MainMenuState
    data class Error(val throwable: Throwable) : MainMenuState
}

sealed interface TomorrowReleaseMoviesState {
    data object Loading : TomorrowReleaseMoviesState
    data class Success(val tomorrowReleaseMovies: List<Movie>) : TomorrowReleaseMoviesState
    data class Error(val throwable: Throwable) : TomorrowReleaseMoviesState
}