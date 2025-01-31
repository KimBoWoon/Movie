package com.bowoon.network.model


import com.bowoon.model.Certification
import com.bowoon.model.CertificationData
import com.bowoon.model.CertificationMap
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

fun NetworkTMDBCertificationData.asExternalModel(): CertificationData =
    CertificationData(
        certifications = certifications?.asExternalModel()
    )

fun NetworkTMDBCertificationMap.asExternalModel(): CertificationMap =
    CertificationMap(
        certifications = certifications?.asExternalModel()
    )

fun List<NetworkTMDBCertification>.asExternalModel(): List<Certification> =
    map {
        Certification(
            certification = it.certification,
            meaning = it.meaning,
            order = it.order
        )
    }

fun Map<String, List<NetworkTMDBCertification>>.asExternalModel(): Map<String, List<Certification>> =
    mapValues { it.value.asExternalModel() }