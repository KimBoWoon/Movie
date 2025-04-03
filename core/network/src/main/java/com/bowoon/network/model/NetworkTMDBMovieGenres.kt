package com.bowoon.network.model


import com.bowoon.model.Genre
import com.bowoon.model.Genres
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

fun NetworkTMDBMovieGenres.asExternalModel(): Genres =
    Genres(
        genres = genres?.asExternalModel()
    )

fun List<NetworkTMDBMovieGenre>.asExternalModel(): List<Genre> =
    map {
        Genre(
            id = it.id,
            name = it.name
        )
    }