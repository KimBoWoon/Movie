package com.bowoon.movie.ui.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bowoon.common.Result
import com.bowoon.common.asResult
import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.data.util.DataManager
import com.bowoon.data.util.SyncManager
import com.bowoon.model.DarkThemeConfig
import com.bowoon.model.MovieAppData
import com.bowoon.ui.image.imageUrl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainVM @Inject constructor(
    dataManager: DataManager,
    databaseRepository: DatabaseRepository,
    userDataRepository: UserDataRepository,
    syncManager: SyncManager,
//    notifier: Notifier
) : ViewModel() {
    init {
//        notifier.postNotification("test deeplink")
        viewModelScope.launch {
            val isFirstInstall = userDataRepository.getFirstInstall()

            if (!isFirstInstall) {
                syncManager.requestSync()
                syncManager.syncMain()
                userDataRepository.updateFirstInstall(value = true)
            }
//            notifier.postMovieNotifications(movies = databaseRepository.getNextWeekReleaseMovies().first())
        }
    }

    val movieAppData = dataManager.movieAppData
        .onEach { imageUrl = it.getImageUrl() }
        .asResult()
        .map {
            when (it) {
                is Result.Loading -> MovieAppDataState.Loading
                is Result.Success -> MovieAppDataState.Success(data = it.data)
                is Result.Error -> MovieAppDataState.Error(throwable = it.throwable)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = MovieAppDataState.Loading
        )
    val nextWeekReleaseMovies = databaseRepository.getNextWeekReleaseMovies()
        .map { movies ->
            movies.filter { movie -> movie.id != null }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )
}

sealed interface MovieAppDataState {
    data object Loading : MovieAppDataState
    data class Success(val data: MovieAppData) : MovieAppDataState {
        override fun shouldUseDarkTheme(isSystemDarkTheme: Boolean) =
            when (data.isDarkMode) {
                DarkThemeConfig.FOLLOW_SYSTEM -> isSystemDarkTheme
                DarkThemeConfig.LIGHT -> false
                DarkThemeConfig.DARK -> true
            }

        override fun getMovieAppData(): MovieAppData = this.data
    }
    data class Error(val throwable: Throwable) : MovieAppDataState

    fun shouldKeepSplashScreen() = this is Loading
    fun shouldUseDarkTheme(isSystemDarkTheme: Boolean): Boolean = isSystemDarkTheme
    fun getMovieAppData(): MovieAppData = MovieAppData()
}