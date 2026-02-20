package com.bowoon.model

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
    val voteAverage: Float?
}