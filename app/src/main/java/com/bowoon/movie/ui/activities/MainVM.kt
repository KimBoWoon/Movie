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
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class MainVM @Inject constructor(
    dataManager: DataManager,
    databaseRepository: DatabaseRepository,
    syncManager: SyncManager,
    val userDataRepository: UserDataRepository
) : ViewModel() {
    init {
        viewModelScope.launch {
            launch {
                val isFirstInstall = userDataRepository.getFirstInstall()

                if (!isFirstInstall) {
                    syncManager.requestSync()
                    syncManager.syncMain()
                    userDataRepository.updateFirstInstall(value = true)
                }
            }
            launch {
                val isAfter = LocalDateTime.ofInstant(Instant.ofEpochMilli(userDataRepository.getWorkScheduleTime()), ZoneId.systemDefault())
                    .isAfter(LocalDateTime.now().withHour(1).withMinute(0).withSecond(0).withNano(0))

                if (isAfter) {
                    syncManager.updateWorker()
                    userDataRepository.updateWorkScheduleTime(
                        value = LocalDateTime.now()
                            .withHour(0)
                            .withMinute(0)
                            .withSecond(0)
                            .withNano(0)
                            .toInstant(ZoneOffset.from(ZonedDateTime.now()))
                            .toEpochMilli()
                    )
                }
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
        .map {
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
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = Pair(first = emptyList(), second = false)
        )

    fun updateShowNextReleaseMoviesDate() {
        viewModelScope.launch {
            userDataRepository.updateShowNextReleaseMoviesDate(value = LocalDate.now().toString())
        }
    }
}