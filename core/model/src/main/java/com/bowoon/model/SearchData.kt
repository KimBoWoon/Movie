package com.bowoon.model

import kotlinx.serialization.Serializable

sealed interface SearchGroup {
    val adult: Boolean?
    val id: Int?
    val name: String?
    val imagePath: String?
    val originalName: String?
    val popularity: Double?
}

data class SearchData(
    val page: Int? = null,
    val results: List<SearchGroup>? = null,
    val totalPages: Int? = null,
    val totalResults: Int? = null
)

@Serializable
data class Movie(
    val backdropPath: String? = null,
    val genreIds: List<Int>? = null,
    val originalLanguage: String? = null,
    val originalTitle: String? = null,
    val overview: String? = null,
    val posterPath: String? = null,
    val releaseDate: String? = null,
    val title: String? = null,
    val video: Boolean? = null,
    val voteAverage: Double? = null,
    val voteCount: Int? = null,

    override val adult: Boolean? = null,
    override val id: Int? = null,
    override val name: String? = null,
    override val imagePath: String? = null,
    override val originalName: String? = null,
    override val popularity: Double? = null
) : SearchGroup

@Serializable
data class People(
    val gender: Int? = null,
    val knownFor: List<SearchPeopleKnownFor>? = null,
    val knownForDepartment: String? = null,
    val profilePath: String? = null,

    override val adult: Boolean? = null,
    override val id: Int? = null,
    override val name: String? = null,
    override val imagePath: String? = null,
    override val originalName: String? = null,
    override val popularity: Double? = null
) : SearchGroup

@Serializable
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