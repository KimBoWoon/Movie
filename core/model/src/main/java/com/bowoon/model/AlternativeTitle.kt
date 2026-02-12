package com.bowoon.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class AlternativeTitle(
    val iso31661: String? = null,
    val title: String? = null,
    val type: String? = null
) : Parcelable