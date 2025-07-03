package com.bowoon.model

import kotlinx.serialization.Serializable

@Serializable
data class MainMenu(
    val nowPlaying: List<DisplayItem> = emptyList(),
    val upComingMovies: List<DisplayItem> = emptyList()
)