package com.cheeke.surfy.model

import kotlinx.serialization.Serializable
import java.util.Locale

val defaultLanguage: String
    get() = Locale.getDefault().language
val defaultRegion: String
    get() = Locale.getDefault().country
val defaultLanguageRegion: String
    get() = "${defaultLanguage}-${defaultRegion}"

@Serializable
data class InternalData(
    val isAdult: Boolean = true,
    val isAutoPlayTrailer: Boolean = true,
    val isDarkMode: DarkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
    val updateDate: String = "",
    val region: String = defaultRegion,
    val language: String = defaultLanguage,
    val imageQuality: String = "original",
    val showNextReleaseMoviesDate: String = "",
    val secureBaseUrl: String = "",
    val isFirstInstall: Boolean = false,
    val isCheatActive: Boolean = false
)