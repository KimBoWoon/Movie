package com.bowoon.model

import kotlinx.serialization.Serializable

@Serializable
data class InternalData(
    val isAdult: Boolean = true,
    val isAutoPlayTrailer: Boolean = true,
    val isDarkMode: DarkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
    val updateDate: String = "",
    val region: String = "KR",
    val language: String = "ko",
    val imageQuality: String = "original",
    val showNextReleaseMoviesDate: String = "",
    val secureBaseUrl: String = "",
    val isFirstInstall: Boolean = false
)