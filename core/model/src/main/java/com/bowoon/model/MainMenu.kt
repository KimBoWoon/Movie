package com.bowoon.model

import kotlinx.serialization.Serializable

@Serializable
data class MainMenu(
    val nowPlaying: List<Movie> = emptyList(),
    val upcomingMovies: List<Movie> = emptyList(),
//    val favoriteMovies: List<MovieDetail> = emptyList()
)