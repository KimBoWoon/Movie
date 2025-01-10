package com.bowoon.model

import kotlinx.serialization.Serializable

@Serializable
data class TMDBLanguageItem(
    val englishName: String? = null,
    val iso6391: String? = null,
    val name: String? = null,
    val isSelected: Boolean = false
)