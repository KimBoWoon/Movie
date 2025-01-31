package com.bowoon.network.model


import com.bowoon.model.RegionList
import com.bowoon.model.Region
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

fun NetworkTMDBRegion.asExternalModel(): RegionList =
    RegionList(
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