package com.bowoon.model

import kotlinx.serialization.Serializable

@Serializable
data class Movie(
    val genreIds: List<Int>? = null,
    val id: Int? = null,
    val originalLanguage: String? = null,
    val originalTitle: String? = null,
    val overview: String? = null,
    val popularity: Double? = null,
    val posterPath: String? = null,
    val releaseDate: String? = null,
    val title: String? = null,
    val voteAverage: Double? = null,
    val voteCount: Int? = null,
    val rank: String? = null,
    val rankOldAndNew: String? = null,
)