package com.bowoon.model

data class SearchData(
    val page: Int? = null,
    val results: List<Media>? = null,
    val totalPages: Int? = null,
    val totalResults: Int? = null
)