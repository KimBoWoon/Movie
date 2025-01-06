package com.bowoon.model

import kotlinx.serialization.Serializable

@Serializable
data class MainMenu(
    val dailyBoxOffice: List<DailyBoxOffice> = emptyList(),
    val favoriteMovies: List<MovieDetail> = emptyList()
)