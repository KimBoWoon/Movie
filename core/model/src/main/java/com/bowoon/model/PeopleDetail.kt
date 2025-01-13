package com.bowoon.model

import kotlinx.serialization.Serializable

data class PeopleDetail(
    val adult: Boolean? = null,
    val alsoKnownAs: List<String>? = null,
    val biography: String? = null,
    val birthday: String? = null,
    val combineCredits: CombineCredits? = null,
    val deathday: String? = null,
    val externalIds: PeopleExternalIds? = null,
    val gender: Int? = null,
    val homepage: String? = null,
    val id: Int? = null,
    val images: List<PeopleImage>? = null,
    val imdbId: String? = null,
    val knownForDepartment: String? = null,
    val name: String? = null,
    val placeOfBirth: String? = null,
    val popularity: Double? = null,
    val profilePath: String? = null,
    val posterUrl: String? = null,
    val isFavorite: Boolean = false
)

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

data class RelatedMovie(
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
    val voteCount: Int? = null,
    val department: String? = null,
    val job: String? = null,
)

@Serializable
data class PeopleImage(
    val aspectRatio: Double? = null,
    val filePath: String? = null,
    val height: Int? = null,
    val iso6391: String? = null,
    val voteAverage: Double? = null,
    val voteCount: Int? = null,
    val width: Int? = null
)

@Serializable
data class PeopleExternalIds(
    val facebookId: String? = null,
    val freebaseId: String? = null,
    val freebaseMid: String? = null,
    val id: Int? = null,
    val imdbId: String? = null,
    val instagramId: String? = null,
    val tiktokId: String? = null,
    val tvrageId: Int? = null,
    val twitterId: String? = null,
    val wikidataId: String? = null,
    val youtubeId: String? = null
)