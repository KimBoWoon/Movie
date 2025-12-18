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

    override suspend fun updateIsAutoPlayTrailer(value: Boolean) {
        datastore.updateIsAutoPlayTrailer(value = value)
    }

    override suspend fun updateDarkMode(darkThemeConfig: DarkThemeConfig) {
        datastore.updateDarkMode(darkThemeConfig = darkThemeConfig)
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

    override suspend fun updateShowNextReleaseMoviesDate(value: String) {
        datastore.updateShowNextReleaseMoviesDate(value = value)
    }

    override suspend fun updateSecureBaseUrl(value: String) {
        datastore.updateSecureBaseUrl(value = value)
    }

    override suspend fun getSecureBaseUrl(): String =
        datastore.getSecureBaseUrl()

    override suspend fun updateFCMToken(token: String) {
        datastore.updateFCMToken(token)
    }

    override suspend fun getIsAdult(): Boolean =
        datastore.getIsAdult()

    override suspend fun getAutoPlayTrailer(): Boolean =
        datastore.getAutoPlayTrailer()

    override suspend fun getDarkMode(): DarkThemeConfig =
        datastore.getDarkMode()

    override suspend fun getMainDate(): String =
        datastore.getMainDate()

    override suspend fun getRegion(): String =
        datastore.getRegion()

    override suspend fun getLanguage(): String =
        datastore.getLanguage()

    override suspend fun getImageQuality(): String =
        datastore.getImageQuality()

    override suspend fun getShowNextReleaseMoviesDate(): String =
        datastore.getShowNextReleaseMoviesDate()

    override suspend fun getFCMToken(): String = datastore.getFCMToken()
}