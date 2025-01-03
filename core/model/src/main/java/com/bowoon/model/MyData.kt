package com.bowoon.model

data class MyData(
    val configuration: TMDBConfiguration,
    val region: TMDBRegion,
    val language: List<TMDBLanguageItem>,
    val posterSize: List<PosterSize>
)

data class PosterSize(
    val size: String? = null,
    val isSelected: Boolean = false
)