package com.bowoon.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.bowoon.model.DailyBoxOffice
import com.bowoon.model.DarkThemeConfig
import com.bowoon.model.UserData
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject


/**
 * DataStore Repository
 */
class InternalDataSource @Inject constructor(
    private val datastore: DataStore<Preferences>,
    private val json: Json
) {
    companion object {
        private const val TAG = "datastore"
    }

    val userData = datastore.data.mapNotNull {
        UserData(
            boxOfficeDate = it[stringPreferencesKey("boxOfficeDate")]?.let { jsonString ->
                json.decodeFromString(jsonString)
            } ?: "",
            dailyBoxOffices = it[stringPreferencesKey("dailyBoxOffices")]?.let { jsonString ->
                json.decodeFromString<List<DailyBoxOffice>>(jsonString)
            } ?: emptyList(),
            isDarkMode = it[stringPreferencesKey("isDarkMode")]?.let { jsonString ->
                json.decodeFromString<DarkThemeConfig>(jsonString)
            } ?: DarkThemeConfig.FOLLOW_SYSTEM
        )
    }

    suspend fun updateDarkTheme(config: DarkThemeConfig) {
        datastore.edit {
            it[stringPreferencesKey("isDarkMode")] = json.encodeToString(config)
        }
    }

    suspend fun updateBoxOfficeDate(date: String) {
        datastore.edit {
            it[stringPreferencesKey("boxOfficeDate")] = json.encodeToString(date)
        }
    }

    suspend fun updateDailyBoxOffices(dailyBoxOffices: List<DailyBoxOffice>) {
        datastore.edit {
            it[stringPreferencesKey("dailyBoxOffices")] = json.encodeToString(dailyBoxOffices)
        }
    }
}