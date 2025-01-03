package com.bowoon.data.repository

import com.bowoon.model.DarkThemeConfig
import com.bowoon.model.MainMenu
import com.bowoon.model.MovieDetail
import com.bowoon.model.PosterSize
import com.bowoon.model.UserData
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {
    val userData: Flow<UserData>
    suspend fun updateDarkModeTheme(config: DarkThemeConfig)
    suspend fun updateMainOfDate(date: String)
    suspend fun updateMainMenu(mainMenu: MainMenu)
    suspend fun updateFavoriteMovies(movie: MovieDetail)
    suspend fun updateRegion(region: String)
    suspend fun updateLanguage(language: String)
    suspend fun updateImageQuality(imageQuality: String)
    suspend fun getMainOfDate(): String
    suspend fun getRegion(): String
    suspend fun getLanguage(): String
    suspend fun getFavoriteMovies(): List<MovieDetail>
    suspend fun getImageQuality(): String?
}