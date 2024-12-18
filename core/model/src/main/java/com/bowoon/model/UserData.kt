package com.bowoon.model

import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    val isDarkMode: DarkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM
)