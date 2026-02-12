package com.bowoon.model

data class TrendingMovie(
    val page: Int? = null,
    val results: List<TrendingMovieResult>? = null,
    val totalPages: Int? = null,
    val totalResults: Int? = null
)

data class TrendingMovieResult(
    val adult: Boolean? = null,
    val backdropPath: String? = null,
    val genreIds: List<Int>? = null,
    override val id: Int? = null,
    val mediaType: String? = null,
    val originalLanguage: String? = null,
    override val originalTitle: String? = null,
    val overview: String? = null,
    val popularity: Double? = null,
    override val posterPath: String? = null,
    override val releaseDate: String? = null,
    override val title: String? = null,
    val video: Boolean? = null,
    val voteAverage: Double? = null,
    val voteCount: Int? = null,
    override val genres: List<Genre>? = null
) : Media