package com.bowoon.data.repository

import com.bowoon.model.DarkThemeConfig
import com.bowoon.model.InternalData
import com.bowoon.model.MainMenu
import com.bowoon.testing.utils.MainDispatcherRule
import com.bowoon.testing.repository.TestUserDataRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class UserDataRepositoryTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private val testUserDataRepository = TestUserDataRepository()

    @Test
    fun updateUserDataTest() = runTest {
        val internalData = InternalData(
            isAdult = true,
            autoPlayTrailer = false,
            isDarkMode = DarkThemeConfig.DARK,
            updateDate = "2025-03-13",
            mainMenu = MainMenu(),
            region = "KR",
            language = "en",
            imageQuality = "w92"
        )

        testUserDataRepository.updateUserData(
            userData = internalData,
            isSync = true
        )

        assertEquals(
            testUserDataRepository.internalData.first(),
            internalData
        )
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
}