package com.bowoon.model

data class SearchKeywordData(
    val page: Int? = null,
    val results: List<SearchKeyword>? = null,
    val totalPages: Int? = null,
    val totalResults: Int? = null
)

data class SearchKeyword(
    val id: Int? = null,
    val name: String? = null
)