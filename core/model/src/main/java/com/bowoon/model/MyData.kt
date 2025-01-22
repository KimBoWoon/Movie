package com.bowoon.model

import com.bowoon.model.tmdb.TMDBCertification
import com.bowoon.model.tmdb.TMDBConfiguration
import com.bowoon.model.tmdb.TMDBLanguageItem
import com.bowoon.model.tmdb.TMDBMovieGenre
import com.bowoon.model.tmdb.TMDBMovieGenres
import com.bowoon.model.tmdb.TMDBRegion
import com.bowoon.model.tmdb.TMDBRegionResult
import kotlinx.serialization.Serializable

@Serializable
data class MyData(
    val isAdult: Boolean? = null,
    val mainUpdateLatestDate: String? = null,
    val secureBaseUrl: String? = null,
    val configuration: TMDBConfiguration? = null,
    val certification: Map<String, List<TMDBCertification>>? = null,
    val genres: List<TMDBMovieGenre>? = null,
    val region: List<TMDBRegionResult>? = null,
    val language: List<TMDBLanguageItem>? = null,
    val posterSize: List<PosterSize>? = null
)

@Serializable
data class PosterSize(
    val size: String? = null,
    val isSelected: Boolean = false
)

data class RequestMyData(
    val configuration: TMDBConfiguration? = null,
    val certification: Map<String, List<TMDBCertification>>? = null,
    val genres: TMDBMovieGenres? = null,
    val region: TMDBRegion? = null,
    val language: List<TMDBLanguageItem>? = null,
    val posterSize: List<PosterSize>? = null
)