package com.bowoon.network.model

import com.bowoon.model.MovieSeries
import com.bowoon.model.MovieSeriesPart
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTMDBMovieSeries(
    @SerialName("backdrop_path")
    val backdropPath: String? = null,
    @SerialName("id")
    val id: Int? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("overview")
    val overview: String? = null,
    @SerialName("parts")
    val parts: List<NetworkTMDBMovieSeriesPart>? = null,
    @SerialName("poster_path")
    val posterPath: String? = null
)

@Serializable
data class NetworkTMDBMovieSeriesPart(
    @SerialName("adult")
    val adult: Boolean? = null,
    @SerialName("backdrop_path")
    val backdropPath: String? = null,
    @SerialName("genre_ids")
    val genreIds: List<Int>? = null,
    @SerialName("id")
    val id: Int? = null,
    @SerialName("media_type")
    val mediaType: String? = null,
    @SerialName("original_language")
    val originalLanguage: String? = null,
    @SerialName("original_title")
    val originalTitle: String? = null,
    @SerialName("overview")
    val overview: String? = null,
    @SerialName("popularity")
    val popularity: Double? = null,
    @SerialName("poster_path")
    val posterPath: String? = null,
    @SerialName("release_date")
    val releaseDate: String? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("video")
    val video: Boolean? = null,
    @SerialName("vote_average")
    val voteAverage: Double? = null,
    @SerialName("vote_count")
    val voteCount: Int? = null
)

fun NetworkTMDBMovieSeries.asExternalModel(): MovieSeries =
    MovieSeries(
        backdropPath = backdropPath,
        id = id,
        name = name,
        overview = overview,
        parts = parts?.asExternalModel(),
        posterPath = posterPath
    )

fun List<NetworkTMDBMovieSeriesPart>.asExternalModel(): List<MovieSeriesPart> =
    map {
        MovieSeriesPart(
            adult = it.adult,
            backdropPath = it.backdropPath,
            genreIds = it.genreIds,
            id = it.id,
            mediaType = it.mediaType,
            originalLanguage = it.originalLanguage,
            originalTitle = it.originalTitle,
            overview = it.overview,
            popularity = it.popularity,
            posterPath = it.posterPath,
            releaseDate = it.releaseDate,
            title = it.title,
            video = it.video,
            voteAverage = it.voteAverage,
            voteCount = it.voteCount
        )
    }