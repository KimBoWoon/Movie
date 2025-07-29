package com.bowoon.testing.repository

import com.bowoon.data.repository.UserDataRepository
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

    override suspend fun updateUserData(userData: InternalData, isSync: Boolean) {
        _userData.tryEmit(userData)
    }

    override suspend fun updateFCMToken(token: String) {
        _fcmToken.tryEmit(token)
    }

    override suspend fun getFCMToken(): String = currentFcmToken
}