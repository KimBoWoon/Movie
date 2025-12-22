package com.bowoon.model

import kotlinx.serialization.Serializable

@Serializable
data class MainMenu(
    val nextWeekReleaseMovies: List<Movie> = emptyList()
)