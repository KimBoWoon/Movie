package com.cheeke.surfy.model

import kotlinx.serialization.Serializable

@Serializable
data class SurfyAppData(
    val isAdult: Boolean = true,
    val autoPlayTrailer: Boolean = true,
    val isDarkMode: DarkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
    val updateDate: String = "",
    val imageQuality: String = "original",
    val secureBaseUrl: String = "",
    val movieGenres: List<Genre> = emptyList(),
    val tvGenres: List<Genre> = emptyList(),
    val region: List<LocaleOption> = emptyList(),
    val language: List<LocaleOption> = emptyList(),
    val posterSize: List<PosterSize> = emptyList()
) {
    fun getImageUrl(): String = "$secureBaseUrl${posterSize.find { it.isSelected }?.size}"
}

@Serializable
data class PosterSize(
    val size: String? = null,
    val isSelected: Boolean = false
)