package com.bowoon.my

import androidx.activity.ComponentActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.bowoon.data.repository.LocalMovieAppDataComposition
import com.bowoon.model.DarkThemeConfig
import com.bowoon.model.InternalData
import com.bowoon.model.LanguageItem
import com.bowoon.model.MovieAppData
import com.bowoon.model.Region
import com.bowoon.testing.model.mainMenuTestData
import org.junit.Rule
import org.junit.Test

class MyScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()
    private val movieAppData = MovieAppData(
        isAdult = false,
        autoPlayTrailer = false,
        isDarkMode = DarkThemeConfig.LIGHT,
        updateDate = "2025-03-12",
        mainMenu = mainMenuTestData,
        region = listOf(
            Region(
                englishName = "United States of America",
                iso31661 = "US",
                nativeName = "US",
                isSelected = true
            ),
            Region(
                englishName = "Korean",
                iso31661 = "KR",
                nativeName = "KR",
                isSelected = false
            )
        ),
        language = listOf(
            LanguageItem(
                englishName = "English",
                iso6391 = "en",
                name = "en",
                isSelected = true
            ),
            LanguageItem(
                englishName = "Korean",
                iso6391 = "ko",
                name = "ko",
                isSelected = false
            )
        ),
        imageQuality = "w92"
    )
    private val userdata = InternalData(
        isAdult = false,
        autoPlayTrailer = false,
        isDarkMode = DarkThemeConfig.LIGHT,
        updateDate = "2025-03-12",
        mainMenu = mainMenuTestData,
        region = "US",
        language = "en",
        imageQuality = "w92"
    )

    @Test
    fun myScreenTest() {
        composeTestRule.apply {
            setContent {
                CompositionLocalProvider(
                    LocalMovieAppDataComposition provides movieAppData
                ) {
                    MyScreen(
                        internalData = userdata,
                        updateUserData = { _, _ -> }
                    )
                }
            }

            onNodeWithText(text = "메인 업데이트 날짜").assertExists().assertIsDisplayed()
            onNodeWithText(text = "2025-03-12").assertExists().assertIsDisplayed()
            onNodeWithText(text = "다크모드 설정").assertExists().assertIsDisplayed()
            onNodeWithText(text = "라이트").assertExists().assertIsDisplayed()
            onNodeWithText(text = "성인").assertExists().assertIsDisplayed()
            onNodeWithText(text = "미성년자").assertExists().assertIsDisplayed()
            onNodeWithText(text = "예고편 자동 재생").assertExists().assertIsDisplayed()
            onNodeWithText(text = "정지").assertExists().assertIsDisplayed()
            onNodeWithText(text = "언어").assertExists().assertIsDisplayed()
            onNodeWithText(text = "en (English)").assertExists().assertIsDisplayed()
            onNodeWithText(text = "지역").assertExists().assertIsDisplayed()
            onNodeWithText(text = "US (United States of America)").assertExists().assertIsDisplayed()
            onNodeWithText(text = "이미지 퀄리티").assertExists().assertIsDisplayed()
            onNodeWithText(text = "w92").assertExists().assertIsDisplayed()
        }
    }
}