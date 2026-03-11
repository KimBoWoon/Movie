package com.cheeke.surfy.network.model

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
data class NetworkTMDBSearchMovie(
    @SerialName("page")
    val page: Int? = null,
    @SerialName("results")
    val results: List<NetworkTMDBSearchMovieResult>? = null,
    @SerialName("total_pages")
    val totalPages: Int? = null,
    @SerialName("total_results")
    val totalResults: Int? = null
)

@Serializable
data class NetworkTMDBSearchMovieResult(
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
    val originCountry: List<String?>? = null,
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
    val voteCount: Int? = null
)

fun NetworkTMDBSearchMovie.asExternalModel(): SearchData =
    SearchData(
        page = page,
        results = results?.asExternalModel(),
        totalPages = totalPages,
        totalResults = totalResults
    )

fun List<NetworkTMDBSearchMovieResult>.asExternalModel(): List<Media> =
    map {
        when {
            it.mediaType?.equals(other = "movie", ignoreCase = true) == true -> Movie(
                genres = it.genreIds?.map { id -> Genre(id = id) },
                adult = it.adult,
                id = it.id,
                title = it.title,
                posterPath = it.posterPath,
                mediaType = MediaType.MOVIE
            )
            it.mediaType?.equals(other = "person", ignoreCase = true) == true -> People(
                genres = it.genreIds?.map { id -> Genre(id = id) },
                adult = it.adult,
                id = it.id,
                title = it.title,
                posterPath = it.profilePath,
                mediaType = MediaType.PEOPLE
            )
            it.mediaType?.equals(other = "tv", ignoreCase = true) == true -> Tv(
                genres = it.genreIds?.map { id -> Genre(id = id) },
                adult = it.adult,
                id = it.id,
                title = it.title,
                posterPath = it.posterPath,
                mediaType = MediaType.TV
            )
            it.mediaType?.equals(other = "series", ignoreCase = true) == true -> Series(
                genres = it.genreIds?.map { id -> Genre(id = id) },
                id = it.id,
                title = it.title,
                posterPath = it.posterPath,
                mediaType = MediaType.SERIES
            )
            else -> throw RuntimeException("Unknown media type: ${it.mediaType}")
        }
    }