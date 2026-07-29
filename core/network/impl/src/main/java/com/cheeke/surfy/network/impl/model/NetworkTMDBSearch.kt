package com.cheeke.surfy.network.impl.model

import com.cheeke.surfy.model.Genre
import com.cheeke.surfy.model.Media
import com.cheeke.surfy.model.MediaType
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.model.People
import com.cheeke.surfy.model.SearchData
import com.cheeke.surfy.model.Series
import com.cheeke.surfy.model.Tv
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTMDBSearch(
    @SerialName(value = "page")
    val page: Int? = null,
    @SerialName(value = "results")
    val results: List<NetworkTMDBSearchResult>? = null,
    @SerialName(value = "total_pages")
    val totalPages: Int? = null,
    @SerialName(value = "total_results")
    val totalResults: Int? = null
)

@Serializable
data class NetworkTMDBSearchResult(
    // Movie
    @SerialName(value = "adult")
    val adult: Boolean? = null,
    @SerialName("backdrop_path")
    val backdropPath: String? = null,
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
    @SerialName("original_title")
    val originalTitle: String? = null,
    @SerialName("original_name")
    val originalName: String? = null,
    @SerialName("overview")
    val overview: String? = null,
    @SerialName("popularity")
    val popularity: Double? = null,
    @SerialName("poster_path")
    val posterPath: String? = null,
    @SerialName("profile_path")
    val profilePath: String? = null,
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

    // People
    @SerialName("gender")
    val gender: Int? = null,
    @SerialName("known_for")
    val knownFor: List<NetworkTMDBSearchPeopleKnownFor>? = null,
    @SerialName("known_for_department")
    val knownForDepartment: String? = null,

    // Tv
    @SerialName("first_air_date")
    val firstAirDate: String? = null,

    // Series
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
    val voteAverage: Float? = null,
    @SerialName("vote_count")
    val voteCount: Int? = null
)

fun NetworkTMDBSearch.asExternalModel(mediaType: MediaType?): SearchData =
    SearchData(
        page = page,
        results = results?.asExternalModel(mediaType),
        totalPages = totalPages,
        totalResults = totalResults
    )

fun List<NetworkTMDBSearchResult>.asExternalModel(mediaType: MediaType?): List<Media> = map {
    val resolvedMediaType = it.mediaType.toMediaType().let { type ->
        if (type == MediaType.NONE) mediaType ?: MediaType.NONE else type
    }

    when (resolvedMediaType) {
        MediaType.NONE -> {
            Movie(
                genres = it.genreIds?.map { id -> Genre(id = id) },
                adult = it.adult,
                id = it.id,
                title = it.title,
                posterPath = it.posterPath,
                mediaType = MediaType.MOVIE
            )
        }
        MediaType.MOVIE -> {
            Movie(
                genres = it.genreIds?.map { id -> Genre(id = id) },
                adult = it.adult,
                id = it.id,
                title = it.title,
                posterPath = it.posterPath,
                mediaType = MediaType.MOVIE
            )
        }
        MediaType.TV -> {
            Tv(
                genres = it.genreIds?.map { id -> Genre(id = id) },
                adult = it.adult,
                id = it.id,
                title = it.title,
                posterPath = it.posterPath,
                mediaType = MediaType.TV
            )
        }
        MediaType.PEOPLE -> {
            People(
                genres = it.genreIds?.map { id -> Genre(id = id) },
                adult = it.adult,
                id = it.id,
                title = it.title,
                posterPath = it.profilePath,
                mediaType = MediaType.PEOPLE
            )
        }
        MediaType.SERIES -> {
            Series(
                genres = it.genreIds?.map { id -> Genre(id = id) },
                id = it.id,
                title = it.title,
                posterPath = it.posterPath,
                mediaType = MediaType.SERIES
            )
        }
    }
}

private fun String?.toMediaType(): MediaType =
    when (this?.lowercase()) {
        "movie" -> MediaType.MOVIE
        "tv" -> MediaType.TV
        "person" -> MediaType.PEOPLE
        "series" -> MediaType.SERIES
        else -> MediaType.NONE
    }