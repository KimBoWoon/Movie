package com.bowoon.datastore

import androidx.datastore.core.DataStore
import com.bowoon.model.DarkThemeConfig
import com.bowoon.model.InternalData
import com.bowoon.surfy.core.datastore.DarkThemeConfigProto
import com.bowoon.surfy.core.datastore.InternalDataPreferences
import com.bowoon.surfy.core.datastore.copy
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * DataStore Repository
 */
class InternalDataSource @Inject constructor(
    private val datastore: DataStore<InternalDataPreferences>
) {
    companion object {
        private const val TAG = "datastore"
    }

    val userData = datastore.data.map { preferences ->
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

    suspend fun updateIsAdult(value: Boolean) {
        datastore.updateData { preferences ->
            preferences.copy {
                isAdult = value
            }
        }
    }

    suspend fun updateIsAutoPlayTrailer(value: Boolean) {
        datastore.updateData { preferences ->
            preferences.copy {
                isAutoPlayTrailer = value
            }
        }
    }

    suspend fun updateDarkMode(darkThemeConfig: DarkThemeConfig) {
        datastore.updateData { preferences ->
            preferences.copy {
                darkMode = when (darkThemeConfig) {
                    DarkThemeConfig.FOLLOW_SYSTEM -> DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM
                    DarkThemeConfig.LIGHT -> DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT
                    DarkThemeConfig.DARK -> DarkThemeConfigProto.DARK_THEME_CONFIG_DARK
                }
            }
        }
    }

    suspend fun updateMainDate(value: String) {
        datastore.updateData { preferences ->
            preferences.copy {
                updateDate = value
            }
        }
    }

    suspend fun updateRegion(value: String) {
        datastore.updateData { preferences ->
            preferences.copy {
                region = value
            }
        }
    }

    suspend fun updateLanguage(value: String) {
        datastore.updateData { preferences ->
            preferences.copy {
                language = value
            }
        }
    }

    suspend fun updateImageQuality(value: String) {
        datastore.updateData { preferences ->
            preferences.copy {
                imageQuality = value
            }
        }
    }

    suspend fun updateShowNextReleaseMoviesDate(value: String) {
        datastore.updateData { preferences ->
            preferences.copy {
                showNextReleaseMoviesDate = value
            }
        }
    }

    suspend fun updateSecureBaseUrl(value: String) {
        datastore.updateData { preferences ->
            preferences.copy {
                secureBaseUrl = value
            }
        }
    }

    suspend fun updateFirstInstall(value: Boolean) {
        datastore.updateData { preferences ->
            preferences.copy {
                isFirstInstall = value
            }
        }
    }

    suspend fun updateWorkScheduleTime(value: Long) {
        datastore.updateData { preferences ->
            preferences.copy {
                workScheduleTime = value
            }
        }
    }

    suspend fun updateIsCheatActive(value: Boolean) {
        datastore.updateData { preferences ->
            preferences.copy {
                isCheatActive = value
            }
        }
    }

    suspend fun getIsAdult(): Boolean =
        datastore.data.map { preferences ->
            preferences.isAdult
        }.firstOrNull() ?: true

    suspend fun getAutoPlayTrailer(): Boolean =
        datastore.data.map { preferences ->
            preferences.isAutoPlayTrailer
        }.firstOrNull() ?: false

    suspend fun getDarkMode(): DarkThemeConfig =
        datastore.data.map { preferences ->
            when (preferences.darkMode) {
                DarkThemeConfigProto.UNRECOGNIZED,
                DarkThemeConfigProto.DARK_THEME_CONFIG_UNSPECIFIED,
                DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM -> DarkThemeConfig.FOLLOW_SYSTEM
                DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT -> DarkThemeConfig.LIGHT
                DarkThemeConfigProto.DARK_THEME_CONFIG_DARK -> DarkThemeConfig.DARK
            }
        }.firstOrNull() ?: DarkThemeConfig.FOLLOW_SYSTEM

    suspend fun getMainDate(): String =
        datastore.data.map { preferences ->
            preferences.updateDate
        }.firstOrNull() ?: ""

    suspend fun getRegion(): String =
        datastore.data.map { preferences ->
            preferences.region
        }.firstOrNull() ?: "KR"

    suspend fun getLanguage(): String =
        datastore.data.map { preferences ->
            preferences.language
        }.firstOrNull() ?: "ko"

    suspend fun getImageQuality(): String =
        datastore.data.map { preferences ->
            preferences.imageQuality
        }.firstOrNull() ?: "original"

    suspend fun getShowNextReleaseMoviesDate(): String =
        datastore.data.map { preferences ->
            preferences.showNextReleaseMoviesDate
        }.firstOrNull() ?: ""

    suspend fun getSecureBaseUrl(): String =
        datastore.data.map { preferences ->
            preferences.secureBaseUrl
        }.firstOrNull() ?: ""

    suspend fun getFirstInstall(): Boolean =
        datastore.data.map { preferences ->
            preferences.isFirstInstall
        }.firstOrNull() ?: false

    suspend fun getWorkScheduleTime(): Long =
        datastore.data.map { preferences ->
            preferences.workScheduleTime
        }.firstOrNull() ?: 0

    suspend fun getIsCheatActive(): Boolean =
        datastore.data.map { preferences ->
            preferences.isCheatActive
        }.firstOrNull() ?: false

    suspend fun updateFCMToken(token: String) {
        datastore.updateData { preferences ->
            preferences.copy {
                fcmToken = token
            }
        }
    }

    suspend fun getFCMToken(): String =
        datastore.data.map { preferences ->
            preferences.fcmToken
        }.firstOrNull() ?: ""
}