package com.cheeke.surfy.network.impl.model

import com.cheeke.surfy.model.Review
import com.cheeke.surfy.model.ReviewAuthorDetails
import com.cheeke.surfy.model.Reviews
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTMDBMovieReviews(
    @SerialName("id")
    val id: Int? = null,
    @SerialName("page")
    val page: Int? = null,
    @SerialName("results")
    val results: List<NetworkTMDBMovieReview>? = null,
    @SerialName("total_pages")
    val totalPages: Int? = null,
    @SerialName("total_results")
    val totalResults: Int? = null
)

@Serializable
data class NetworkTMDBMovieReview(
    @SerialName("author")
    val author: String? = null,
    @SerialName("author_details")
    val authorDetails: NetworkTMDBMovieAuthorDetails? = null,
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
data class NetworkTMDBMovieAuthorDetails(
    @SerialName("avatar_path")
    val avatarPath: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("rating")
    val rating: Float? = null,
    @SerialName("username")
    val username: String? = null
)

fun NetworkTMDBMovieReviews.asExternalModel(): Reviews = Reviews(
    id = id,
    page = page,
    results = results?.asExternalModel(),
    totalPages = totalPages,
    totalResults = totalResults
)

fun List<NetworkTMDBMovieReview>.asExternalModel(): List<Review> = map { movieReview ->
    Review(
        author = movieReview.author,
        authorDetails = movieReview.authorDetails?.asExternalModel(),
        content = movieReview.content,
        createdAt = movieReview.createdAt,
        id = movieReview.id,
        updatedAt = movieReview.updatedAt,
        url = movieReview.url
    )
}

fun NetworkTMDBMovieAuthorDetails.asExternalModel(): ReviewAuthorDetails = ReviewAuthorDetails(
    avatarPath = avatarPath,
    name = name,
    rating = rating,
    username = username
)