package com.bowoon.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.bowoon.model.InternalData
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
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

        private val USER_DATA = stringPreferencesKey("userData")
        private val FCM_TOKEN = stringPreferencesKey("fcmToken")
    }

    val userData = datastore.data.map {
        it[USER_DATA]?.let { jsonString ->
            json.decodeFromString<InternalData>(jsonString)
        } ?: InternalData()
    }

    suspend fun updateUserData(userData: InternalData) {
        datastore.edit {
            it[USER_DATA] = json.encodeToString(userData)
        }
    }

    suspend fun updateSecureBaseUrl(secureBaseUrl: String) {
        datastore.edit {
            val data = it[USER_DATA]?.let { jsonString ->
                json.decodeFromString<InternalData>(jsonString)
            }
            it[USER_DATA] = json.encodeToString(data?.copy(secureBaseUrl = secureBaseUrl))
        }
    }

    suspend fun updateFCMToken(token: String) {
        datastore.edit {
            it[FCM_TOKEN] = token
        }
    }

    suspend fun getUserData(): InternalData =
        datastore.data.map {
            it[USER_DATA]?.let { jsonString ->
                json.decodeFromString<InternalData>(jsonString)
            } ?: InternalData()
        }.firstOrNull() ?: InternalData()

    suspend fun getFCMToken(): String =
        datastore.data.map { it[FCM_TOKEN] }.firstOrNull() ?: ""
}