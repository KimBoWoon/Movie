package com.bowoon.model

import kotlinx.serialization.Serializable

@Serializable
data class InternalData(
    val secureBaseUrl: String? = "",
    val isAdult: Boolean = true,
    val autoPlayTrailer: Boolean = true,
    val isDarkMode: DarkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
    val updateDate: String = "",
    val mainMenu: MainMenu = MainMenu(),
    val favoriteMovies: List<MovieDetail> = emptyList(),
    val region: String = "KR",
    val language: String = "ko",
    val imageQuality: String = "original"
)