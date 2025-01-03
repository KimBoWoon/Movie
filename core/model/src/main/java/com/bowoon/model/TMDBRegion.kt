package com.bowoon.model


data class TMDBRegion(
    val results: List<TMDBRegionResult>? = null
)

data class TMDBRegionResult(
    val englishName: String? = null,
    val iso31661: String? = null,
    val nativeName: String? = null,
    val isSelected: Boolean = false
)