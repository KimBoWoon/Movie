package com.bowoon.model


import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieGenreList(
    @SerialName("genres")
    val genres: List<MovieGenre>? = null
)

@Serializable
@Parcelize
data class MovieGenre(
    @SerialName("id")
    val id: Int? = null,
    @SerialName("name")
    val name: String? = null
) : Parcelable