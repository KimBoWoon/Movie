package com.bowoon.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class MovieReviews(
    val id: Int? = null,
    val page: Int? = null,
    val results: List<MovieReview>? = null,
    val totalPages: Int? = null,
    val totalResults: Int? = null
) : Parcelable

@Serializable
@Parcelize
data class MovieReview(
    val author: String? = null,
    val authorDetails: MovieReviewAuthorDetails? = null,
    val content: String? = null,
    val createdAt: String? = null,
    val id: String? = null,
    val updatedAt: String? = null,
    val url: String? = null
) : Parcelable

@Serializable
@Parcelize
data class MovieReviewAuthorDetails(
    val avatarPath: String? = null,
    val name: String? = null,
    val rating: Int? = null,
    val username: String? = null
) : Parcelable