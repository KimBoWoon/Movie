package com.bowoon.network.model


import com.bowoon.model.TMDBRegion
import com.bowoon.model.TMDBRegionResult
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTMDBRegion(
    @SerialName("results")
    val results: List<NetworkTMDBRegionResult>? = null
)

@Serializable
data class NetworkTMDBRegionResult(
    @SerialName("english_name")
    val englishName: String? = null,
    @SerialName("iso_3166_1")
    val iso31661: String? = null,
    @SerialName("native_name")
    val nativeName: String? = null
)

fun NetworkTMDBRegion.asExternalModel(): TMDBRegion =
    TMDBRegion(
        results = results?.asExternalModel()
    )

fun List<NetworkTMDBRegionResult>.asExternalModel(): List<TMDBRegionResult> =
    map {
        TMDBRegionResult(
            englishName = it.englishName,
            iso31661 = it.iso31661,
            nativeName = it.nativeName
        )
    }