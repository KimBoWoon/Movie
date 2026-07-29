package com.cheeke.surfy.network.impl.model

import com.cheeke.surfy.model.Region
import com.cheeke.surfy.model.Regions
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTMDBRegion(
    @SerialName(value = "results")
    val results: List<NetworkTMDBRegionResult>? = null
)

@Serializable
data class NetworkTMDBRegionResult(
    @SerialName(value = "english_name")
    val englishName: String? = null,
    @SerialName(value = "iso_3166_1")
    val iso31661: String? = null,
    @SerialName(value = "native_name")
    val nativeName: String? = null
)

fun NetworkTMDBRegion.asExternalModel(): Regions =
    Regions(
        results = results?.asExternalModel()
    )

fun List<NetworkTMDBRegionResult>.asExternalModel(): List<Region> =
    map {
        Region(
            englishName = it.englishName,
            iso31661 = it.iso31661,
            nativeName = it.nativeName
        )
    }