package com.bowoon.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bowoon.common.Result
import com.bowoon.common.asResult
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.data.util.SyncManager
import com.bowoon.model.MainMenu
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeVM @Inject constructor(
    syncManager: SyncManager,
    userDataRepository: UserDataRepository,
) : ViewModel() {
    companion object {
        private const val TAG = "HomeVM"
    }

    val isSyncing = syncManager.isSyncing
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
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
            started = SharingStarted.Lazily
        )
}

sealed interface MainMenuState {
    data object Loading : MainMenuState
    data class Success(val mainMenu: MainMenu) : MainMenuState
    data class Error(val throwable: Throwable) : MainMenuState
}