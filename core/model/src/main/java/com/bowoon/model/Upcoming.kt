package com.bowoon.model

import kotlinx.serialization.Serializable

data class Upcoming(
    val dates: UpComingDates? = null,
    val page: Int? = null,
    val results: List<UpComingResult>? = null,
    val totalPages: Int? = null,
    val totalResults: Int? = null
)

data class UpComingDates(
    val maximum: String? = null,
    val minimum: String? = null
)

@Serializable
data class UpComingResult(
    val adult: Boolean? = null,
    val backdropPath: String? = null,
    val genreIds: List<Int>? = null,
    val id: Int? = null,
    val originalLanguage: String? = null,
    val originalTitle: String? = null,
    val overview: String? = null,
    val popularity: Double? = null,
    val posterPath: String? = null,
    val releaseDate: String? = null,
    val title: String? = null,
    val video: Boolean? = null,
    val voteAverage: Double? = null,
    val voteCount: Int? = null
)