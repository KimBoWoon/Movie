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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MainVM @Inject constructor(
    private val userDataRepository: UserDataRepository,
    dataManager: DataManager,
    databaseRepository: DatabaseRepository,
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
        .onEach {
            val url = it.getMovieAppData().getImageUrl()

            if (url.startsWith(prefix = "http://") || url.startsWith(prefix = "https://")) {
                imageUrl = url
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = MovieAppDataState.Loading
        )
    val nextWeekReleaseMedia = flow {
        val nextWeekReleaseMovies = databaseRepository.getNextWeekReleaseMovies().firstOrNull() ?: emptyList()
        val nextWeekReleaseTvs = databaseRepository.getNextWeekReleaseTvs().firstOrNull() ?: emptyList()
        emit(value = nextWeekReleaseMovies + nextWeekReleaseTvs)
    }.map {
        val showNextReleaseMoviesDate = userDataRepository.getShowNextReleaseMoviesDate().let { showNextReleaseMoviesDate ->
            if (showNextReleaseMoviesDate.isEmpty()) {
                false
            } else {
                !LocalDate.parse(showNextReleaseMoviesDate).isBefore(LocalDate.now())
            }
        }

        if (it.isEmpty() || showNextReleaseMoviesDate) {
            it to false
        } else {
            it to true
        }
    }

    fun updateShowNextReleaseMoviesDate() {
        viewModelScope.launch {
            userDataRepository.updateShowNextReleaseMoviesDate(value = LocalDate.now().toString())
        }
    }
}