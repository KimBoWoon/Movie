package com.bowoon.model

import kotlinx.serialization.Serializable

data class CertificationData(
    val certifications: CertificationMap? = null
)

data class CertificationMap(
    val certifications: Map<String, List<Certification>>? = null
)

@Serializable
data class Certification(
    val certification: String? = null,
    val meaning: String? = null,
    val order: Int? = null
)