package com.bowoon.network.model

import com.bowoon.model.SimilarTvs
import com.bowoon.model.SimilarTv
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTMDBSimilarTv(
    @SerialName("page")
    val page: Int? = null,
    @SerialName("results")
    val results: List<NetworkTMDBSimilarTvResult>? = null,
    @SerialName("total_pages")
    val totalPages: Int? = null,
    @SerialName("total_results")
    val totalResults: Int? = null
)

@Serializable
data class NetworkTMDBSimilarTvResult(
    @SerialName("adult")
    val adult: Boolean? = null,
    @SerialName("backdrop_path")
    val backdropPath: String? = null,
    @SerialName("first_air_date")
    val firstAirDate: String? = null,
    @SerialName("genre_ids")
    val genreIds: List<Int>? = null,
    @SerialName("id")
    val id: Int? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("origin_country")
    val originCountry: List<String>? = null,
    @SerialName("original_language")
    val originalLanguage: String? = null,
    @SerialName("original_name")
    val originalName: String? = null,
    @SerialName("overview")
    val overview: String? = null,
    @SerialName("popularity")
    val popularity: Double? = null,
    @SerialName("poster_path")
    val posterPath: String? = null,
    @SerialName("vote_average")
    val voteAverage: Double? = null,
    @SerialName("vote_count")
    val voteCount: Int? = null
)

fun NetworkTMDBSimilarTv.asExternalModel(): SimilarTvs = SimilarTvs(
    page = page,
    results = results?.asExternalModel(),
    totalPages = totalPages,
    totalResults = totalResults
)

fun List<NetworkTMDBSimilarTvResult>.asExternalModel(): List<SimilarTv> = map {
    SimilarTv(
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
        voteCount = it.voteCount
    )
}