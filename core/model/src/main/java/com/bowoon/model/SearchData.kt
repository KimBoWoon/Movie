package com.bowoon.model

data class SearchData(
    val page: Int? = null,
    val results: List<DisplayItem>? = null,
    val totalPages: Int? = null,
    val totalResults: Int? = null
)