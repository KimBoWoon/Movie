package com.bowoon.network.model


import com.bowoon.model.tmdb.TMDBCertification
import com.bowoon.model.tmdb.TMDBCertificationData
import com.bowoon.model.tmdb.TMDBCertificationMap
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTMDBCertificationData(
    @SerialName("certifications")
    val certifications: NetworkTMDBCertificationMap? = null
)

@Serializable
data class NetworkTMDBCertificationMap(
    val certifications: Map<String, List<NetworkTMDBCertification>>? = null
)

@Serializable
data class NetworkTMDBCertification(
    @SerialName("certification")
    val certification: String? = null,
    @SerialName("meaning")
    val meaning: String? = null,
    @SerialName("order")
    val order: Int? = null
)

fun NetworkTMDBCertificationData.asExternalModel(): TMDBCertificationData =
    TMDBCertificationData(
        certifications = certifications?.asExternalModel()
    )

fun NetworkTMDBCertificationMap.asExternalModel(): TMDBCertificationMap =
    TMDBCertificationMap(
        certifications = certifications?.asExternalModel()
    )

fun List<NetworkTMDBCertification>.asExternalModel(): List<TMDBCertification> =
    map {
        TMDBCertification(
            certification = it.certification,
            meaning = it.meaning,
            order = it.order
        )
    }

fun Map<String, List<NetworkTMDBCertification>>.asExternalModel(): Map<String, List<TMDBCertification>> =
    mapValues { it.value.asExternalModel() }