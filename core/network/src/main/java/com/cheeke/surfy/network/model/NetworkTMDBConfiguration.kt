package com.cheeke.surfy.network.model

import com.cheeke.surfy.model.Configuration
import com.cheeke.surfy.model.ImageInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTMDBConfiguration(
    @SerialName(value = "change_keys")
    val changeKeys: List<String>? = null,
    @SerialName(value = "images")
    val images: NetworkTMDBImagesConfiguration? = null
)

@Serializable
data class NetworkTMDBImagesConfiguration(
    @SerialName(value = "backdrop_sizes")
    val backdropSizes: List<String>? = null,
    @SerialName(value = "base_url")
    val baseUrl: String? = null,
    @SerialName(value = "logo_sizes")
    val logoSizes: List<String>? = null,
    @SerialName(value = "poster_sizes")
    val posterSizes: List<String>? = null,
    @SerialName(value = "profile_sizes")
    val profileSizes: List<String>? = null,
    @SerialName("secure_base_url")
    val secureBaseUrl: String? = null,
    @SerialName("still_sizes")
    val stillSizes: List<String>? = null
)

fun NetworkTMDBConfiguration.asExternalModel(): Configuration =
    Configuration(
        changeKeys = changeKeys,
        images = images?.asExternalModel()
    )

fun NetworkTMDBImagesConfiguration.asExternalModel(): ImageInfo =
    ImageInfo(
        backdropSizes = backdropSizes,
        baseUrl = baseUrl,
        logoSizes = logoSizes,
        posterSizes = posterSizes,
        profileSizes = profileSizes,
        secureBaseUrl = secureBaseUrl,
        stillSizes = stillSizes
    )