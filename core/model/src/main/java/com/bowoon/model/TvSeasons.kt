package com.bowoon.model

data class TvSeasons(
    val airDate: String? = null,
    val credits: Credits? = null,
    val episodes: List<TvEpisode>? = null,
    val _id: String? = null,
    val id: Int? = null,
    val images: Images? = null,
    val name: String? = null,
    val networks: List<TvNetwork>? = null,
    val overview: String? = null,
    val posterPath: String? = null,
    val seasonNumber: Int? = null,
    val videos: Videos? = null,
    val voteAverage: Float? = null
)

data class TvEpisode(
    val airDate: String? = null,
    val crew: List<Crew>? = null,
    val episodeNumber: Int? = null,
    val episodeType: String? = null,
    val guestStars: List<TvGuestStar>? = null,
    val id: Int? = null,
    val name: String? = null,
    val overview: String? = null,
    val productionCode: String? = null,
    val runtime: Int? = null,
    val seasonNumber: Int? = null,
    val showId: Int? = null,
    val stillPath: String? = null,
    val voteAverage: Float? = null,
    val voteCount: Int? = null
)

data class TvGuestStar(
    val adult: Boolean? = null,
    val character: String? = null,
    val creditId: String? = null,
    val gender: Int? = null,
    val id: Int? = null,
    val knownForDepartment: String? = null,
    val name: String? = null,
    val order: Int? = null,
    val originalName: String? = null,
    val popularity: Double? = null,
    val profilePath: String? = null
)