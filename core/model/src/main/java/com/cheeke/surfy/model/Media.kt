package com.cheeke.surfy.model

interface Media {
    val id: Int?
    val certification: String?
    val posterPath: String?
    val tagline: String?
    val title: String?
    val runtime: Int?
    val originalTitle: String?
    val genres: List<Genre>?
    val releaseDate: String?
    val firstAirDate: String?
    val lastAirDate: String?
    val voteAverage: Float?
    val mediaType: MediaType
}