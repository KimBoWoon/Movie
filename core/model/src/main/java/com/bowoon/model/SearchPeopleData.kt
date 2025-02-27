package com.bowoon.model

data class SearchPeopleData(
    val page: Int? = null,
    val results: List<SearchPeopleItem>? = null,
    val totalPages: Int? = null,
    val totalResults: Int? = null
)

data class SearchPeopleItem(
    val adult: Boolean? = null,
    val gender: Int? = null,
    val id: Int? = null,
    val knownFor: List<SearchPeopleKnownFor>? = null,
    val knownForDepartment: String? = null,
    val name: String? = null,
    val originalName: String? = null,
    val popularity: Double? = null,
    val profilePath: String? = null,
    override val tmdbId: Int? = null,
    override val searchTitle: String? = null,
    override val imagePath: String? = null
) : SearchResult

data class SearchPeopleKnownFor(
    val adult: Boolean? = null,
    val backdropPath: String? = null,
    val genreIds: List<Int>? = null,
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