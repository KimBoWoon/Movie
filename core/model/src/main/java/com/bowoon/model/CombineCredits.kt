package com.bowoon.model

import kotlinx.serialization.Serializable

@Serializable
data class CombineCredits(
    val cast: List<CombineCreditsCast>? = null,
    val crew: List<CombineCreditsCrew>? = null,
    val id: Int? = null
)

@Serializable
data class CombineCreditsCast(
    val adult: Boolean? = null,
    val backdropPath: String? = null,
    val character: String? = null,
    val creditId: String? = null,
    val episodeCount: Int? = null,
    val firstAirDate: String? = null,
    val genreIds: List<Int>? = null,
    val id: Int? = null,
    val mediaType: String? = null,
    val name: String? = null,
    val order: Int? = null,
    val originCountry: List<String>? = null,
    val originalLanguage: String? = null,
    val originalName: String? = null,
    val originalTitle: String? = null,
    val overview: String? = null,
    val popularity: Double? = null,
    val posterPath: String? = null,
    val releaseDate: String? = null,
    val title: String? = null,
    val video: Boolean? = null,
    val voteAverage: Double? = null,
    val voteCount: Int? = null
)

@Serializable
data class CombineCreditsCrew(
    val adult: Boolean? = null,
    val backdropPath: String? = null,
    val creditId: String? = null,
    val department: String? = null,
    val episodeCount: Int? = null,
    val firstAirDate: String? = null,
    val genreIds: List<Int>? = null,
    val id: Int? = null,
    val job: String? = null,
    val mediaType: String? = null,
    val name: String? = null,
    val originCountry: List<String>? = null,
    val originalLanguage: String? = null,
    val originalName: String? = null,
    val originalTitle: String? = null,
    val overview: String? = null,
    val popularity: Double? = null,
    val posterPath: String? = null,
    val releaseDate: String? = null,
    val title: String? = null,
    val video: Boolean? = null,
    val voteAverage: Double? = null,
    val voteCount: Int? = null
)

fun CombineCredits.getRelatedMovie(): List<RelatedMovie> =
    (cast?.map {
        RelatedMovie(
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
            voteCount = it.voteCount,
            department = "",
            job = ""
        )
    } ?: emptyList()).plus(
        crew?.map {
            RelatedMovie(
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
        } ?: emptyList()
    ).sortedByDescending { it.releaseDate }