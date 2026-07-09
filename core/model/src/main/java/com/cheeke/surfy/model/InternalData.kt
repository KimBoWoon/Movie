package com.cheeke.surfy.model

import kotlinx.serialization.Serializable
import java.util.Locale

@Serializable
data class InternalData(
    val isAdult: Boolean = true,
    val isAutoPlayTrailer: Boolean = true,
    val isDarkMode: DarkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
    val updateDate: String = "",
    val region: String = "${Locale.getDefault().country}",
    val language: String = "${Locale.getDefault().language}",
    val imageQuality: String = "original",
    val showNextReleaseMoviesDate: String = "",
    val secureBaseUrl: String = "",
    val isFirstInstall: Boolean = false,
    val isCheatActive: Boolean = false
)