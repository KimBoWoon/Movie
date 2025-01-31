package com.bowoon.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class MovieLists(
    val id: Int? = null,
    val page: Int? = null,
    val results: List<MovieListResult?>? = null,
    val totalPages: Int? = null,
    val totalResults: Int? = null
) : Parcelable

@Serializable
@Parcelize
data class MovieListResult(
    val description: String? = null,
    val favoriteCount: Int? = null,
    val id: Int? = null,
    val iso6391: String? = null,
    val itemCount: Int? = null,
    val listType: String? = null,
    val name: String? = null,
    val posterPath: String? = null
) : Parcelable