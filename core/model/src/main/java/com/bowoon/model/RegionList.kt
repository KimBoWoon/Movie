package com.bowoon.model

import kotlinx.serialization.Serializable

@Serializable
data class RegionList(
    val results: List<Region>? = null
)

@Serializable
data class Region(
    val englishName: String? = null,
    val iso31661: String? = null,
    val nativeName: String? = null,
    val isSelected: Boolean = false
)