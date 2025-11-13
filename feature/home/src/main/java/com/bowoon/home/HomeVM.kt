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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
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

    val mainMenu = combine(
        userDataRepository.internalData,
        databaseRepository.getMovies()
    ) { internalData, nextWeekReleaseMovies ->
        internalData.mainMenu to nextWeekReleaseMovies.filter { movie ->
            val now = LocalDate.now()
            !movie.releaseDate?.trim().isNullOrEmpty() && LocalDate.parse(movie.releaseDate ?: "") in (now..now.plusDays(7))
        }.sortedWith(comparator = compareBy({ movie -> movie.releaseDate }, { movie -> movie.title }))
    }.onEach {
        if (it.second.isEmpty()) {
            isShowNextWeekReleaseMovie.value = true
        }
    }.asResult()
        .map {
            when (it) {
                is Result.Loading -> MainMenuState.Loading
                is Result.Success -> MainMenuState.Success(it.data.first, it.data.second)
                is Result.Error -> MainMenuState.Error(it.throwable)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = MainMenuState.Loading
        )
    val isShowNextWeekReleaseMovie = mutableStateOf<Boolean>(value = false)
}

sealed interface MainMenuState {
    data object Loading : MainMenuState
    data class Success(val mainMenu: MainMenu, val nextWeekReleaseMovies: List<Movie>) : MainMenuState
    data class Error(val throwable: Throwable) : MainMenuState
}