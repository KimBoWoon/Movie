package com.bowoon.model

interface Media {
    val id: Int?
    val posterPath: String?
    val title: String?
    val originalTitle: String?
    val genres: List<Genre>?
    val releaseDate: String?
}