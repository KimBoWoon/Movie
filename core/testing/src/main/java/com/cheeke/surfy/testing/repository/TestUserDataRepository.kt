package com.cheeke.surfy.testing.repository

import com.cheeke.surfy.model.DarkThemeConfig
import com.cheeke.surfy.model.InternalData
import com.cheeke.surfy.userdata.api.UserDataRepository
import kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterNotNull

class TestUserDataRepository : UserDataRepository {
    private val _userData = MutableSharedFlow<InternalData>(replay = 1, onBufferOverflow = DROP_OLDEST)
    private val _fcmToken = MutableSharedFlow<String>(replay = 1, onBufferOverflow = DROP_OLDEST)
    private val currentUserData get() = _userData.replayCache.firstOrNull() ?: InternalData()
    private val currentFcmToken get() = _fcmToken.replayCache.firstOrNull() ?: ""
    private var workedTime = 0L
    override val internalData: Flow<InternalData> = _userData.filterNotNull()

    init {
        _userData.tryEmit(value = InternalData())
    }

    override suspend fun updateFCMToken(token: String) {
        _fcmToken.tryEmit(token)
    }

    override suspend fun getFCMToken(): String = currentFcmToken

    override suspend fun updateIsAdult(value: Boolean) {
        _userData.tryEmit(value = currentUserData.copy(isAdult = value))
    }

    override suspend fun updateIsAutoPlayTrailer(value: Boolean) {
        _userData.tryEmit(value = currentUserData.copy(isAutoPlayTrailer = value))
    }

    override suspend fun updateDarkMode(darkThemeConfig: DarkThemeConfig) {
        _userData.tryEmit(value = currentUserData.copy(isDarkMode = darkThemeConfig))
    }

    override suspend fun updateMainDate(value: String) {
        _userData.tryEmit(value = currentUserData.copy(updateDate = value))
    }

    override suspend fun updateRegion(value: String) {
        _userData.tryEmit(value = currentUserData.copy(region = value))
    }

    override suspend fun updateLanguage(value: String) {
        _userData.tryEmit(value = currentUserData.copy(language = value))
    }

    override suspend fun updateImageQuality(value: String) {
        _userData.tryEmit(value = currentUserData.copy(imageQuality = value))
    }

    override suspend fun updateShowNextReleaseMoviesDate(value: String) {
        _userData.tryEmit(value = currentUserData.copy(showNextReleaseMoviesDate = value))
    }

    override suspend fun updateSecureBaseUrl(value: String) {
        _userData.tryEmit(value = currentUserData.copy(secureBaseUrl = value))
    }

    override suspend fun updateFirstInstall(value: Boolean) {
        _userData.tryEmit(value = currentUserData.copy(isFirstInstall = value))
    }

    override suspend fun updateWorkScheduleTime(value: Long) {
        workedTime = value
    }

    override suspend fun updateIsCheatActive(value: Boolean) {
        _userData.tryEmit(value = currentUserData.copy(isCheatActive = value))
    }

    override suspend fun getIsAdult(): Boolean =
        currentUserData.isAdult

    override suspend fun getAutoPlayTrailer(): Boolean =
        currentUserData.isAutoPlayTrailer

    override suspend fun getDarkMode(): DarkThemeConfig = currentUserData.isDarkMode

    override suspend fun getMainDate(): String =
        currentUserData.updateDate

    override suspend fun getRegion(): String =
        currentUserData.region

    override suspend fun getLanguage(): String =
        currentUserData.language

    override suspend fun getImageQuality(): String =
        currentUserData.imageQuality

    override suspend fun getShowNextReleaseMoviesDate(): String =
        currentUserData.showNextReleaseMoviesDate

    override suspend fun getSecureBaseUrl(): String =
        currentUserData.secureBaseUrl

    override suspend fun getFirstInstall(): Boolean =
        currentUserData.isFirstInstall

    override suspend fun getWorkScheduleTime(): Long = workedTime

    override suspend fun getIsCheatActive(): Boolean = currentUserData.isCheatActive
}