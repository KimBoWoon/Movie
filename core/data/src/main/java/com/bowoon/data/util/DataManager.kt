package com.bowoon.data.util

import com.bowoon.model.DarkThemeConfig
import com.bowoon.model.SurfyAppData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface DataManager {
    val surfyAppData: StateFlow<SurfyAppDataState>
    val localeFlow: Flow<Locale>
}

sealed interface SurfyAppDataState {
    data object Loading : SurfyAppDataState
    data class Success(val data: SurfyAppData) : SurfyAppDataState {
        override fun shouldUseDarkTheme(isSystemDarkTheme: Boolean) =
            when (data.isDarkMode) {
                DarkThemeConfig.FOLLOW_SYSTEM -> isSystemDarkTheme
                DarkThemeConfig.LIGHT -> false
                DarkThemeConfig.DARK -> true
            }

        override fun getMovieAppData(): SurfyAppData = this.data
    }
    data class Error(val throwable: Throwable) : SurfyAppDataState

    fun shouldKeepSplashScreen(): Boolean = this is Loading
    fun shouldUseDarkTheme(isSystemDarkTheme: Boolean): Boolean = isSystemDarkTheme
    fun getMovieAppData(): SurfyAppData = SurfyAppData()
}