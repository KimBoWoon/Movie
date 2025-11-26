package com.bowoon.model

import kotlinx.serialization.Serializable

@Serializable
data class Configuration(
    val changeKeys: List<String>? = null,
    val images: ImageInfo? = null
)

@Serializable
data class ImageInfo(
    val backdropSizes: List<String>? = null,
    val baseUrl: String? = null,
    val logoSizes: List<String>? = null,
    val posterSizes: List<String>? = null,
    val profileSizes: List<String>? = null,
    val secureBaseUrl: String? = null,
    val stillSizes: List<String>? = null
)