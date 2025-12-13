package com.bowoon.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.bowoon.model.DarkThemeConfig
import com.bowoon.model.InternalData
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * DataStore Repository
 */
class InternalDataSource @Inject constructor(
    private val datastore: DataStore<Preferences>
) {
    companion object {
        private const val TAG = "datastore"

        private val FCM_TOKEN = stringPreferencesKey(name = "fcmToken")
        private val IS_ADULT = booleanPreferencesKey(name = "isAdult")
        private val AUTO_PLAY_TRAILER = booleanPreferencesKey(name = "autoPlayTrailer")
        private val IS_DARK_MODE = stringPreferencesKey(name = "isDarkMode")
        private val UPDATE_DATE = stringPreferencesKey(name = "updateDate")
        private val REGION = stringPreferencesKey(name = "region")
        private val LANGUAGE = stringPreferencesKey(name = "language")
        private val IMAGE_QUALITY = stringPreferencesKey(name = "imageQuality")
        private val NO_SHOW_TODAY = stringPreferencesKey(name = "noShowToday")

    }

    val userData = datastore.data.map { preferences ->
        InternalData(
            isAdult = preferences[IS_ADULT] ?: true,
            autoPlayTrailer = preferences[AUTO_PLAY_TRAILER] ?: false,
            isDarkMode = when (preferences[IS_DARK_MODE]) {
                "LIGHT" -> DarkThemeConfig.LIGHT
                "DARK" -> DarkThemeConfig.DARK
                else -> DarkThemeConfig.FOLLOW_SYSTEM
            },
            updateDate = preferences[UPDATE_DATE] ?: "",
            region = preferences[REGION] ?: "KR",
            language = preferences[LANGUAGE] ?: "ko",
            imageQuality = preferences[IMAGE_QUALITY] ?: "original",
            noShowToday = preferences[NO_SHOW_TODAY] ?: ""
        )
    }

    suspend fun updateIsAdult(value: Boolean) {
        datastore.updateData { preferences ->
            preferences.toMutablePreferences().also { preferences ->
                preferences[IS_ADULT] = value
            }
        }
    }

    suspend fun updateAutoPlayTrailer(value: Boolean) {
        datastore.updateData { preferences ->
            preferences.toMutablePreferences().also { preferences ->
                preferences[AUTO_PLAY_TRAILER] = value
            }
        }
    }

    suspend fun updateIsDarkMode(darkThemeConfig: DarkThemeConfig) {
        datastore.updateData { preferences ->
            preferences.toMutablePreferences().also { preferences ->
                preferences[IS_DARK_MODE] = when (darkThemeConfig) {
                    DarkThemeConfig.LIGHT -> "LIGHT"
                    DarkThemeConfig.DARK -> "DARK"
                    else -> "FOLLOW_SYSTEM"
                }
            }
        }
    }

    suspend fun updateMainDate(value: String) {
        datastore.updateData { preferences ->
            preferences.toMutablePreferences().also { preferences ->
                preferences[UPDATE_DATE] = value
            }
        }
    }

    suspend fun updateRegion(value: String) {
        datastore.updateData { preferences ->
            preferences.toMutablePreferences().also { preferences ->
                preferences[REGION] = value
            }
        }
    }

    suspend fun updateLanguage(value: String) {
        datastore.updateData { preferences ->
            preferences.toMutablePreferences().also { preferences ->
                preferences[LANGUAGE] = value
            }
        }
    }

    suspend fun updateImageQuality(value: String) {
        datastore.updateData { preferences ->
            preferences.toMutablePreferences().also { preferences ->
                preferences[IMAGE_QUALITY] = value
            }
        }
    }

    suspend fun updateNoShowToday(value: String) {
        datastore.updateData { preferences ->
            preferences.toMutablePreferences().also { preferences ->
                preferences[NO_SHOW_TODAY] = value
            }
        }
    }

    suspend fun getIsAdult(): Boolean =
        datastore.data.map { preferences ->
            preferences[IS_ADULT]
        }.firstOrNull() ?: true

    suspend fun getAutoPlayTrailer(): Boolean =
        datastore.data.map { preferences ->
            preferences[AUTO_PLAY_TRAILER]
        }.firstOrNull() ?: false

    suspend fun getIsDarkMode(): String =
        datastore.data.map { preferences ->
            preferences[IS_DARK_MODE]
        }.firstOrNull() ?: "FOLLOW_SYSTEM"

    suspend fun getMainDate(): String =
        datastore.data.map { preferences ->
            preferences[UPDATE_DATE]
        }.firstOrNull() ?: ""

    suspend fun getRegion(): String =
        datastore.data.map { preferences ->
            preferences[REGION]
        }.firstOrNull() ?: "KR"

    suspend fun getLanguage(): String =
        datastore.data.map { preferences ->
            preferences[LANGUAGE]
        }.firstOrNull() ?: "ko"

    suspend fun getImageQuality(): String =
        datastore.data.map { preferences ->
            preferences[IMAGE_QUALITY]
        }.firstOrNull() ?: "original"

    suspend fun getNoShowToday(): String =
        datastore.data.map { preferences ->
            preferences[NO_SHOW_TODAY]
        }.firstOrNull() ?: ""

    suspend fun updateFCMToken(token: String) {
        datastore.edit { preferences ->
            preferences[FCM_TOKEN] = token
        }
    }

    suspend fun getFCMToken(): String =
        datastore.data.map { it[FCM_TOKEN] }.firstOrNull() ?: ""
}