package com.bowoon.network.model

import com.bowoon.model.MovieReview
import com.bowoon.model.MovieReviewAuthorDetails
import com.bowoon.model.MovieReviews
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTMDBMovieReviews(
    @SerialName("id")
    val id: Int? = null,
    @SerialName("page")
    val page: Int? = null,
    @SerialName("results")
    val results: List<NetworkTMDBMovieResult>? = null,
    @SerialName("total_pages")
    val totalPages: Int? = null,
    @SerialName("total_results")
    val totalResults: Int? = null
)

@Serializable
data class NetworkTMDBMovieResult(
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
    val rating: Int? = null,
    @SerialName("username")
    val username: String? = null
)

fun NetworkTMDBMovieReviews.asExternalModel(): MovieReviews = MovieReviews(
    id = id,
    page = page,
    results = results?.asExternalModel(),
    totalPages = totalPages,
    totalResults = totalResults
)

fun List<NetworkTMDBMovieResult>.asExternalModel(): List<MovieReview> = map { movieReview ->
    MovieReview(
        author = movieReview.author,
        authorDetails = movieReview.authorDetails?.asExternalModel(),
        content = movieReview.content,
        createdAt = movieReview.createdAt,
        id = movieReview.id,
        updatedAt = movieReview.updatedAt,
        url = movieReview.url
    )
}

fun NetworkTMDBMovieAuthorDetails.asExternalModel(): MovieReviewAuthorDetails = MovieReviewAuthorDetails(
    avatarPath = avatarPath,
    name = name,
    rating = rating,
    username = username
)