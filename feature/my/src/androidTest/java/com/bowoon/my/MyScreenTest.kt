package com.bowoon.my

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bowoon.common.getVersionName
import com.bowoon.model.InternalData
import com.bowoon.model.MovieAppData
import com.bowoon.model.PosterSize
import com.bowoon.testing.model.configurationTestData
import com.bowoon.testing.model.genreListTestData
import com.bowoon.testing.model.languageListTestData
import com.bowoon.testing.model.regionTestData
import com.bowoon.testing.repository.TestMovieAppDataRepository
import com.bowoon.testing.repository.TestUserDataRepository
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MyScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()
    private lateinit var testUserDataRepository: TestUserDataRepository
    private lateinit var testMovieAppDataRepository: TestMovieAppDataRepository

    @Before
    fun setup() {
        testUserDataRepository = TestUserDataRepository()
        testMovieAppDataRepository = TestMovieAppDataRepository()
        val movieAppData = MovieAppData(
            secureBaseUrl = configurationTestData.images?.secureBaseUrl ?: "",
            genres = genreListTestData.genres ?: emptyList(),
            region = regionTestData.results ?: emptyList(),
            language = languageListTestData.map { it.copy(isSelected = it.name == "en") },
            posterSize = configurationTestData.images?.posterSizes?.map {
                PosterSize(size = it, isSelected = it == "original")
            } ?: emptyList()
        )

        testMovieAppDataRepository.setMovieAppData(movieAppData)
        runBlocking {
            testUserDataRepository.updateUserData(
                userData = InternalData(
                    updateDate = "2025-03-12"
                ),
                isSync = false
            )
        }
    }

    @Test
    fun myScreenTest() {
        composeTestRule.apply {
            setContent {
                val internalData by testUserDataRepository.internalData.collectAsStateWithLifecycle(initialValue = InternalData())
                val movieAppData by testMovieAppDataRepository.movieAppData.collectAsStateWithLifecycle()

                MyScreen(
                    internalData = internalData,
                    updateUserData = { _, _ -> },
                    movieAppData = movieAppData
                )
            }

            onNodeWithText(text = "메인 업데이트 날짜").assertExists().assertIsDisplayed()
            onNodeWithText(text = "2025-03-12").assertExists().assertIsDisplayed()
            onNodeWithText(text = "다크 모드 설정").assertExists().assertIsDisplayed()
            onNodeWithText(text = "성인").assertExists().assertIsDisplayed()
            onNodeWithText(text = "예고편 자동 재생").assertExists().assertIsDisplayed()
            onNodeWithText(text = "언어").assertExists().assertIsDisplayed()
            onNodeWithText(text = "en (en)").assertExists().assertIsDisplayed()
            onNodeWithText(text = "지역").assertExists().assertIsDisplayed()
            onNodeWithText(text = "ko (ko)").assertExists().assertIsDisplayed()
            onNodeWithText(text = "이미지 퀄리티").assertExists().assertIsDisplayed()
            onNodeWithText(text = "original").assertExists().assertIsDisplayed()
            onNodeWithText(text = "버전 정보").assertExists().assertIsDisplayed()
//            onNodeWithText(text = getVersionName(composeTestRule.activity)).assertExists().assertIsDisplayed()
        }
    }
}