package com.bowoon.network.model


import com.bowoon.model.SearchPeopleData
import com.bowoon.model.SearchPeopleKnownFor
import com.bowoon.model.SearchPeopleItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTMDBSearchPeople(
    @SerialName("page")
    val page: Int? = null,
    @SerialName("results")
    val results: List<NetworkTMDBSearchPeopleResult>? = null,
    @SerialName("total_pages")
    val totalPages: Int? = null,
    @SerialName("total_results")
    val totalResults: Int? = null
)

@Serializable
data class NetworkTMDBSearchPeopleResult(
    @SerialName("adult")
    val adult: Boolean? = null,
    @SerialName("gender")
    val gender: Int? = null,
    @SerialName("id")
    val id: Int? = null,
    @SerialName("known_for")
    val knownFor: List<NetworkTMDBSearchPeopleKnownFor>? = null,
    @SerialName("known_for_department")
    val knownForDepartment: String? = null,
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
data class NetworkTMDBSearchPeopleKnownFor(
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
    val voteAverage: Double? = null,
    @SerialName("vote_count")
    val voteCount: Int? = null
)

fun NetworkTMDBSearchPeople.asExternalModel(): SearchPeopleData =
    SearchPeopleData(
        page = page,
        results = results?.asExternalModel(),
        totalPages = totalPages,
        totalResults = totalResults
    )

@JvmName("NetworkTMDBSearchPeopleResultAsExternalModel")
fun List<NetworkTMDBSearchPeopleResult>.asExternalModel(): List<SearchPeopleItem> =
    map {
        SearchPeopleItem(
            adult = it.adult,
            gender = it.gender,
            id = it.id,
            knownFor = it.knownFor?.asExternalModel(),
            knownForDepartment = it.knownForDepartment,
            name = it.name,
            originalName = it.originalName,
            popularity = it.popularity,
            profilePath = it.profilePath,
            searchTitle = it.name,
            tmdbId = it.id,
            imagePath = it.profilePath
        )
    }

@JvmName("NetworkTMDBSearchPeopleKnownForAsExternalModel")
fun List<NetworkTMDBSearchPeopleKnownFor>.asExternalModel(): List<SearchPeopleKnownFor> =
    map {
        SearchPeopleKnownFor(
            adult = it.adult,
            backdropPath = it.backdropPath,
            genreIds = it.genreIds,
            id = it.id,
            mediaType = it.mediaType,
            originalLanguage = it.originalLanguage,
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