package com.cheeke.surfy.network.model

import com.cheeke.surfy.model.Review
import com.cheeke.surfy.model.ReviewAuthorDetails
import com.cheeke.surfy.model.Reviews
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTMDBTvReviews(
    @SerialName(value = "id")
    val id: Int? = null,
    @SerialName(value = "page")
    val page: Int? = null,
    @SerialName(value = "results")
    val results: List<NetworkTMDBTvReviewsResult>? = null,
    @SerialName(value = "total_pages")
    val totalPages: Int? = null,
    @SerialName(value = "total_results")
    val totalResults: Int? = null
)

@Serializable
data class NetworkTMDBTvReviewsResult(
    @SerialName(value = "author")
    val author: String? = null,
    @SerialName("author_details")
    val authorDetails: NetworkTMDBTvReviewsAuthorDetails? = null,
    @SerialName("content")
    val content: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("id")
    val id: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null,
    @SerialName("url")
    val url: String? = null
)

@Serializable
data class NetworkTMDBTvReviewsAuthorDetails(
    @SerialName("avatar_path")
    val avatarPath: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("rating")
    val rating: Float? = null,
    @SerialName("username")
    val username: String? = null
)

fun NetworkTMDBTvReviews.asExternalModel(): Reviews = Reviews(
    id = id,
    page = page,
    results = results?.asExternalModel(),
    totalPages = totalPages,
    totalResults = totalResults
)

fun List<NetworkTMDBTvReviewsResult>.asExternalModel(): List<Review> = map {
    Review(
        author = it.author,
        authorDetails = it.authorDetails?.asExternalModel(),
        content = it.content,
        createdAt = it.createdAt,
        id = it.id,
        updatedAt = it.updatedAt,
        url = it.url
    )
}

fun NetworkTMDBTvReviewsAuthorDetails.asExternalModel(): ReviewAuthorDetails = ReviewAuthorDetails(
    avatarPath = avatarPath,
    name = name,
    rating = rating,
    username = username
)