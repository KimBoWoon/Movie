package com.bowoon.model

import kotlinx.serialization.Serializable

@Serializable
data class TMDBRegion(
    val results: List<TMDBRegionResult>? = null
)

@Serializable
data class TMDBRegionResult(
    val englishName: String? = null,
    val iso31661: String? = null,
    val nativeName: String? = null,
    val isSelected: Boolean = false
)