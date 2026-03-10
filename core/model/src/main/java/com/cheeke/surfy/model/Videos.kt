package com.cheeke.surfy.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Videos(
    val results: List<VideoInfo>? = null
) : Parcelable

@Serializable
@Parcelize
data class VideoInfo(
    val id: String? = null,
    val iso31661: String? = null,
    val iso6391: String? = null,
    val key: String? = null,
    val name: String? = null,
    val official: Boolean? = null,
    val publishedAt: String? = null,
    val site: String? = null,
    val size: Int? = null,
    val type: String? = null
) : Parcelable