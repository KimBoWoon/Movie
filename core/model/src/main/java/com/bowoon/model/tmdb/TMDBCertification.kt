package com.bowoon.model.tmdb


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TMDBCertificationData(
    @SerialName("certifications")
    val certifications: TMDBCertificationMap? = null
)

@Serializable
data class TMDBCertificationMap(
    @SerialName("KR")
    val certifications: Map<String, List<TMDBCertification>>? = null
)

@Serializable
data class TMDBCertification(
    @SerialName("certification")
    val certification: String? = null,
    @SerialName("meaning")
    val meaning: String? = null,
    @SerialName("order")
    val order: Int? = null
)