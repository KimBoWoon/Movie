package com.bowoon.data.repository

import com.bowoon.model.UserData
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {
    val userData: Flow<UserData>
    suspend fun updateUserData(userData: UserData, isSync: Boolean)
    suspend fun updateFCMToken(token: String)
    suspend fun isAdult(): Boolean
    suspend fun isAutoPlayTrailer(): Boolean
    suspend fun getMainOfDate(): String
    suspend fun getRegion(): String
    suspend fun getLanguage(): String
    suspend fun getImageQuality(): String
    suspend fun getFCMToken(): String
}