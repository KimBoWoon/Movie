package com.bowoon.movie.ui.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bowoon.common.Result
import com.bowoon.common.asResult
import com.bowoon.domain.GetInitDataUseCase
import com.bowoon.model.DarkThemeConfig
import com.bowoon.model.InitData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainVM @Inject constructor(
    getMyDataUseCase: GetInitDataUseCase
) : ViewModel() {
    val myData = getMyDataUseCase()
        .asResult()
        .map {
            when (it) {
                is Result.Loading -> UserdataState.Loading
                is Result.Success -> UserdataState.Success(it.data)
                is Result.Error -> UserdataState.Error(it.throwable)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UserdataState.Loading
        )
}

sealed interface UserdataState {
    data object Loading : UserdataState
    data class Success(val data: InitData) : UserdataState {
        override fun shouldUseDarkTheme(isSystemDarkTheme: Boolean) =
            when (data.internalData.isDarkMode) {
                DarkThemeConfig.FOLLOW_SYSTEM -> isSystemDarkTheme
                DarkThemeConfig.LIGHT -> false
                DarkThemeConfig.DARK -> true
            }
    }
    data class Error(val throwable: Throwable) : UserdataState

    fun shouldKeepSplashScreen() = this is Loading
    fun shouldUseDarkTheme(isSystemDarkTheme: Boolean): Boolean = isSystemDarkTheme
}