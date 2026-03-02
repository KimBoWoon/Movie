package com.bowoon.movie.ui.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.data.util.DataManager
import com.bowoon.data.util.MovieAppDataState
import com.bowoon.data.util.SyncManager
import com.bowoon.model.Media
import com.bowoon.ui.image.imageUrl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MainVM @Inject constructor(
    dataManager: DataManager,
    syncManager: SyncManager,
    private val userDataRepository: UserDataRepository,
    private val databaseRepository: DatabaseRepository
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
                val lastUpdateMainDate = userDataRepository.getMainDate()

                if (LocalDate.parse(lastUpdateMainDate).plusDays(1) < LocalDate.now()) {
                    syncManager.requestSync()
                }
            }

            launch {
                sessionNextWeekReleaseSnapshot.emit(
                    value = combine(
                        flow = databaseRepository.getNextWeekReleaseMovies(),
                        flow2 = databaseRepository.getNextWeekReleaseTvs()
                    ) { movies: List<Media>, tvs: List<Media> ->
                        movies + tvs
                    }.first()
                )
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
    private val sessionNextWeekReleaseSnapshot: MutableStateFlow<List<Media>> = MutableStateFlow(value = emptyList())
    private val dismissedThisSession: MutableStateFlow<Boolean> = MutableStateFlow(value = false)
    private val hiddenToday: StateFlow<Boolean> = flow {
        emit(value = userDataRepository.getShowNextReleaseMoviesDate())
    }.map { stored ->
        stored == LocalDate.now().toString()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = false
    )
    val shouldShowNextWeekReleaseDialog: StateFlow<Boolean> =
        combine(
            flow = sessionNextWeekReleaseSnapshot,
            flow2 = hiddenToday,
            flow3 = dismissedThisSession
        ) { snapshot: List<Media>?, hidden: Boolean, dismissed: Boolean ->
            !snapshot.isNullOrEmpty() && !hidden && !dismissed
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = false
        )
    val nextWeekReleaseDialogItems: StateFlow<List<Media>> =
        sessionNextWeekReleaseSnapshot
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = emptyList()
            )

    fun dismissNextWeekReleaseDialog() {
        dismissedThisSession.value = true
    }

    fun dontShowNextWeekReleaseDialogToday() {
        dismissedThisSession.value = true
        viewModelScope.launch {
            userDataRepository.updateShowNextReleaseMoviesDate(value = LocalDate.now().toString())
        }
    }
}