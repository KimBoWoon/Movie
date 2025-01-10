package com.bowoon.model

import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    val isDarkMode: DarkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
    val updateDate: String = "",
    val mainMenu: MainMenu = MainMenu(),
    val favoriteMovies: List<MovieDetail> = emptyList(),
    val region: String = "KR",
    val language: String = "ko",
    val imageQuality: String = "original",
    val myData: MyData? = null
)