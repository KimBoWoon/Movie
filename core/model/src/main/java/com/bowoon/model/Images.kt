package com.bowoon.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Images(
    val backdrops: List<Image>? = null,
    val logos: List<Image>? = null,
    val posters: List<Image>? = null
) : Parcelable

@Serializable
@Parcelize
data class Image(
    val aspectRatio: Double? = null,
    val filePath: String? = null,
    val height: Int? = null,
    val iso6391: String? = null,
    val voteAverage: Double? = null,
    val voteCount: Int? = null,
    val width: Int? = null
) : Parcelable