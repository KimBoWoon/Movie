package com.cheeke.surfy.network.model

import com.cheeke.surfy.model.Genre
import com.cheeke.surfy.model.Media
import com.cheeke.surfy.model.MediaType
import com.cheeke.surfy.model.SearchData
import com.cheeke.surfy.model.Tv
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTMDBSearchTv(
    @SerialName("page")
    val page: Int? = null,
    @SerialName("results")
    val results: List<NetworkTMDBSearchTvResult>? = null,
    @SerialName("total_pages")
    val totalPages: Int? = null,
    @SerialName("total_results")
    val totalResults: Int? = null
)

@Serializable
data class NetworkTMDBSearchTvResult(
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
    val voteAverage: Float? = null,
    @SerialName("vote_count")
    val voteCount: Int? = null
)

fun NetworkTMDBSearchTv.asExternalModel(): SearchData =
    SearchData(
        page = page,
        results = results?.asExternalModel(),
        totalPages = totalPages,
        totalResults = totalResults
    )

fun List<NetworkTMDBSearchTvResult>.asExternalModel(): List<Media> =
    map {
        Tv(
            genres = it.genreIds?.map { id -> Genre(id = id) },
            adult = it.adult,
            id = it.id,
            title = it.name,
            posterPath = it.posterPath,
            mediaType = MediaType.TV
        )
    }