package com.cheeke.surfy.data.util

import com.cheeke.surfy.model.DarkThemeConfig
import com.cheeke.surfy.model.SurfyAppData
import io.reactivex.rxjava3.core.Flowable

interface DataManager {
    val surfyAppData: Flowable<SurfyAppDataState>
    val localeFlow: Flowable<Locale>
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