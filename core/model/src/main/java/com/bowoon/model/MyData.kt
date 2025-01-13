package com.bowoon.model

import kotlinx.serialization.Serializable

@Serializable
data class MyData(
    val secureBaseUrl: String? = null,
    val region: List<TMDBRegionResult>? = null,
    val language: List<TMDBLanguageItem>? = null,
    val posterSize: List<PosterSize>? = null
)

@Serializable
data class PosterSize(
    val size: String? = null,
    val isSelected: Boolean = false
)