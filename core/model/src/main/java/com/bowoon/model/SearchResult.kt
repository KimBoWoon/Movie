package com.bowoon.model

sealed interface SearchResult {
    val tmdbId: Int?
    val searchTitle: String?
    val imagePath: String?
}