package com.cheeke.surfy.network.model

import com.cheeke.surfy.model.SimilarMedia
import com.cheeke.surfy.model.SimilarMedias
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTMDBSimilarMedia(
    @SerialName("page")
    val page: Int? = null,
    @SerialName("results")
    val results: List<NetworkTMDBSimilarMediaResult>? = null,
    @SerialName("total_pages")
    val totalPages: Int? = null,
    @SerialName("total_results")
    val totalResults: Int? = null
)

@Serializable
data class NetworkTMDBSimilarMediaResult(
    // Movie
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
    val voteAverage: Float? = null,
    @SerialName("vote_count")
    val voteCount: Int? = null,

    // TV
    @SerialName("first_air_date")
    val firstAirDate: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("origin_country")
    val originCountry: List<String>? = null,
    @SerialName("original_name")
    val originalName: String? = null
)

fun NetworkTMDBSimilarMedia.asExternalModel(): SimilarMedias = SimilarMedias(
    page = page,
    results = results?.asExternalModel(),
    totalPages = totalPages,
    totalResults = totalResults
)

fun List<NetworkTMDBSimilarMediaResult>.asExternalModel(): List<SimilarMedia> = map {
    SimilarMedia(
        adult = it.adult,
        backdropPath = it.backdropPath,
        firstAirDate = it.firstAirDate,
        genreIds = it.genreIds,
        id = it.id,
        name = it.name,
        originCountry = it.originCountry,
        originalLanguage = it.originalLanguage,
        originalName = it.originalName,
        overview = it.overview,
        popularity = it.popularity,
        posterPath = it.posterPath,
        voteAverage = it.voteAverage,
        voteCount = it.voteCount,
        originalTitle = it.originalTitle,
        releaseDate = it.releaseDate,
        title = it.title,
        video = it.video
    )
}