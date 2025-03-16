package com.bowoon.datastore

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.bowoon.model.DarkThemeConfig
import com.bowoon.model.InternalData
import com.bowoon.model.MainMenu
import com.bowoon.testing.utils.MainDispatcherRule
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals

private val internalDataSource = InternalDataSource(
    datastore = PreferenceDataStoreFactory.create(
        scope = TestScope(UnconfinedTestDispatcher()),
        produceFile = { File("movie-test-store.preferences_pb") }
    ),
    json = Json { ignoreUnknownKeys = true }
)

class InternalDataSourceTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setup() {
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