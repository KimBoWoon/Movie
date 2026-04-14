package com.cheeke.surfy.network.model

import com.cheeke.surfy.model.MovieList
import com.cheeke.surfy.model.MovieListDate
import com.cheeke.surfy.model.MovieResult
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTMDBMovieList(
    @SerialName(value = "dates")
    val dates: NetworkTMDBMovieListDate? = null,
    @SerialName(value = "page")
    val page: Int? = null,
    @SerialName(value = "results")
    val results: List<NetworkTMDBMovieListResult>? = null,
    @SerialName(value = "total_pages")
    val totalPages: Int? = null,
    @SerialName(value = "total_results")
    val totalResults: Int? = null
)

@Serializable
data class NetworkTMDBMovieListDate(
    @SerialName(value = "maximum")
    val maximum: String? = null,
    @SerialName(value = "minimum")
    val minimum: String? = null
)

@Serializable
data class NetworkTMDBMovieListResult(
    @SerialName(value = "adult")
    val adult: Boolean? = null,
    @SerialName(value = "backdrop_path")
    val backdropPath: String? = null,
    @SerialName(value = "genre_ids")
    val genreIds: List<Int>? = null,
    @SerialName(value = "id")
    val id: Int? = null,
    @SerialName(value = "original_language")
    val originalLanguage: String? = null,
    @SerialName(value = "original_title")
    val originalTitle: String? = null,
    @SerialName(value = "overview")
    val overview: String? = null,
    @SerialName(value = "popularity")
    val popularity: Double? = null,
    @SerialName(value = "poster_path")
    val posterPath: String? = null,
    @SerialName("release_date")
    val releaseDate: String? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("video")
    val video: Boolean? = null,
    @SerialName("vote_average")
    val voteAverage: Float? = null,
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