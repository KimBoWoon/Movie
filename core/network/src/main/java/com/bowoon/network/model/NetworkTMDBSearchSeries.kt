package com.bowoon.network.model

import com.bowoon.model.Movie
import com.bowoon.model.SearchData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTMDBSearchSeries(
    @SerialName("page")
    val page: Int? = null,
    @SerialName("results")
    val results: List<NetworkTMDBSearchSeriesResult>? = null,
    @SerialName("total_pages")
    val totalPages: Int? = null,
    @SerialName("total_results")
    val totalResults: Int? = null
)

@Serializable
data class NetworkTMDBSearchSeriesResult(
    @SerialName("adult")
    val adult: Boolean? = null,
    @SerialName("backdrop_path")
    val backdropPath: String? = null,
    @SerialName("id")
    val id: Int? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("original_language")
    val originalLanguage: String? = null,
    @SerialName("original_name")
    val originalName: String? = null,
    @SerialName("overview")
    val overview: String? = null,
    @SerialName("poster_path")
    val posterPath: String? = null
)

fun NetworkTMDBSearchSeries.asExternalModel(): SearchData =
    SearchData(
        page = page,
        results = results?.asExternalModel(),
        totalPages = totalPages,
        totalResults = totalResults
    )

fun List<NetworkTMDBSearchSeriesResult>.asExternalModel(): List<Movie> =
    map {
        Movie(
            adult = it.adult,
            id = it.id,
            title = it.name,
            posterPath = it.posterPath
        )
    }