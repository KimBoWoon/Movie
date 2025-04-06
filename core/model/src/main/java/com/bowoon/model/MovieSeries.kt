package com.bowoon.model

data class MovieSeries(
    val backdropPath: String? = null,
    val id: Int? = null,
    val name: String? = null,
    val overview: String? = null,
    val parts: List<MovieSeriesPart?>? = null,
    val posterPath: String? = null
)

data class MovieSeriesPart(
    val adult: Boolean? = null,
    val backdropPath: String? = null,
    val genreIds: List<Int?>? = null,
    val id: Int? = null,
    val mediaType: String? = null,
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