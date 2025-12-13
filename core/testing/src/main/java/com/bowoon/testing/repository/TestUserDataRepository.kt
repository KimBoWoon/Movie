package com.bowoon.testing.repository

import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.DarkThemeConfig
import com.bowoon.model.InternalData
import kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterNotNull

class TestUserDataRepository : UserDataRepository {
    private val _userData = MutableSharedFlow<InternalData>(replay = 1, onBufferOverflow = DROP_OLDEST)
    private val _fcmToken = MutableSharedFlow<String>(replay = 1, onBufferOverflow = DROP_OLDEST)
    private val currentUserData get() = _userData.replayCache.firstOrNull() ?: InternalData()
    private val currentFcmToken get() = _fcmToken.replayCache.firstOrNull() ?: ""
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

    override suspend fun updateAutoPlayTrailer(value: Boolean) {
        _userData.tryEmit(value = currentUserData.copy(autoPlayTrailer = value))
    }

    override suspend fun updateIsDarkMode(darkThemeConfig: DarkThemeConfig) {
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

    override suspend fun updateNoShowToday(value: String) {
        _userData.tryEmit(value = currentUserData.copy(noShowToday = value))
    }
}