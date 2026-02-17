package com.bowoon.model

data class MovieWatchProvider(
    val id: Int? = null,
    val results: Map<String, MovieWatchProviderResult>? = null
)

data class MovieWatchProviderResult(
    val link: String? = null,
    val flatrate: List<MovieWatchProviderItem>? = null,
    val buy: List<MovieWatchProviderItem>? = null,
    val rent: List<MovieWatchProviderItem>? = null
)

data class MovieWatchProviderItem(
    val logoPath: String? = null,
    val providerId: Int? = null,
    val providerName: String? = null,
    val displayPriority: Int? = null
)