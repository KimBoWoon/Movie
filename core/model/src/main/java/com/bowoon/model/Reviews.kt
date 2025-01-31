package com.bowoon.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Reviews(
    val id: Int? = null,
    val page: Int? = null,
    val results: List<ReviewsResult?>? = null,
    val totalPages: Int? = null,
    val totalResults: Int? = null
) : Parcelable

@Serializable
@Parcelize
data class ReviewsResult(
    val author: String? = null,
    val authorDetails: AuthorDetails? = null,
    val content: String? = null,
    val createdAt: String? = null,
    val id: String? = null,
    val updatedAt: String? = null,
    val url: String? = null
) : Parcelable

@Serializable
@Parcelize
data class AuthorDetails(
    val avatarPath: String? = null,
    val name: String? = null,
    val rating: Int? = null,
    val username: String? = null
) : Parcelable