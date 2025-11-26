package com.bowoon.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
data class Genres(
    val genres: List<Genre>? = null
)

@Serializable
@Parcelize
data class Genre(
    val id: Int? = null,
    val name: String? = null
) : Parcelable