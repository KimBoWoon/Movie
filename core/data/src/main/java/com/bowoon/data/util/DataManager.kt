package com.bowoon.data.util

import com.bowoon.model.DarkThemeConfig
import com.bowoon.model.MovieAppData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface DataManager {
    val movieAppData: StateFlow<MovieAppDataState>
    val localeFlow: Flow<Locale>
}

sealed interface MovieAppDataState {
    data object Loading : MovieAppDataState
    data class Success(val data: MovieAppData) : MovieAppDataState {
        override fun shouldUseDarkTheme(isSystemDarkTheme: Boolean) =
            when (data.isDarkMode) {
                DarkThemeConfig.FOLLOW_SYSTEM -> isSystemDarkTheme
                DarkThemeConfig.LIGHT -> false
                DarkThemeConfig.DARK -> true
            }

        override fun getMovieAppData(): MovieAppData = this.data
    }
    data class Error(val throwable: Throwable) : MovieAppDataState

    fun shouldKeepSplashScreen(): Boolean = this is Loading
    fun shouldUseDarkTheme(isSystemDarkTheme: Boolean): Boolean = isSystemDarkTheme
    fun getMovieAppData(): MovieAppData = MovieAppData()
}