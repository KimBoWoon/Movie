package com.cheeke.surfy.data.repository

import com.cheeke.surfy.model.DarkThemeConfig
import com.cheeke.surfy.model.InternalData
import io.reactivex.rxjava3.core.Flowable

interface UserDataRepository {
    val internalData: Flowable<InternalData>
    fun updateFCMToken(token: String)
    fun getFCMToken(): Flowable<String>
    fun updateIsAdult(value: Boolean)
    fun updateIsAutoPlayTrailer(value: Boolean)
    fun updateDarkMode(darkThemeConfig: DarkThemeConfig)
    fun updateMainDate(value: String)
    fun updateRegion(value: String)
    fun updateLanguage(value: String)
    fun updateImageQuality(value: String)
    fun updateShowNextReleaseMoviesDate(value: String)
    fun updateSecureBaseUrl(value: String)
    fun updateFirstInstall(value: Boolean)
    fun updateWorkScheduleTime(value: Long)
    fun getIsAdult(): Flowable<Boolean>
    fun getAutoPlayTrailer(): Flowable<Boolean>
    fun getDarkMode(): Flowable<DarkThemeConfig>
    fun getMainDate(): Flowable<String>
    fun getRegion(): Flowable<String>
    fun getLanguage(): Flowable<String>
    fun getImageQuality(): Flowable<String>
    fun getFirstInstall(): Flowable<Boolean>
    fun getShowNextReleaseMoviesDate(): Flowable<String>
    fun getSecureBaseUrl(): Flowable<String>
    fun getWorkScheduleTime(): Flowable<Long>
    fun updateIsCheatActive(value: Boolean)
    fun getIsCheatActive(): Flowable<Boolean>
}