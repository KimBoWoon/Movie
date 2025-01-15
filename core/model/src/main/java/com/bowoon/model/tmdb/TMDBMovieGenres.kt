package com.bowoon.model.tmdb


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TMDBMovieGenres(
    @SerialName("genres")
    val genres: List<TMDBMovieGenre>? = null
)

@Serializable
data class TMDBMovieGenre(
    @SerialName("id")
    val id: Int? = null,
    @SerialName("name")
    val name: String? = null
)