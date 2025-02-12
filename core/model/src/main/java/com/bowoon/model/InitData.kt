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
)

@Serializable
data class PosterSize(
    val size: String? = null,
    val isSelected: Boolean = false
)

data class TMDBConfiguration(
    val secureBaseUrl: String? = null,
    val configuration: Configuration? = null,
    val certification: Map<String, List<Certification>>? = null,
    val genres: MovieGenreList? = null,
    val region: RegionList? = null,
    val language: List<LanguageItem>? = null,
    val posterSize: Images? = null
)

data class MovieData(
    val secureBaseUrl: String? = "",
    val isAdult: Boolean = true,
    val autoPlayTrailer: Boolean = true,
    val isDarkMode: DarkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
    val updateDate: String = "",
    val mainMenu: MainMenu = MainMenu(),
    val favoriteMovies: List<MovieDetail> = emptyList(),
    val region: String = "KR",
    val language: String = "ko",
    val imageQuality: String = "original"
)