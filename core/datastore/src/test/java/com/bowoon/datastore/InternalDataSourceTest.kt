package com.bowoon.datastore

import androidx.datastore.preferences.core.preferencesOf
import com.bowoon.datastore_test.InMemoryDataStore
import com.bowoon.model.DarkThemeConfig
import com.bowoon.model.InternalData
import com.bowoon.model.MainMenu
import com.bowoon.testing.utils.MainDispatcherRule
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class InternalDataSourceTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var internalDataSource: InternalDataSource

    @Before
    fun setup() {
        internalDataSource = InternalDataSource(
            datastore = InMemoryDataStore(preferencesOf()),
            json = Json { ignoreUnknownKeys = true }
        )
        runBlocking {
            internalDataSource.updateUserData(InternalData())
            internalDataSource.updateFCMToken("")
        }
    }

    @Test
    fun updateUserDataTest() = runTest {
        val userData = InternalData(
            isAdult = true,
            autoPlayTrailer = false,
            isDarkMode = DarkThemeConfig.DARK,
            updateDate = "2025-03-12",
            mainMenu = MainMenu(),
            region = "KR",
            language = "ko",
            imageQuality = "original"
        )
        assertEquals(internalDataSource.getUserData(), InternalData())
        internalDataSource.updateUserData(userData)
        assertEquals(internalDataSource.getUserData(), userData)
    }

    @Test
    fun updateFcmTokenTest() = runTest {
        val fcmToken = "asdnui490u09jb"

        assertEquals(internalDataSource.getFCMToken(), "")
        internalDataSource.updateFCMToken(fcmToken)
        assertEquals(internalDataSource.getFCMToken(), fcmToken)
    }
}