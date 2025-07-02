package com.bowoon.model

import kotlinx.serialization.Serializable

@Serializable
data class DisplayItem(
    val adult: Boolean? = null,
    val id: Int? = null,
    val title: String? = null,
    val imagePath: String? = null,
    val genreIds: List<Int>? = null,
    val releaseDate: String? = null
)