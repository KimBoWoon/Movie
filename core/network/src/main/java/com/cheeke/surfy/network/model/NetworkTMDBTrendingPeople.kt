package com.cheeke.surfy.network.model


import com.cheeke.surfy.model.MediaType
import com.cheeke.surfy.model.TrendingPeople
import com.cheeke.surfy.model.TrendingPeopleKnownFor
import com.cheeke.surfy.model.TrendingPeopleResult
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTMDBTrendingPeople(
    @SerialName("page")
    val page: Int? = null,
    @SerialName("results")
    val results: List<NetworkTMDBTrendingPeopleResult>? = null,
    @SerialName("total_pages")
    val totalPages: Int? = null,
    @SerialName("total_results")
    val totalResults: Int? = null
)

@Serializable
data class NetworkTMDBTrendingPeopleResult(
    @SerialName("adult")
    val adult: Boolean? = null,
    @SerialName("gender")
    val gender: Int? = null,
    @SerialName("id")
    val id: Int? = null,
    @SerialName("known_for")
    val knownFor: List<NetworkTMDBTrendingPeopleKnownFor>? = null,
    @SerialName("known_for_department")
    val knownForDepartment: String? = null,
    @SerialName("media_type")
    val mediaType: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("original_name")
    val originalName: String? = null,
    @SerialName("popularity")
    val popularity: Double? = null,
    @SerialName("profile_path")
    val profilePath: String? = null
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

fun NetworkTMDBTrendingPeople.asExternalModel(): TrendingPeople = TrendingPeople(
    page = page,
    results = results?.asExternalModel(),
    totalPages = totalPages,
    totalResults = totalResults
)

@JvmName("asExternalModelTrendingPeopleResult")
fun List<NetworkTMDBTrendingPeopleResult>.asExternalModel(): List<TrendingPeopleResult> = map {
    TrendingPeopleResult(
        adult = it.adult,
        gender = it.gender,
        id = it.id,
        knownFor = it.knownFor?.asExternalModel(),
        knownForDepartment = it.knownForDepartment,
        mediaType = when {
            it.mediaType?.equals(other = "tv", ignoreCase = true) == true -> MediaType.TV
            it.mediaType?.equals(other = "movie", ignoreCase = true) == true -> MediaType.MOVIE
            else -> MediaType.NONE
        },
        title = it.name,
        originalTitle = it.originalName,
        popularity = it.popularity,
        posterPath = it.profilePath
    )
}

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