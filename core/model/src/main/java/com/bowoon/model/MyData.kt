package com.bowoon.model

import com.bowoon.model.tmdb.TMDBCertification
import com.bowoon.model.tmdb.TMDBLanguageItem
import com.bowoon.model.tmdb.TMDBMovieGenre
import com.bowoon.model.tmdb.TMDBRegionResult
import kotlinx.serialization.Serializable

@Serializable
data class MyData(
    val secureBaseUrl: String? = null,
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