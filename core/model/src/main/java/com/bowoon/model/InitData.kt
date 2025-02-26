package com.bowoon.model

import kotlinx.serialization.Serializable

@Serializable
data class InitData(
    val internalData: InternalData = InternalData(),
    val secureBaseUrl: String? = null,
    val configuration: Configuration? = null,
    val certification: Map<String, List<Certification>>? = null,
    val genres: List<MovieGenre>? = null,
    val region: List<Region>? = null,
    val language: List<LanguageItem>? = null,
    val posterSize: List<PosterSize>? = null
) {
    fun isDarkMode(isSystemInDarkMode: Boolean): Boolean = when (internalData.isDarkMode) {
        DarkThemeConfig.FOLLOW_SYSTEM -> isSystemInDarkMode
        DarkThemeConfig.LIGHT -> false
        DarkThemeConfig.DARK -> true
    }
    fun getImageUrl(): String =
        "$secureBaseUrl${posterSize?.find { it.isSelected }?.size ?: "original"}"
    fun getRegion(): String = "${region?.find { it.isSelected } ?: "KR"}"
    fun getLanguage(): String = "${language?.find { it.isSelected } ?: "ko"}"
}

@Serializable
data class PosterSize(
    val size: String? = null,
    val isSelected: Boolean = false
)

data class ExternalData(
    val secureBaseUrl: String? = null,
    val configuration: Configuration? = null,
    val certification: Map<String, List<Certification>>? = null,
    val genres: MovieGenreList? = null,
    val region: RegionList? = null,
    val language: List<LanguageItem>? = null,
    val posterSize: Images? = null
)