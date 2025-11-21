package com.bowoon.model

import kotlinx.serialization.Serializable

@Serializable
data class MainMenu(
    val nowPlayingMovies: List<Movie> = emptyList(),
    val upComingMovies: List<Movie> = emptyList(),
    val nextWeekReleaseMovies: List<Movie> = emptyList()
)