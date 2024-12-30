package com.bowoon.data.repository

import com.bowoon.datastore.InternalDataSource
import com.bowoon.model.DarkThemeConfig
import com.bowoon.model.MainMenu
import com.bowoon.model.MovieDetail
import com.bowoon.model.UserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserDataRepositoryImpl @Inject constructor(
    private val datastore: InternalDataSource
) : UserDataRepository {
    override val userData: Flow<UserData> = datastore.userData

    override suspend fun updateDarkModeTheme(config: DarkThemeConfig) {
        datastore.updateDarkTheme(config)
    }

    override suspend fun updateBoxOfficeDate(date: String) {
        datastore.updateBoxOfficeDate(date)
    }

    override suspend fun updateMainMenu(mainMenu: MainMenu) {
        datastore.updateMainMenu(mainMenu)
    }

    override suspend fun updateFavoriteMovies(favoriteMovies: MovieDetail) {
        datastore.updateFavoriteMovies(favoriteMovies)
    }

    override suspend fun updateRegion(region: String) {
        datastore.updateRegion(region)
    }

    override suspend fun updateLanguage(language: String) {
        datastore.updateLanguage(language)
    }

    override suspend fun getUpdateDate(): String? = datastore.getUpdateDate()

    override suspend fun getRegion(): String? = datastore.getRegion()

    override suspend fun getLanguage(): String? = datastore.getLanguage()

    override suspend fun getFavoriteMovies(): List<MovieDetail> = datastore.getFavoriteMovies()
}