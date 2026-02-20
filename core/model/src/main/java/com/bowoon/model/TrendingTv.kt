package com.bowoon.model

data class TrendingTv(
    val page: Int? = null,
    val results: List<TrendingTvResult>? = null,
    val totalPages: Int? = null,
    val totalResults: Int? = null
)

data class TrendingTvResult(
    val adult: Boolean? = null,
    val backdropPath: String? = null,
    val firstAirDate: String? = null,
    val genreIds: List<Int?>? = null,
    override val id: Int? = null,
    val mediaType: String? = null,
    override val title: String? = null,
    val originCountry: List<String?>? = null,
    val originalLanguage: String? = null,
    override val originalTitle: String? = null,
    val overview: String? = null,
    val popularity: Double? = null,
    override val posterPath: String? = null,
    override val voteAverage: Float? = null,
    val voteCount: Int? = null,
    override val genres: List<Genre>? = null,
    override val releaseDate: String? = null,
    override val certification: String? = null,
    override val runtime: Int? = null,
    override val tagline: String? = null
) : Media