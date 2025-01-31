package com.bowoon.network.model


import com.bowoon.model.MovieGenre
import com.bowoon.model.MovieGenreList
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

fun NetworkTMDBMovieGenres.asExternalModel(): MovieGenreList =
    MovieGenreList(
        genres = genres?.asExternalModel()
    )

fun List<NetworkTMDBMovieGenre>.asExternalModel(): List<MovieGenre> =
    map {
        MovieGenre(
            id = it.id,
            name = it.name
        )
    }