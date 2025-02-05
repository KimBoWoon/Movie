package com.bowoon.model

import kotlinx.serialization.Serializable

@Serializable
data class MyData(
    val isAdult: Boolean? = null,
    val isDarkMode: DarkThemeConfig? = null,
    val isAutoPlayTrailer: Boolean? = null,
    val mainUpdateLatestDate: String? = null,
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

data class RequestMyData(
    val configuration: Configuration? = null,
    val certification: Map<String, List<Certification>>? = null,
    val genres: MovieGenreList? = null,
    val region: RegionList? = null,
    val language: List<LanguageItem>? = null,
    val posterSize: Images? = null
)