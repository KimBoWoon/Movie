package com.bowoon.network.model


import com.bowoon.model.MovieList
import com.bowoon.model.MovieListDate
import com.bowoon.model.MovieResult
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTMDBMovieList(
    @SerialName("dates")
    val dates: NetworkTMDBMovieListDate? = null,
    @SerialName("page")
    val page: Int? = null,
    @SerialName("results")
    val results: List<NetworkTMDBMovieListResult>? = null,
    @SerialName("total_pages")
    val totalPages: Int? = null,
    @SerialName("total_results")
    val totalResults: Int? = null
)

@Serializable
data class NetworkTMDBMovieListDate(
    @SerialName("maximum")
    val maximum: String? = null,
    @SerialName("minimum")
    val minimum: String? = null
)

@Serializable
data class NetworkTMDBMovieListResult(
    @SerialName("adult")
    val adult: Boolean? = null,
    @SerialName("backdrop_path")
    val backdropPath: String? = null,
    @SerialName("genre_ids")
    val genreIds: List<Int>? = null,
    @SerialName("id")
    val id: Int? = null,
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

fun NetworkTMDBMovieList.asExternalModel(): MovieList =
    MovieList(
        dates = dates?.asExternalModel(),
        page = page,
        results = results?.asExternalModel(),
        totalResults = totalResults,
        totalPages = totalPages
    )

fun NetworkTMDBMovieListDate.asExternalModel(): MovieListDate =
    MovieListDate(
        maximum = maximum,
        minimum = minimum
    )

fun List<NetworkTMDBMovieListResult>.asExternalModel(): List<MovieResult> =
    map {
        MovieResult(
            adult = it.adult,
            backdropPath = it.backdropPath,
            genreIds = it.genreIds,
            id = it.id,
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