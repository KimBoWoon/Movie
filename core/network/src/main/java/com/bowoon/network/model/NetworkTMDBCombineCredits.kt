package com.bowoon.network.model


import com.bowoon.model.TMDBCombineCredits
import com.bowoon.model.TMDBCombineCreditsCast
import com.bowoon.model.TMDBCombineCreditsCrew
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTMDBCombineCredits(
    @SerialName("cast")
    val cast: List<NetworkTMDBCombineCreditsCast>? = null,
    @SerialName("crew")
    val crew: List<NetworkTMDBCombineCreditsCrew>? = null,
    @SerialName("id")
    val id: Int? = null
)

@Serializable
data class NetworkTMDBCombineCreditsCast(
    @SerialName("adult")
    val adult: Boolean? = null,
    @SerialName("backdrop_path")
    val backdropPath: String? = null,
    @SerialName("character")
    val character: String? = null,
    @SerialName("credit_id")
    val creditId: String? = null,
    @SerialName("episode_count")
    val episodeCount: Int? = null,
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
    @SerialName("order")
    val order: Int? = null,
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
    val voteAverage: Double? = null,
    @SerialName("vote_count")
    val voteCount: Int? = null
)

@Serializable
data class NetworkTMDBCombineCreditsCrew(
    @SerialName("adult")
    val adult: Boolean? = null,
    @SerialName("backdrop_path")
    val backdropPath: String? = null,
    @SerialName("credit_id")
    val creditId: String? = null,
    @SerialName("department")
    val department: String? = null,
    @SerialName("episode_count")
    val episodeCount: Int? = null,
    @SerialName("first_air_date")
    val firstAirDate: String? = null,
    @SerialName("genre_ids")
    val genreIds: List<Int>? = null,
    @SerialName("id")
    val id: Int? = null,
    @SerialName("job")
    val job: String? = null,
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
    val voteAverage: Double? = null,
    @SerialName("vote_count")
    val voteCount: Int? = null
)

@JvmName("NetworkTMDBCombine")
fun NetworkTMDBCombineCredits.asExternalModel(): TMDBCombineCredits =
    TMDBCombineCredits(
        cast = cast?.asExternalModel(),
        crew = crew?.asExternalModel(),
        id = id
    )

@JvmName("NetworkTMDBCombineCreditsCast")
fun List<NetworkTMDBCombineCreditsCast>.asExternalModel(): List<TMDBCombineCreditsCast> =
    map {
        TMDBCombineCreditsCast(
            adult = it.adult,
            backdropPath = it.backdropPath,
            character = it.character,
            creditId = it.creditId,
            episodeCount = it.episodeCount,
            firstAirDate = it.firstAirDate,
            genreIds = it.genreIds,
            id = it.id,
            mediaType = it.mediaType,
            name = it.name,
            order = it.order,
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

@JvmName("NetworkTMDBCombineCreditsCrew")
fun List<NetworkTMDBCombineCreditsCrew>.asExternalModel(): List<TMDBCombineCreditsCrew> =
    map {
        TMDBCombineCreditsCrew(
            adult = it.adult,
            backdropPath = it.backdropPath,
            creditId = it.creditId,
            episodeCount = it.episodeCount,
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
            voteCount = it.voteCount,
            department = it.department,
            job = it.job
        )
    }