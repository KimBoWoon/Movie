package com.bowoon.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class ProductionCompany(
    val id: Int? = null,
    val logoPath: String? = null,
    val name: String? = null,
    val originCountry: String? = null
) : Parcelable

@Serializable
@Parcelize
data class ProductionCountry(
    val iso31661: String? = null,
    val name: String? = null
) : Parcelable