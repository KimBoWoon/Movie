package com.cheeke.surfy.ui.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cheeke.surfy.data.repository.DatabaseRepository
import com.cheeke.surfy.data.repository.UserDataRepository
import com.cheeke.surfy.data.util.DataManager
import com.cheeke.surfy.data.util.SurfyAppDataState
import com.cheeke.surfy.data.util.SyncManager
import com.cheeke.surfy.model.Media
import com.cheeke.surfy.ui.image.imageUrl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
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
            supervisorScope {
                val isFirstInstall = userDataRepository.getFirstInstall()

                if (!isFirstInstall) {
                    syncManager.requestSync()
                    syncManager.syncMain()
                    userDataRepository.updateFirstInstall(value = true)
                }
            }

            supervisorScope {
                val lastUpdateMainDate = userDataRepository.getMainDate()

                if (lastUpdateMainDate.isEmpty()) {
                    syncManager.requestSync()
                } else {
                    if (LocalDate.parse(lastUpdateMainDate).plusDays(1) < LocalDate.now()) {
                        syncManager.requestSync()
                    }
                }
            }

            supervisorScope {
                _nextWeekReleaseDialogItems.emit(
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

    val surfyAppData = dataManager.surfyAppData
        .onEach {
            val url = it.getMovieAppData().getImageUrl()

            if (url.startsWith(prefix = "http://") || url.startsWith(prefix = "https://")) {
                imageUrl = url
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = SurfyAppDataState.Loading
        )
    private val _nextWeekReleaseDialogItems: MutableStateFlow<List<Media>> = MutableStateFlow(value = emptyList())
    val nextWeekReleaseDialogItems = _nextWeekReleaseDialogItems.asStateFlow()
}