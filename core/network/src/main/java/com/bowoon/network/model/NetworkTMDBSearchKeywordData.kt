package com.bowoon.network.model

import com.bowoon.model.SearchKeyword
import com.bowoon.model.SearchKeywordData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTMDBSearchKeywordData(
    @SerialName("page")
    val page: Int? = null,
    @SerialName("results")
    val results: List<NetworkSearchKeyword>? = null,
    @SerialName("total_pages")
    val totalPages: Int? = null,
    @SerialName("total_results")
    val totalResults: Int? = null
)

@Serializable
data class NetworkSearchKeyword(
    @SerialName("id")
    val id: Int? = null,
    @SerialName("name")
    val name: String? = null
)

fun NetworkTMDBSearchKeywordData.asExternalModel(): SearchKeywordData =
    SearchKeywordData(
        page = page,
        results = results?.asExternalModel(),
        totalPages = totalPages,
        totalResults = totalResults
    )

fun List<NetworkSearchKeyword>.asExternalModel(): List<SearchKeyword> =
    map {
        SearchKeyword(
            id = it.id,
            name = it.name
        )
    }