package com.bowoon.model

data class TrendingPeople(
    val page: Int? = null,
    val results: List<TrendingPeopleResult>? = null,
    val totalPages: Int? = null,
    val totalResults: Int? = null
)

data class TrendingPeopleResult(
    val adult: Boolean? = null,
    val gender: Int? = null,
    override val id: Int? = null,
    val knownFor: List<TrendingPeopleKnownFor>? = null,
    val knownForDepartment: String? = null,
    val mediaType: String? = null,
    override val title: String? = null,
    override val originalTitle: String? = null,
    val popularity: Double? = null,
    override val posterPath: String? = null,
    override val genres: List<Genre>? = null,
    override val releaseDate: String? = null,
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
    val voteAverage: Double? = null,
    val voteCount: Int? = null
)