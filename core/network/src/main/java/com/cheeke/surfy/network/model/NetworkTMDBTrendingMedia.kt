package com.cheeke.surfy.network.model

import com.cheeke.surfy.model.Genre
import com.cheeke.surfy.model.MediaType
import com.cheeke.surfy.model.TrendingMedia
import com.cheeke.surfy.model.TrendingMediaResult
import com.cheeke.surfy.model.TrendingPeopleKnownFor
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTMDBTrendingMedia(
    @SerialName("page")
    val page: Int? = null,
    @SerialName("results")
    val results: List<NetworkTMDBTrendingMediaResult>? = null,
    @SerialName("total_pages")
    val totalPages: Int? = null,
    @SerialName("total_results")
    val totalResults: Int? = null
)

@Serializable
data class NetworkTMDBTrendingMediaResult(
    // Movie
    @SerialName("adult")
    val adult: Boolean? = null,
    @SerialName("backdrop_path")
    val backdropPath: String? = null,
    @SerialName("genre_ids")
    val genreIds: List<Int>? = null,
    @SerialName("id")
    val id: Int? = null,
    @SerialName("media_type")
    val mediaType: String? = null,
    @SerialName("original_language")
    val originalLanguage: String? = null,
    @SerialName("original_title")
    val originalTitle: String? = null,
    @SerialName("overview")
    val overview: String? = null,
    @SerialName("popularity")
    val popularity: Double? = null,
    @SerialName("poster_path")
    val posterPath: String? = null,
    @SerialName("release_date")
    val releaseDate: String? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("video")
    val video: Boolean? = null,
    @SerialName("vote_average")
    val voteAverage: Float? = null,
    @SerialName("vote_count")
    val voteCount: Int? = null,
    @SerialName("genres")
    val genres: List<Genre>? = null,
    @SerialName("certification")
    val certification: String? = null,
    @SerialName("runtime")
    val runtime: Int? = null,
    @SerialName("tagline")
    val tagline: String? = null,

    // People
    @SerialName("gender")
    val gender: Int? = null,
    @SerialName("known_for")
    val knownFor: List<NetworkTMDBTrendingPeopleKnownFor>? = null,
    @SerialName("known_for_department")
    val knownForDepartment: String? = null,
    @SerialName("profile_path")
    val profilePath: String? = null,

    // TV
    @SerialName("first_air_date")
    val firstAirDate: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("origin_country")
    val originCountry: List<String>? = null,
    @SerialName("original_name")
    val originalName: String? = null
)

@Serializable
data class NetworkTMDBTrendingPeopleKnownFor(
    @SerialName("adult")
    val adult: Boolean? = null,
    @SerialName("backdrop_path")
    val backdropPath: String? = null,
    @SerialName("first_air_date")
    val firstAirDate: String? = null,
    @SerialName("genre_ids")
    val genreIds: List<Int>? = null,
    @SerialName("id")
    val id: Int? = null,
    @SerialName("media_type")
    val mediaType: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("origin_country")
    val originCountry: List<String>? = null,
    @SerialName("original_language")
    val originalLanguage: String? = null,
    @SerialName("original_name")
    val originalName: String? = null,
    @SerialName("original_title")
    val originalTitle: String? = null,
    @SerialName("overview")
    val overview: String? = null,
    @SerialName("popularity")
    val popularity: Double? = null,
    @SerialName("poster_path")
    val posterPath: String? = null,
    @SerialName("release_date")
    val releaseDate: String? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("video")
    val video: Boolean? = null,
    @SerialName("vote_average")
    val voteAverage: Float? = null,
    @SerialName("vote_count")
    val voteCount: Int? = null
)

fun NetworkTMDBTrendingMedia.asExternalModel(mediaType: MediaType): TrendingMedia = TrendingMedia(
    page = page,
    results = results?.asExternalModel(mediaType = mediaType),
    totalPages = totalPages,
    totalResults = totalResults
)

@JvmName("asExternalModelTrendingMediaResult")
fun List<NetworkTMDBTrendingMediaResult>.asExternalModel(mediaType: MediaType): List<TrendingMediaResult> = map {
    TrendingMediaResult(
        adult = it.adult,
        backdropPath = it.backdropPath,
        genreIds = it.genreIds,
        id = it.id,
        mediaType = mediaType,
        originalLanguage = it.originalLanguage,
        originalTitle = when (mediaType) {
            MediaType.MOVIE -> it.originalTitle
            MediaType.TV -> it.originalName
            MediaType.PEOPLE -> it.originalName
            else -> it.originalTitle
        },
        overview = it.overview,
        popularity = it.popularity,
        posterPath = when (mediaType) {
            MediaType.MOVIE -> it.posterPath
            MediaType.TV -> it.posterPath
            MediaType.PEOPLE -> it.profilePath
            else -> it.posterPath
        },
        releaseDate = it.releaseDate,
        title = when (mediaType) {
            MediaType.MOVIE -> it.title
            MediaType.TV -> it.name
            MediaType.PEOPLE -> it.name
            else -> it.title
        },
        video = it.video,
        voteAverage = it.voteAverage,
        voteCount = it.voteCount,
        genres = it.genres,
        certification = it.certification,
        runtime = it.runtime,
        tagline = it.tagline,
        gender = it.gender,
        knownFor = it.knownFor?.asExternalModel(),
        knownForDepartment = it.knownForDepartment,
        firstAirDate = it.firstAirDate,
        originCountry = it.originCountry
    )
}

@JvmName("asExternalModelTrendingPeopleKnownFor")
fun List<NetworkTMDBTrendingPeopleKnownFor>.asExternalModel(): List<TrendingPeopleKnownFor> = map {
    TrendingPeopleKnownFor(
        adult = it.adult,
        backdropPath = it.backdropPath,
        firstAirDate = it.firstAirDate,
        genreIds = it.genreIds,
        id = it.id,
        mediaType = it.mediaType,
        name = it.name,
        originCountry = it.originCountry,
        originalLanguage = it.originalLanguage,
        originalName = it.originalName,
        originalTitle = it.originalTitle,
        overview = it.overview,
        popularity = it.popularity,
        posterPath = it.posterPath,
        releaseDate = it.releaseDate,
        title = it.title,
        video = it.video,
        voteAverage = it.voteAverage,
        voteCount = it.voteCount
    )
}