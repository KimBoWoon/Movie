package com.bowoon.data.repository

import com.bowoon.model.DarkThemeConfig
import com.bowoon.model.InternalData
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {
    val internalData: Flow<InternalData>
    suspend fun updateFCMToken(token: String)
    suspend fun getFCMToken(): String
    suspend fun updateIsAdult(value: Boolean)
    suspend fun updateAutoPlayTrailer(value: Boolean)
    suspend fun updateIsDarkMode(darkThemeConfig: DarkThemeConfig)
    suspend fun updateMainDate(value: String)
    suspend fun updateRegion(value: String)
    suspend fun updateLanguage(value: String)
    suspend fun updateImageQuality(value: String)
    suspend fun updateNoShowToday(value: String)
}