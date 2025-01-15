package com.bowoon.data.repository

import com.bowoon.data.util.SyncManager
import com.bowoon.datastore.InternalDataSource
import com.bowoon.model.DarkThemeConfig
import com.bowoon.model.MainMenu
import com.bowoon.model.UserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserDataRepositoryImpl @Inject constructor(
    private val datastore: InternalDataSource,
    private val syncManager: SyncManager
) : UserDataRepository {
    override val userData: Flow<UserData> = datastore.userData

    override suspend fun updateDarkModeTheme(config: DarkThemeConfig) {
        datastore.updateDarkTheme(config)
    }

    override suspend fun updateMainOfDate(date: String) {
        datastore.updateMainOfDate(date)
    }

    override suspend fun updateMainMenu(mainMenu: MainMenu) {
        datastore.updateMainMenu(mainMenu)
    }

    override suspend fun updateRegion(region: String) {
        datastore.updateRegion(region)
        syncManager.requestSync()
    }

    override suspend fun updateLanguage(language: String) {
        datastore.updateLanguage(language)
        syncManager.requestSync()
    }

    override suspend fun updateImageQuality(imageQuality: String) {
        datastore.updateImageQuality(imageQuality)
        syncManager.requestSync()
    }

    override suspend fun getMainOfDate(): String = datastore.getMainOfDate()

    override suspend fun getRegion(): String = datastore.getRegion()

    override suspend fun getLanguage(): String = datastore.getLanguage()

    override suspend fun getImageQuality(): String = datastore.getImageQuality()
}