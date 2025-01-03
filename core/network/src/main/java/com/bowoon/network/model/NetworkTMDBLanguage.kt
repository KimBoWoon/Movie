package com.bowoon.network.model

import com.bowoon.model.TMDBLanguageItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTMDBLanguageItem(
    @SerialName("english_name")
    val englishName: String? = null,
    @SerialName("iso_639_1")
    val iso6391: String? = null,
    @SerialName("name")
    val name: String? = null
)

fun List<NetworkTMDBLanguageItem>.asExternalModel(): List<TMDBLanguageItem> =
    map {
        TMDBLanguageItem(
            englishName = it.englishName,
            iso6391 = it.iso6391,
            name = it.name
        )
    }