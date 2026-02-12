package com.bowoon.model

data class SimilarTvs(
    val page: Int? = null,
    val results: List<SimilarTv>? = null,
    val totalPages: Int? = null,
    val totalResults: Int? = null
)

data class SimilarTv(
    val adult: Boolean? = null,
    val backdropPath: String? = null,
    val firstAirDate: String? = null,
    val genreIds: List<Int>? = null,
    val id: Int? = null,
    val name: String? = null,
    val originCountry: List<String>? = null,
    val originalLanguage: String? = null,
    val originalName: String? = null,
    val overview: String? = null,
    val popularity: Double? = null,
    val posterPath: String? = null,
    val voteAverage: Double? = null,
    val voteCount: Int? = null
)