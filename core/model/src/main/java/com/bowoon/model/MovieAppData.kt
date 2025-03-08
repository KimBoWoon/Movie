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
    val secureBaseUrl: String? = null,
    val configuration: Configuration? = null,
    val certification: Map<String, List<Certification>>? = null,
    val genres: List<MovieGenre>? = null,
    val region: List<Region>? = null,
    val language: List<LanguageItem>? = null,
    val posterSize: List<PosterSize>? = null
) {
    fun isDarkMode(isSystemInDarkMode: Boolean): Boolean = when (isDarkMode) {
        DarkThemeConfig.FOLLOW_SYSTEM -> isSystemInDarkMode
        DarkThemeConfig.LIGHT -> false
        DarkThemeConfig.DARK -> true
    }

    fun getImageUrl(): String =
        "$secureBaseUrl${posterSize?.find { it.isSelected }?.size}"

    fun getRegion(): String = "${region?.find { it.isSelected }?.iso31661} (${region?.find { it.isSelected }?.englishName})"
    fun getLanguage(): String = "${language?.find { it.isSelected }?.iso6391} (${language?.find { it.isSelected }?.englishName})"
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

fun MovieAppData.asInternalData(): InternalData = InternalData(
    isAdult = isAdult,
    autoPlayTrailer = autoPlayTrailer,
    isDarkMode = isDarkMode,
    updateDate = updateDate,
    mainMenu = mainMenu,
    region = region?.find { it.isSelected }?.iso31661 ?: "",
    language = language?.find { it.isSelected }?.iso6391 ?: "",
    imageQuality = imageQuality
)