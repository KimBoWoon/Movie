package com.bowoon.data.repository

import com.bowoon.model.DarkThemeConfig
import com.bowoon.model.UserData
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {
    val userData: Flow<UserData>
    suspend fun updateDarkModeTheme(config: DarkThemeConfig)
}