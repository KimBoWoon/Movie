package com.bowoon.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bowoon.common.Result
import com.bowoon.common.asResult
import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.MainMenu
import com.bowoon.notifications.SystemTrayNotifier
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeVM @Inject constructor(
    databaseRepository: DatabaseRepository,
    private val userDataRepository: UserDataRepository,
    private val notifier: SystemTrayNotifier
) : ViewModel() {
    companion object {
        private const val TAG = "HomeVM"
    }

    val mainMenu = userDataRepository.internalData
        .map { internalData ->
            val isShowToday = internalData.noShowToday.isEmpty() || LocalDate.parse(internalData.noShowToday).isBefore(LocalDate.now())

            if (!isShowNextWeekReleaseMovie.value) {
                isShowNextWeekReleaseMovie.value = !isShowToday
            }

            MainMenu(
                nowPlayingMovies = internalData.mainMenu.nowPlayingMovies,
                upComingMovies = internalData.mainMenu.upComingMovies,
                nextWeekReleaseMovies = databaseRepository.getNextWeekReleaseMovies()
            )
        }.onEach {
            if (it.nextWeekReleaseMovies.isEmpty()) {
                isShowNextWeekReleaseMovie.value = true
            }
            notifier.postMovieNotifications(it.nextWeekReleaseMovies)
        }.asResult()
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
    val isShowNextWeekReleaseMovie = mutableStateOf(value = false)

    fun onNoShowToday() {
        viewModelScope.launch {
            userDataRepository.updateUserData(
                userData = userDataRepository.internalData.first().copy(noShowToday = LocalDate.now().toString()),
                isSync = false
            )
        }
    }
}

sealed interface MainMenuState {
    data object Loading : MainMenuState
    data class Success(val mainMenu: MainMenu) : MainMenuState
    data class Error(val throwable: Throwable) : MainMenuState
}