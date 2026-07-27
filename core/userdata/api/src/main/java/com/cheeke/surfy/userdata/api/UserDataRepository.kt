package com.cheeke.surfy.userdata.api

import com.cheeke.surfy.model.DarkThemeConfig
import com.cheeke.surfy.model.InternalData
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {
    val internalData: Flow<InternalData>
    suspend fun updateFCMToken(token: String)
    suspend fun getFCMToken(): String
    suspend fun updateIsAdult(value: Boolean)
    suspend fun updateIsAutoPlayTrailer(value: Boolean)
    suspend fun updateDarkMode(darkThemeConfig: DarkThemeConfig)
    suspend fun updateMainDate(value: String)
    suspend fun updateRegion(value: String)
    suspend fun updateLanguage(value: String)
    suspend fun updateImageQuality(value: String)
    suspend fun updateShowNextReleaseMoviesDate(value: String)
    suspend fun updateSecureBaseUrl(value: String)
    suspend fun updateFirstInstall(value: Boolean)
    suspend fun updateWorkScheduleTime(value: Long)
    suspend fun getIsAdult(): Boolean
    suspend fun getAutoPlayTrailer(): Boolean
    suspend fun getDarkMode(): DarkThemeConfig
    suspend fun getMainDate(): String
    suspend fun getRegion(): String
    suspend fun getLanguage(): String
    suspend fun getImageQuality(): String
    suspend fun getFirstInstall(): Boolean
    suspend fun getShowNextReleaseMoviesDate(): String
    suspend fun getSecureBaseUrl(): String
    suspend fun getWorkScheduleTime(): Long
    suspend fun updateIsCheatActive(value: Boolean)
    suspend fun getIsCheatActive(): Boolean
}