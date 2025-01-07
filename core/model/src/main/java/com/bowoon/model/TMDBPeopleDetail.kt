package com.bowoon.model

data class TMDBPeopleDetail(
    val adult: Boolean? = null,
    val alsoKnownAs: List<String>? = null,
    val biography: String? = null,
    val birthday: String? = null,
    val deathday: String? = null,
    val gender: Int? = null,
    val homepage: String? = null,
    val id: Int? = null,
    val images: TMDBPeopleImages? = null,
    val imdbId: String? = null,
    val knownForDepartment: String? = null,
    val name: String? = null,
    val placeOfBirth: String? = null,
    val popularity: Double? = null,
    val profilePath: String? = null
)

data class TMDBPeopleImages(
    val profiles: List<TMDBPeopleProfile>? = null
)

data class TMDBPeopleProfile(
    val aspectRatio: Double? = null,
    val filePath: String? = null,
    val height: Int? = null,
    val iso6391: String? = null,
    val voteAverage: Double? = null,
    val voteCount: Int? = null,
    val width: Int? = null
)