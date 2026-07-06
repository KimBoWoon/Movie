package com.cheeke.surfy.data.repository

import com.cheeke.surfy.data.util.SyncManager
import com.cheeke.surfy.datastore.InternalDataSource
import com.cheeke.surfy.model.DarkThemeConfig
import com.cheeke.surfy.model.InternalData
import io.reactivex.rxjava3.core.Flowable
import javax.inject.Inject

class UserDataRepositoryImpl @Inject constructor(
    private val datastore: InternalDataSource,
    private val syncManager: SyncManager
) : UserDataRepository {
    override val internalData: Flowable<InternalData> = datastore.userData

    override fun updateIsAdult(value: Boolean) {
        datastore.updateIsAdult(value = value)
    }

    override fun updateIsAutoPlayTrailer(value: Boolean) {
        datastore.updateIsAutoPlayTrailer(value = value)
    }

    override fun updateDarkMode(darkThemeConfig: DarkThemeConfig) {
        datastore.updateDarkMode(darkThemeConfig = darkThemeConfig)
    }

    override fun updateMainDate(value: String) {
        datastore.updateMainDate(value = value)
    }

    override fun updateRegion(value: String) {
        datastore.updateRegion(value = value)
        syncManager.requestSync()
    }

    override fun updateLanguage(value: String) {
        datastore.updateLanguage(value = value)
        syncManager.requestSync()
    }

    override fun updateImageQuality(value: String) {
        datastore.updateImageQuality(value = value)
    }

    override fun updateShowNextReleaseMoviesDate(value: String) {
        datastore.updateShowNextReleaseMoviesDate(value = value)
    }

    override fun updateSecureBaseUrl(value: String) {
        datastore.updateSecureBaseUrl(value = value)
    }

    override fun updateFirstInstall(value: Boolean) {
        datastore.updateFirstInstall(value = value)
    }

    override fun updateIsCheatActive(value: Boolean) {
        datastore.updateIsCheatActive(value = value)
    }

    override fun getSecureBaseUrl(): Flowable<String> =
        datastore.getSecureBaseUrl()

    override fun updateFCMToken(token: String) {
        datastore.updateFCMToken(token)
    }

    override fun updateWorkScheduleTime(value: Long) {
        datastore.updateWorkScheduleTime(value = value)
    }

    override fun getIsAdult(): Flowable<Boolean> = datastore.getIsAdult()

    override fun getAutoPlayTrailer(): Flowable<Boolean> = datastore.getAutoPlayTrailer()

    override fun getDarkMode(): Flowable<DarkThemeConfig> = datastore.getDarkMode()

    override fun getMainDate(): Flowable<String> = datastore.getMainDate()

    override fun getRegion(): Flowable<String> = datastore.getRegion()

    override fun getLanguage(): Flowable<String> = datastore.getLanguage()

    override fun getImageQuality(): Flowable<String> = datastore.getImageQuality()

    override fun getShowNextReleaseMoviesDate(): Flowable<String> = datastore.getShowNextReleaseMoviesDate()

    override fun getFCMToken(): Flowable<String> = datastore.getFCMToken()

    override fun getFirstInstall(): Flowable<Boolean> = datastore.getFirstInstall()

    override fun getWorkScheduleTime(): Flowable<Long> = datastore.getWorkScheduleTime()

    override fun getIsCheatActive(): Flowable<Boolean> = datastore.getIsCheatActive()
}