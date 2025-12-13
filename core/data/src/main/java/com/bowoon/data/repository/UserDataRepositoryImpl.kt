package com.bowoon.data.repository

import com.bowoon.data.util.SyncManager
import com.bowoon.datastore.InternalDataSource
import com.bowoon.model.DarkThemeConfig
import com.bowoon.model.InternalData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserDataRepositoryImpl @Inject constructor(
    private val datastore: InternalDataSource,
    private val syncManager: SyncManager
) : UserDataRepository {
    override val internalData: Flow<InternalData> = datastore.userData

    override suspend fun updateIsAdult(value: Boolean) {
        datastore.updateIsAdult(value = value)
    }

    override suspend fun updateAutoPlayTrailer(value: Boolean) {
        datastore.updateAutoPlayTrailer(value = value)
    }

    override suspend fun updateIsDarkMode(darkThemeConfig: DarkThemeConfig) {
        datastore.updateIsDarkMode(darkThemeConfig = darkThemeConfig)
    }

    override suspend fun updateMainDate(value: String) {
        datastore.updateMainDate(value = value)
    }

    override suspend fun updateRegion(value: String) {
        datastore.updateRegion(value = value)
        syncManager.requestSync()
    }

    override suspend fun updateLanguage(value: String) {
        datastore.updateLanguage(value = value)
        syncManager.requestSync()
    }

    override suspend fun updateImageQuality(value: String) {
        datastore.updateImageQuality(value = value)
    }

    override suspend fun updateNoShowToday(value: String) {
        datastore.updateNoShowToday(value = value)
    }

    override suspend fun updateFCMToken(token: String) {
        datastore.updateFCMToken(token)
    }

    override suspend fun getFCMToken(): String = datastore.getFCMToken()
}