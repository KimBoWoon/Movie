package com.bowoon.network.model


import com.bowoon.model.tmdb.TMDBMovieGenre
import com.bowoon.model.tmdb.TMDBMovieGenres
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTMDBMovieGenres(
    @SerialName("genres")
    val genres: List<NetworkTMDBMovieGenre>? = null
)

@Serializable
data class NetworkTMDBMovieGenre(
    @SerialName("id")
    val id: Int? = null,
    @SerialName("name")
    val name: String? = null
)

fun NetworkTMDBMovieGenres.asExternalModel(): TMDBMovieGenres =
    TMDBMovieGenres(
        genres = genres?.asExternalModel()
    )

fun List<NetworkTMDBMovieGenre>.asExternalModel(): List<TMDBMovieGenre> =
    map {
        TMDBMovieGenre(
            id = it.id,
            name = it.name
        )
    }