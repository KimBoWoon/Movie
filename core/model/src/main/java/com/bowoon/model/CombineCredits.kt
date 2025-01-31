package com.bowoon.model

import kotlinx.serialization.Serializable

@Serializable
data class CombineCredits(
    val cast: List<CombineCreditsCast>? = null,
    val crew: List<CombineCreditsCrew>? = null,
    val id: Int? = null
)

@Serializable
data class CombineCreditsCast(
    val adult: Boolean? = null,
    val backdropPath: String? = null,
    val character: String? = null,
    val creditId: String? = null,
    val episodeCount: Int? = null,
    val firstAirDate: String? = null,
    val genreIds: List<Int>? = null,
    val id: Int? = null,
    val mediaType: String? = null,
    val name: String? = null,
    val order: Int? = null,
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

@Serializable
data class CombineCreditsCrew(
    val adult: Boolean? = null,
    val backdropPath: String? = null,
    val creditId: String? = null,
    val department: String? = null,
    val episodeCount: Int? = null,
    val firstAirDate: String? = null,
    val genreIds: List<Int>? = null,
    val id: Int? = null,
    val job: String? = null,
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