package com.bowoon.movie.ui.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.data.util.DataManager
import com.bowoon.data.util.MovieAppDataState
import com.bowoon.data.util.SyncManager
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
    syncManager: SyncManager
) : ViewModel() {
    init {
        viewModelScope.launch {
            val isFirstInstall = userDataRepository.getFirstInstall()

            if (!isFirstInstall) {
                syncManager.requestSync()
                syncManager.syncMain()
                userDataRepository.updateFirstInstall(value = true)
            }
        }
    }

    val movieAppData = dataManager.movieAppData
        .onEach { imageUrl = it.getMovieAppData().getImageUrl() }
        .stateIn(
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