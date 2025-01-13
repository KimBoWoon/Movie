package com.bowoon.model

import kotlinx.serialization.Serializable

@Serializable
data class MainMenu(
//    val dailyBoxOffice: List<DailyBoxOffice> = emptyList(),
//    val upcomingMovies: List<UpComingResult> = emptyList(),
    val dailyBoxOffice: List<MainMovie> = emptyList(),
    val upcomingMovies: List<MainMovie> = emptyList()
)