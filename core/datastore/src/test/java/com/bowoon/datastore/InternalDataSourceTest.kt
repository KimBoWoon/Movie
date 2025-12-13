package com.bowoon.datastore

import androidx.datastore.preferences.core.preferencesOf
import com.bowoon.datastore_test.InMemoryDataStore
import com.bowoon.model.DarkThemeConfig
import com.bowoon.testing.utils.MainDispatcherRule
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
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
            datastore = InMemoryDataStore(initialValue = preferencesOf())
        )
        runBlocking {
            internalDataSource.updateFCMToken(token = "")
        }
    }

    @Test
    fun updateFcmTokenTest() = runTest {
        val fcmToken = "asdnui490u09jb"

        assertEquals(internalDataSource.getFCMToken(), "")
        internalDataSource.updateFCMToken(fcmToken)
        assertEquals(internalDataSource.getFCMToken(), fcmToken)
    }

    @Test
    fun updateIsAdultTest() = runTest {
        assertEquals(
            expected = internalDataSource.getIsAdult(),
            actual = true
        )
        internalDataSource.updateIsAdult(value = false)
        assertEquals(
            expected = internalDataSource.getIsAdult(),
            actual = false
        )
    }

    @Test
    fun updateAutoPlayTrailerTest() = runTest {
        assertEquals(
            expected = internalDataSource.getAutoPlayTrailer(),
            actual = false
        )
        internalDataSource.updateAutoPlayTrailer(value = true)
        assertEquals(
            expected = internalDataSource.getAutoPlayTrailer(),
            actual = true
        )
    }

    @Test
    fun updateDarkModeSettingTest() = runTest {
        assertEquals(
            expected = internalDataSource.getIsDarkMode(),
            actual = "FOLLOW_SYSTEM"
        )
        internalDataSource.updateIsDarkMode(darkThemeConfig = DarkThemeConfig.DARK)
        assertEquals(
            expected = internalDataSource.getIsDarkMode(),
            actual = "DARK"
        )
    }

    @Test
    fun updateMainDateTest() = runTest {
        assertEquals(
            expected = internalDataSource.getMainDate(),
            actual = ""
        )
        internalDataSource.updateMainDate(value = "2025-12-13")
        assertEquals(
            expected = internalDataSource.getMainDate(),
            actual = "2025-12-13"
        )
    }

    @Test
    fun updateRegionTest() = runTest {
        assertEquals(
            expected = internalDataSource.getRegion(),
            actual = "KR"
        )
        internalDataSource.updateRegion(value = "US")
        assertEquals(
            expected = internalDataSource.getRegion(),
            actual = "US"
        )
    }

    @Test
    fun updateLanguageTest() = runTest {
        assertEquals(
            expected = internalDataSource.getLanguage(),
            actual = "ko"
        )
        internalDataSource.updateLanguage(value = "ja")
        assertEquals(
            expected = internalDataSource.getLanguage(),
            actual = "ja"
        )
    }

    @Test
    fun updateImageQualityTest() = runTest {
        assertEquals(
            expected = internalDataSource.getImageQuality(),
            actual = "original"
        )
        internalDataSource.updateImageQuality(value = "w342")
        assertEquals(
            expected = internalDataSource.getImageQuality(),
            actual = "w342"
        )
    }

    @Test
    fun updateNoShowTodayTest() = runTest {
        assertEquals(
            expected = internalDataSource.getNoShowToday(),
            actual = ""
        )
        internalDataSource.updateNoShowToday(value = "2025-12-13")
        assertEquals(
            expected = internalDataSource.getNoShowToday(),
            actual = "2025-12-13"
        )
    }
}