package com.bowoon.model.tmdb


data class TMDBNowPlaying(
    val dates: TMDBNowPlayingDates? = null,
    val page: Int? = null,
    val results: List<TMDBNowPlayingResult>? = null,
    val totalPages: Int? = null,
    val totalResults: Int? = null
)

data class TMDBNowPlayingDates(
    val maximum: String? = null,
    val minimum: String? = null
)

data class TMDBNowPlayingResult(
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