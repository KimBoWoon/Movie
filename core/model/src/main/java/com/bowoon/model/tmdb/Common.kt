package com.bowoon.model.tmdb

sealed interface SearchResult {
    val tmdbId: Int?
    val searchTitle: String?
    val imagePath: String?
}