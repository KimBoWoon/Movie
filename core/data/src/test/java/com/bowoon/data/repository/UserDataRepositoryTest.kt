package com.bowoon.data.repository

import com.bowoon.model.DarkThemeConfig
import com.bowoon.testing.repository.TestUserDataRepository
import com.bowoon.testing.utils.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class UserDataRepositoryTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var testUserDataRepository: TestUserDataRepository

    @Before
    fun setup() {
        testUserDataRepository = TestUserDataRepository()
    }

    @Test
    fun updateFcmTokenTest() = runTest {
        val fcmToken = "asdfjaioejfi390i90g"

        testUserDataRepository.updateFCMToken(fcmToken)

        assertEquals(
            testUserDataRepository.getFCMToken(),
            fcmToken
        )
    }

    @Test
    fun updateIsAdultTest() = runTest {
        assertEquals(
            expected = testUserDataRepository.internalData.first().isAdult,
            actual = true
        )
        testUserDataRepository.updateIsAdult(value = false)
        assertEquals(
            expected = testUserDataRepository.internalData.first().isAdult,
            actual = false
        )
    }

    @Test
    fun updateAutoPlayTrailerTest() = runTest {
        assertEquals(
            expected = testUserDataRepository.internalData.first().autoPlayTrailer,
            actual = true
        )
        testUserDataRepository.updateAutoPlayTrailer(value = false)
        assertEquals(
            expected = testUserDataRepository.internalData.first().autoPlayTrailer,
            actual = false
        )
    }

    @Test
    fun updateDarkModeSettingTest() = runTest {
        assertEquals(
            expected = testUserDataRepository.internalData.first().isDarkMode,
            actual = DarkThemeConfig.FOLLOW_SYSTEM
        )
        testUserDataRepository.updateIsDarkMode(darkThemeConfig = DarkThemeConfig.LIGHT)
        assertEquals(
            expected = testUserDataRepository.internalData.first().isDarkMode,
            actual = DarkThemeConfig.LIGHT
        )
    }

    @Test
    fun updateMainDateTest() = runTest {
        assertEquals(
            expected = testUserDataRepository.internalData.first().updateDate,
            actual = ""
        )
        testUserDataRepository.updateMainDate(value = "2025-12-13")
        assertEquals(
            expected = testUserDataRepository.internalData.first().updateDate,
            actual = "2025-12-13"
        )
    }

    @Test
    fun updateRegionTest() = runTest {
        assertEquals(
            expected = testUserDataRepository.internalData.first().region,
            actual = "KR"
        )
        testUserDataRepository.updateRegion(value = "US")
        assertEquals(
            expected = testUserDataRepository.internalData.first().region,
            actual = "US"
        )
    }

    @Test
    fun updateLanguageTest() = runTest {
        assertEquals(
            expected = testUserDataRepository.internalData.first().language,
            actual = "ko"
        )
        testUserDataRepository.updateLanguage(value = "ja")
        assertEquals(
            expected = testUserDataRepository.internalData.first().language,
            actual = "ja"
        )
    }

    @Test
    fun updateImageQualityTest() = runTest {
        assertEquals(
            expected = testUserDataRepository.internalData.first().imageQuality,
            actual = "original"
        )
        testUserDataRepository.updateImageQuality(value = "w342")
        assertEquals(
            expected = testUserDataRepository.internalData.first().imageQuality,
            actual = "w342"
        )
    }

    @Test
    fun updateNoShowTodayTest() = runTest {
        assertEquals(
            expected = testUserDataRepository.internalData.first().noShowToday,
            actual = ""
        )
        testUserDataRepository.updateNoShowToday(value = "2025-12-13")
        assertEquals(
            expected = testUserDataRepository.internalData.first().noShowToday,
            actual = "2025-12-13"
        )
    }
}