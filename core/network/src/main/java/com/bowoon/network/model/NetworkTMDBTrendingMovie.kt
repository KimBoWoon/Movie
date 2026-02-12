package com.bowoon.network.model

import com.bowoon.model.TrendingMovie
import com.bowoon.model.TrendingMovieResult
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTMDBTrendingMovie(
    @SerialName("page")
    val page: Int? = null,
    @SerialName("results")
    val results: List<NetworkTMDBTrendingMovieResult>? = null,
    @SerialName("total_pages")
    val totalPages: Int? = null,
    @SerialName("total_results")
    val totalResults: Int? = null
)

@Serializable
data class NetworkTMDBTrendingMovieResult(
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

fun NetworkTMDBTrendingMovie.asExternalModel(): TrendingMovie = TrendingMovie(
    page = page,
    results = results?.asExternalModel(),
    totalPages = totalPages,
    totalResults = totalResults
)

fun List<NetworkTMDBTrendingMovieResult>.asExternalModel(): List<TrendingMovieResult> = map {
    TrendingMovieResult(
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