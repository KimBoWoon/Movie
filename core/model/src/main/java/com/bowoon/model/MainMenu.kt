package com.bowoon.model

import kotlinx.serialization.Serializable

@Serializable
data class MainMenu(
    val nowPlaying: List<MainMovie> = emptyList(),
    val upcomingMovies: List<MainMovie> = emptyList(),
    val favoriteMovies: List<MovieDetail> = emptyList()
)