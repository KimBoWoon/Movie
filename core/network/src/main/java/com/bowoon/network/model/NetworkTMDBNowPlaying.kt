package com.bowoon.network.model


import com.bowoon.model.tmdb.TMDBNowPlaying
import com.bowoon.model.tmdb.TMDBNowPlayingDates
import com.bowoon.model.tmdb.TMDBNowPlayingResult
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTMDBNowPlaying(
    @SerialName("dates")
    val dates: NetworkTMDBNowPlayingDates? = null,
    @SerialName("page")
    val page: Int? = null,
    @SerialName("results")
    val results: List<NetworkTMDBNowPlayingResult>? = null,
    @SerialName("total_pages")
    val totalPages: Int? = null,
    @SerialName("total_results")
    val totalResults: Int? = null
)

@Serializable
data class NetworkTMDBNowPlayingDates(
    @SerialName("maximum")
    val maximum: String? = null,
    @SerialName("minimum")
    val minimum: String? = null
)

@Serializable
data class NetworkTMDBNowPlayingResult(
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

fun NetworkTMDBNowPlaying.asExternalModel(): TMDBNowPlaying =
    TMDBNowPlaying(
        dates = dates?.asExternalModel(),
        page = page,
        results = results?.asExternalModel(),
        totalResults = totalResults,
        totalPages = totalPages
    )

fun NetworkTMDBNowPlayingDates.asExternalModel(): TMDBNowPlayingDates =
    TMDBNowPlayingDates(
        maximum = maximum,
        minimum = minimum
    )

fun List<NetworkTMDBNowPlayingResult>.asExternalModel(): List<TMDBNowPlayingResult> =
    map {
        TMDBNowPlayingResult(
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