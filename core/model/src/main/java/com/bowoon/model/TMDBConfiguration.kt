package com.bowoon.model

import kotlinx.serialization.Serializable

@Serializable
data class TMDBConfiguration(
    val changeKeys: List<String>? = null,
    val images: Images? = null
)

@Serializable
data class Images(
    val backdropSizes: List<String>? = null,
    val baseUrl: String? = null,
    val logoSizes: List<String>? = null,
    val posterSizes: List<String>? = null,
    val profileSizes: List<String>? = null,
    val secureBaseUrl: String? = null,
    val stillSizes: List<String>? = null
)