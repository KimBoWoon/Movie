package com.bowoon.model

import kotlinx.serialization.Serializable

@Serializable
data class MovieAppData(
    val isAdult: Boolean = true,
    val autoPlayTrailer: Boolean = true,
    val isDarkMode: DarkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
    val updateDate: String = "",
    val mainMenu: MainMenu = MainMenu(),
    val imageQuality: String = "original",
    val secureBaseUrl: String = "",
    val genres: List<Genre> = emptyList(),
    val region: List<Region> = emptyList(),
    val language: List<Language> = emptyList(),
    val posterSize: List<PosterSize> = emptyList()
) {
    fun isDarkMode(isSystemInDarkMode: Boolean): Boolean = when (isDarkMode) {
        DarkThemeConfig.FOLLOW_SYSTEM -> isSystemInDarkMode
        DarkThemeConfig.LIGHT -> false
        DarkThemeConfig.DARK -> true
    }

    fun getImageUrl(): String = "$secureBaseUrl${posterSize.find { it.isSelected }?.size}"
    fun getRegion(): String = "${region.find { it.isSelected }?.iso31661} (${region.find { it.isSelected }?.englishName})"
    fun getLanguage(): String = "${language.find { it.isSelected }?.iso6391} (${language.find { it.isSelected }?.englishName})"
}

@Serializable
data class PosterSize(
    val size: String? = null,
    val isSelected: Boolean = false
)