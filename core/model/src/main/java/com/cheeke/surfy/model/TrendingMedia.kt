package com.cheeke.surfy.model

data class TrendingMedia(
    val page: Int? = null,
    val results: List<TrendingMediaResult>? = null,
    val totalPages: Int? = null,
    val totalResults: Int? = null
)

data class TrendingMediaResult(
    // common
    override val isFavorite: Boolean = false,

    // Movie
    val adult: Boolean? = null,
    val backdropPath: String? = null,
    val genreIds: List<Int>? = null,
    override val id: Int? = null,
    override val mediaType: MediaType = MediaType.NONE,
    val originalLanguage: String? = null,
    override val originalTitle: String? = null,
    val overview: String? = null,
    val popularity: Double? = null,
    override val posterPath: String? = null,
    override val releaseDate: String? = null,
    override val title: String? = null,
    val video: Boolean? = null,
    override val voteAverage: Float? = null,
    val voteCount: Int? = null,
    override val genres: List<Genre>? = null,
    override val certification: String? = null,
    override val runtime: Int? = null,
    override val tagline: String? = null,

    // People
    val gender: Int? = null,
    val knownFor: List<TrendingPeopleKnownFor>? = null,
    val knownForDepartment: String? = null,

    // TV
    override val firstAirDate: String? = null,
    override val lastAirDate: String? = null,
    val originCountry: List<String>? = null
) : Media

data class TrendingPeopleKnownFor(
    val adult: Boolean? = null,
    val backdropPath: String? = null,
    val firstAirDate: String? = null,
    val genreIds: List<Int>? = null,
    val id: Int? = null,
    val mediaType: String? = null,
    val name: String? = null,
    val originCountry: List<String>? = null,
    val originalLanguage: String? = null,
    val originalName: String? = null,
    val originalTitle: String? = null,
    val overview: String? = null,
    val popularity: Double? = null,
    val posterPath: String? = null,
    val releaseDate: String? = null,
    val title: String? = null,
    val video: Boolean? = null,
    val voteAverage: Float? = null,
    val voteCount: Int? = null
)