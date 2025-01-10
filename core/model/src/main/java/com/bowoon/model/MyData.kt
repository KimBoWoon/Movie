package com.bowoon.model

import kotlinx.serialization.Serializable

@Serializable
data class MyData(
    val configuration: TMDBConfiguration,
    val region: TMDBRegion,
    val language: List<TMDBLanguageItem>,
    val posterSize: List<PosterSize>
)

@Serializable
data class PosterSize(
    val size: String? = null,
    val isSelected: Boolean = false
)