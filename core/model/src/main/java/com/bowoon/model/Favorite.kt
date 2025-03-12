package com.bowoon.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Favorite(
    val id: Int? = null,
    val title: String? = null,
    val imagePath: String? = null
) : Parcelable