package com.cheeke.surfy.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Reviews(
    val id: Int? = null,
    val page: Int? = null,
    val results: List<Review>? = null,
    val totalPages: Int? = null,
    val totalResults: Int? = null
) : Parcelable

@Serializable
@Parcelize
data class Review(
    val author: String? = null,
    val authorDetails: ReviewAuthorDetails? = null,
    val content: String? = null,
    val createdAt: String? = null,
    val id: String? = null,
    val updatedAt: String? = null,
    val url: String? = null
) : Parcelable

@Serializable
@Parcelize
data class ReviewAuthorDetails(
    val avatarPath: String? = null,
    val name: String? = null,
    val rating: Float? = null,
    val username: String? = null
) : Parcelable