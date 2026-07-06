package com.cheeke.surfy.datastore

import androidx.datastore.rxjava3.RxDataStore
import com.cheeke.surfy.core.datastore.DarkThemeConfigProto
import com.cheeke.surfy.core.datastore.InternalDataPreferences
import com.cheeke.surfy.model.DarkThemeConfig
import com.cheeke.surfy.model.InternalData
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

/**
 * DataStore Repository
 */
@OptIn(ExperimentalCoroutinesApi::class)
class InternalDataSource @Inject constructor(
    private val datastore: RxDataStore<InternalDataPreferences>
) {
    companion object {
        private const val TAG = "datastore"
    }

    val userData = datastore.data()
        .map { preferences ->
            InternalData(
                isAdult = preferences.isAdult,
                isAutoPlayTrailer = preferences.isAutoPlayTrailer,
                isDarkMode = when (preferences.darkMode) {
                    DarkThemeConfigProto.DARK_THEME_CONFIG_UNSPECIFIED,
                    DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM,
                    DarkThemeConfigProto.UNRECOGNIZED -> DarkThemeConfig.FOLLOW_SYSTEM
                    DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT -> DarkThemeConfig.LIGHT
                    DarkThemeConfigProto.DARK_THEME_CONFIG_DARK -> DarkThemeConfig.DARK
                },
                updateDate = preferences.updateDate,
                region = preferences.region,
                language = preferences.language,
                imageQuality = preferences.imageQuality,
                showNextReleaseMoviesDate = preferences.showNextReleaseMoviesDate,
                secureBaseUrl = preferences.secureBaseUrl,
                isCheatActive = preferences.isCheatActive,
            )
        }

    fun updateIsAdult(value: Boolean) {
        datastore.updateDataAsync { preferences ->
            val updated = preferences.toBuilder()
                .setIsAdult(value)
                .build()

            Single.just(updated)
        }
    }

    fun updateIsAutoPlayTrailer(value: Boolean) {
        datastore.updateDataAsync { preferences ->
            val updated = preferences.toBuilder()
                .setIsAutoPlayTrailer(value)
                .build()

            Single.just(updated)
        }
    }

    fun updateDarkMode(darkThemeConfig: DarkThemeConfig) {
        datastore.updateDataAsync { preferences ->
            val updated = preferences.toBuilder()
                .setDarkMode(
                    when (darkThemeConfig) {
                        DarkThemeConfig.FOLLOW_SYSTEM -> DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM
                        DarkThemeConfig.LIGHT -> DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT
                        DarkThemeConfig.DARK -> DarkThemeConfigProto.DARK_THEME_CONFIG_DARK
                    }
                )
                .build()

            Single.just(updated)
        }
    }

    fun updateMainDate(value: String) {
        datastore.updateDataAsync { preferences ->
            val updated = preferences.toBuilder()
                .setUpdateDate(value)
                .build()

            Single.just(updated)
        }
    }

    fun updateRegion(value: String) {
        datastore.updateDataAsync { preferences ->
            val updated = preferences.toBuilder()
                .setRegion(value)
                .build()

            Single.just(updated)
        }
    }

    fun updateLanguage(value: String) {
        datastore.updateDataAsync { preferences ->
            val updated = preferences.toBuilder()
                .setLanguage(value)
                .build()

            Single.just(updated)
        }
    }

    fun updateImageQuality(value: String) {
        datastore.updateDataAsync { preferences ->
            val updated = preferences.toBuilder()
                .setImageQuality(value)
                .build()

            Single.just(updated)
        }
    }

    fun updateShowNextReleaseMoviesDate(value: String) {
        datastore.updateDataAsync { preferences ->
            val updated = preferences.toBuilder()
                .setShowNextReleaseMoviesDate(value)
                .build()

            Single.just(updated)
        }
    }

    fun updateSecureBaseUrl(value: String) {
        datastore.updateDataAsync { preferences ->
            val updated = preferences.toBuilder()
                .setSecureBaseUrl(value)
                .build()

            Single.just(updated)
        }
    }

    fun updateFirstInstall(value: Boolean) {
        datastore.updateDataAsync { preferences ->
            val updated = preferences.toBuilder()
                .setIsFirstInstall(value)
                .build()

            Single.just(updated)
        }
    }

    fun updateWorkScheduleTime(value: Long) {
        datastore.updateDataAsync { preferences ->
            val updated = preferences.toBuilder()
                .setWorkScheduleTime(value)
                .build()

            Single.just(updated)
        }
    }

    fun updateIsCheatActive(value: Boolean) {
        datastore.updateDataAsync { preferences ->
            val updated = preferences.toBuilder()
                .setIsCheatActive(value)
                .build()

            Single.just(updated)
        }
    }

    fun getIsAdult(): Flowable<Boolean> =
        datastore.data().map { preferences ->
            preferences.isAdult
        }

    fun getAutoPlayTrailer(): Flowable<Boolean> =
        datastore.data().map { preferences ->
            preferences.isAutoPlayTrailer
        }

    fun getDarkMode(): Flowable<DarkThemeConfig> =
        datastore.data().map { preferences ->
            when (preferences.darkMode) {
                DarkThemeConfigProto.UNRECOGNIZED,
                DarkThemeConfigProto.DARK_THEME_CONFIG_UNSPECIFIED,
                DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM -> DarkThemeConfig.FOLLOW_SYSTEM
                DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT -> DarkThemeConfig.LIGHT
                DarkThemeConfigProto.DARK_THEME_CONFIG_DARK -> DarkThemeConfig.DARK
            }
        }

    fun getMainDate(): Flowable<String> =
        datastore.data().map { preferences ->
            preferences.updateDate
        }

    fun getRegion(): Flowable<String> =
        datastore.data().map { preferences ->
            preferences.region
        }

    fun getLanguage(): Flowable<String> =
        datastore.data().map { preferences ->
            preferences.language
        }

    fun getImageQuality(): Flowable<String> =
        datastore.data().map { preferences ->
            preferences.imageQuality
        }

    fun getShowNextReleaseMoviesDate(): Flowable<String> =
        datastore.data().map { preferences ->
            preferences.showNextReleaseMoviesDate
        }

    fun getSecureBaseUrl(): Flowable<String> =
        datastore.data().map { preferences ->
            preferences.secureBaseUrl
        }

    fun getFirstInstall(): Flowable<Boolean> =
        datastore.data().map { preferences ->
            preferences.isFirstInstall
        }

    fun getWorkScheduleTime(): Flowable<Long> =
        datastore.data().map { preferences ->
            preferences.workScheduleTime
        }

    fun getIsCheatActive(): Flowable<Boolean> =
        datastore.data().map { preferences ->
            preferences.isCheatActive
        }

    fun updateFCMToken(token: String) {
        datastore.updateDataAsync { preferences ->
            val updated = preferences.toBuilder()
                .setFcmToken(token)
                .build()
            Single.just(updated)
        }
    }

    fun getFCMToken(): Flowable<String> =
        datastore.data().map { preferences ->
            preferences.fcmToken
        }
}