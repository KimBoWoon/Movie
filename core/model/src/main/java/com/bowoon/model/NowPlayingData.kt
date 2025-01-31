package com.bowoon.model


data class NowPlayingData(
    val dates: NowPlayingDates? = null,
    val page: Int? = null,
    val results: List<NowPlaying>? = null,
    val totalPages: Int? = null,
    val totalResults: Int? = null
)

data class NowPlayingDates(
    val maximum: String? = null,
    val minimum: String? = null
)

data class NowPlaying(
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