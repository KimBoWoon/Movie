package com.bowoon.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.bowoon.model.DarkThemeConfig
import com.bowoon.model.MainMenu
import com.bowoon.model.MovieDetail
import com.bowoon.model.UserData
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
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

        private val UPDATE_DATE = stringPreferencesKey("updateDate")
        private val MAIN_MENU = stringPreferencesKey("mainMenu")
        private val FAVORITE_MOVIES = stringPreferencesKey("favoriteMovies")
        private val IS_DART_MODE = stringPreferencesKey("isDarkMode")
        private val REGION = stringPreferencesKey("region")
        private val LANGUAGE = stringPreferencesKey("language")
        private val IMAGE_QUALITY = stringPreferencesKey("imageQuality")
    }

    val userData = datastore.data.map {
        UserData(
            isDarkMode = it[IS_DART_MODE]?.let { jsonString ->
                json.decodeFromString<DarkThemeConfig>(jsonString)
            } ?: DarkThemeConfig.FOLLOW_SYSTEM,
            updateDate = it[UPDATE_DATE] ?: "",
            mainMenu = it[MAIN_MENU]?.let { jsonString ->
                json.decodeFromString<MainMenu>(jsonString)
            } ?: MainMenu(emptyList(), emptyList(), emptyList()),
            favoriteMovies = it[FAVORITE_MOVIES]?.let { jsonString ->
                json.decodeFromString<List<MovieDetail>>(jsonString)
            } ?: emptyList(),
            region = it[REGION] ?: "KR",
            language = it[LANGUAGE] ?: "ko",
            imageQuality = it[IMAGE_QUALITY] ?: "original"
        )
    }

    suspend fun updateDarkTheme(config: DarkThemeConfig) {
        datastore.edit {
            it[IS_DART_MODE] = json.encodeToString(config)
        }
    }

    suspend fun updateMainOfDate(date: String) {
        datastore.edit {
            it[UPDATE_DATE] = date
        }
    }

    suspend fun updateFavoriteMovies(favoriteMovies: MovieDetail) {
        datastore.edit {
            it[FAVORITE_MOVIES].let { jsonString ->
                when (jsonString) {
                    null -> it[FAVORITE_MOVIES] = json.encodeToString(listOf(favoriteMovies))
                    else -> {
                        val favoriteList = json.decodeFromString<List<MovieDetail>>(jsonString)

                        when {
                            favoriteList.find { it.id == favoriteMovies.id } != null -> favoriteList.filter { it.id != favoriteMovies.id }
                            else -> favoriteList + listOf(favoriteMovies)
                        }.run {
                            it[FAVORITE_MOVIES] = json.encodeToString(this)
                        }
                    }
                }
            }
        }
    }

    suspend fun updateMainMenu(mainMenu: MainMenu) {
        datastore.edit {
            it[MAIN_MENU] = json.encodeToString(mainMenu)
        }
    }

    suspend fun updateRegion(region: String) {
        datastore.edit {
            it[REGION] = region
        }
    }

    suspend fun updateLanguage(language: String) {
        datastore.edit {
            it[LANGUAGE] = language
        }
    }

    suspend fun updateImageQuality(imageQuality: String) {
        datastore.edit {
            it[IMAGE_QUALITY] = imageQuality
        }
    }

    suspend fun getMainOfDate(): String =
        datastore.data.map { it[UPDATE_DATE] }.firstOrNull() ?: ""

    suspend fun getRegion(): String =
        datastore.data.map { it[REGION] }.firstOrNull() ?: "KR"

    suspend fun getLanguage(): String =
        datastore.data.map { it[LANGUAGE] }.firstOrNull() ?: "ko"

    suspend fun getFavoriteMovies(): List<MovieDetail> =
        datastore.data.map { it[FAVORITE_MOVIES] }.firstOrNull()?.let { jsonString ->
            json.decodeFromString<List<MovieDetail>>(jsonString)
        } ?: emptyList()

    suspend fun getImageQuality(): String =
        datastore.data.map { it[IMAGE_QUALITY] }.firstOrNull()?.let { jsonString ->
            json.decodeFromString<String>(jsonString)
        } ?: "original"
}