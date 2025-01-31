package com.bowoon.network.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTMDBMovieLists(
    @SerialName("id")
    val id: Int? = null,
    @SerialName("page")
    val page: Int? = null,
    @SerialName("results")
    val results: List<NetworkTMDBMovieListResult?>? = null,
    @SerialName("total_pages")
    val totalPages: Int? = null,
    @SerialName("total_results")
    val totalResults: Int? = null
)

@Serializable
data class NetworkTMDBMovieListResult(
    @SerialName("description")
    val description: String? = null,
    @SerialName("favorite_count")
    val favoriteCount: Int? = null,
    @SerialName("id")
    val id: Int? = null,
    @SerialName("iso_639_1")
    val iso6391: String? = null,
    @SerialName("item_count")
    val itemCount: Int? = null,
    @SerialName("list_type")
    val listType: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("poster_path")
    val posterPath: String? = null
)