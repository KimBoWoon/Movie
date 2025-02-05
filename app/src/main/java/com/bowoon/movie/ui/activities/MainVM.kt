package com.bowoon.movie.ui.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.DarkThemeConfig
import com.bowoon.model.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainVM @Inject constructor(
    userDataRepository: UserDataRepository
) : ViewModel() {
    val userdata = userDataRepository.userData
        .map { UserdataState.Success(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UserdataState.Loading
        )
}

sealed interface UserdataState {
    data object Loading : UserdataState
    data class Success(val data: UserData) : UserdataState {
        override fun shouldUseDarkTheme(isSystemDarkTheme: Boolean) =
            when (data.isDarkMode) {
                DarkThemeConfig.FOLLOW_SYSTEM -> isSystemDarkTheme
                DarkThemeConfig.LIGHT -> false
                DarkThemeConfig.DARK -> true
            }
    }
    data class Error(val throwable: Throwable) : UserdataState

    fun shouldKeepSplashScreen() = this is Loading
    fun shouldUseDarkTheme(isSystemDarkTheme: Boolean): Boolean = isSystemDarkTheme
}