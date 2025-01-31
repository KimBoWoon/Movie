package com.bowoon.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class MovieDetailImages(
    val backdrops: List<DetailImage>? = null,
    val logos: List<DetailImage>? = null,
    val posters: List<DetailImage>? = null
) : Parcelable

@Serializable
@Parcelize
data class DetailImage(
    val aspectRatio: Double? = null,
    val filePath: String? = null,
    val height: Int? = null,
    val iso6391: String? = null,
    val voteAverage: Double? = null,
    val voteCount: Int? = null,
    val width: Int? = null
) : Parcelable