package com.cheeke.surfy.userdata.impl

import com.cheeke.surfy.core.userdata.InternalDataPreferences
import com.cheeke.surfy.model.DarkThemeConfig
import com.cheeke.surfy.testing.utils.MainDispatcherRule
import com.cheeke.surfy.userdata.impl.test.InMemoryDataStore
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
            datastore = InMemoryDataStore(initialValue = InternalDataPreferences.getDefaultInstance())
        )
        runBlocking {
            internalDataSource.updateFCMToken(token = "")
            internalDataSource.updateIsAdult(value = true)
            internalDataSource.updateRegion(value = "KR")
            internalDataSource.updateLanguage(value = "ko")
            internalDataSource.updateImageQuality(value = "original")
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
        internalDataSource.updateIsAutoPlayTrailer(value = true)
        assertEquals(
            expected = internalDataSource.getAutoPlayTrailer(),
            actual = true
        )
    }

    @Test
    fun updateDarkModeSettingTest() = runTest {
        assertEquals(
            expected = internalDataSource.getDarkMode(),
            actual = DarkThemeConfig.FOLLOW_SYSTEM
        )
        internalDataSource.updateDarkMode(darkThemeConfig = DarkThemeConfig.DARK)
        assertEquals(
            expected = internalDataSource.getDarkMode(),
            actual = DarkThemeConfig.DARK
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
    fun updateShowNextReleaseMoviesDateTest() = runTest {
        assertEquals(
            expected = internalDataSource.getShowNextReleaseMoviesDate(),
            actual = ""
        )
        internalDataSource.updateShowNextReleaseMoviesDate(value = "2025-12-13")
        assertEquals(
            expected = internalDataSource.getShowNextReleaseMoviesDate(),
            actual = "2025-12-13"
        )
    }

    @Test
    fun updateNoSecureBaseUrlTest() = runTest {
        assertEquals(
            expected = internalDataSource.getSecureBaseUrl(),
            actual = ""
        )
        internalDataSource.updateSecureBaseUrl(value = "secureBaseUrl")
        assertEquals(
            expected = internalDataSource.getSecureBaseUrl(),
            actual = "secureBaseUrl"
        )
    }
}