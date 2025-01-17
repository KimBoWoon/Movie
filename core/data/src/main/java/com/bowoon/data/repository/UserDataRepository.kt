package com.bowoon.data.repository

import com.bowoon.model.DarkThemeConfig
import com.bowoon.model.MainMenu
import com.bowoon.model.UserData
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {
    val userData: Flow<UserData>
    suspend fun updateIsAdult(isAdult: Boolean)
    suspend fun updateDarkModeTheme(config: DarkThemeConfig)
    suspend fun updateMainOfDate(date: String)
    suspend fun updateMainMenu(mainMenu: MainMenu)
    suspend fun updateRegion(region: String)
    suspend fun updateLanguage(language: String)
    suspend fun updateImageQuality(imageQuality: String)
    suspend fun isAdult(): Boolean
    suspend fun getMainOfDate(): String
    suspend fun getRegion(): String
    suspend fun getLanguage(): String
    suspend fun getImageQuality(): String
}