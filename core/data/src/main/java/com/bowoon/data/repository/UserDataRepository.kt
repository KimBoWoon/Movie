package com.bowoon.data.repository

import com.bowoon.model.InternalData
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {
    val internalData: Flow<InternalData>
    suspend fun updateUserData(userData: InternalData, isSync: Boolean)
    suspend fun updateFCMToken(token: String)
    suspend fun getFCMToken(): String
}