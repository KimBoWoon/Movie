package com.bowoon.model

data class SearchData(
    val page: Int? = null,
    val results: List<Movie>? = null,
    val totalPages: Int? = null,
    val totalResults: Int? = null
)