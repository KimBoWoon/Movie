package com.bowoon.network.model


import com.bowoon.model.Images
import com.bowoon.model.Configuration
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTMDBConfiguration(
    @SerialName("change_keys")
    val changeKeys: List<String>? = null,
    @SerialName("images")
    val images: NetworkTMDBImagesConfiguration? = null
)

@Serializable
data class NetworkTMDBImagesConfiguration(
    @SerialName("backdrop_sizes")
    val backdropSizes: List<String>? = null,
    @SerialName("base_url")
    val baseUrl: String? = null,
    @SerialName("logo_sizes")
    val logoSizes: List<String>? = null,
    @SerialName("poster_sizes")
    val posterSizes: List<String>? = null,
    @SerialName("profile_sizes")
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

fun NetworkTMDBImagesConfiguration.asExternalModel(): Images =
    Images(
        backdropSizes = backdropSizes,
        baseUrl = baseUrl,
        logoSizes = logoSizes,
        posterSizes = posterSizes,
        profileSizes = profileSizes,
        secureBaseUrl = secureBaseUrl,
        stillSizes = stillSizes
    )