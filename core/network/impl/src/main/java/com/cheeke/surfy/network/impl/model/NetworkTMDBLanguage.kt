package com.cheeke.surfy.network.impl.model

import com.cheeke.surfy.model.Language
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTMDBLanguageItem(
    @SerialName(value = "english_name")
    val englishName: String? = null,
    @SerialName(value = "iso_639_1")
    val iso6391: String? = null,
    @SerialName(value = "name")
    val name: String? = null
)

fun List<NetworkTMDBLanguageItem>.asExternalModel(): List<Language> =
    map {
        Language(
            englishName = it.englishName,
            iso6391 = it.iso6391,
            name = it.name
        )
    }