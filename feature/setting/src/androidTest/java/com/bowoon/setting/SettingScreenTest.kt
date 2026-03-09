package com.bowoon.setting

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bowoon.model.LocaleOption
import com.bowoon.model.SurfyAppData
import com.bowoon.model.PosterSize
import com.bowoon.testing.model.configurationTestData
import com.bowoon.testing.model.genreListTestData
import com.bowoon.testing.model.languageListTestData
import com.bowoon.testing.model.regionTestData
import com.bowoon.testing.repository.TestUserDataRepository
import com.bowoon.testing.utils.TestMovieAppDataManager
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SettingScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()
    private lateinit var viewModel: SettingVM
    private lateinit var testUserDataRepository: TestUserDataRepository
    private lateinit var testMovieAppDataManager: TestMovieAppDataManager
    private val surfyAppData = SurfyAppData(
        secureBaseUrl = configurationTestData.images?.secureBaseUrl ?: "",
        movieGenres = genreListTestData.genres ?: emptyList(),
        region = regionTestData.results?.map { LocaleOption(code = it.iso31661 ?: "", label = it.englishName ?: "", isSelected = false) }.orEmpty(),
        language = languageListTestData.map { LocaleOption(code = it.iso6391 ?: "", label = it.englishName ?: "", isSelected = false) },
        posterSize = configurationTestData.images?.posterSizes?.map {
            PosterSize(size = it, isSelected = it == "original")
        } ?: emptyList()
    )

    @Before
    fun setup() {
        testUserDataRepository = TestUserDataRepository()
        testMovieAppDataManager = TestMovieAppDataManager()
        viewModel = SettingVM(
            userDataRepository = testUserDataRepository,
            dataManager = testMovieAppDataManager
        )

        testMovieAppDataManager.setMovieAppData(surfyAppData)
    }

    @Test
    fun myScreenTest() {
        composeTestRule.apply {
            setContent {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                SettingScreen(
                    state = uiState,
                    onAction = {}
                )
            }

            onNodeWithText(text = "메인 업데이트 날짜").assertExists().assertIsDisplayed()
            onNodeWithText(text = "2025-03-12").assertExists().assertIsDisplayed()
            onNodeWithText(text = "다크 모드 설정").assertExists().assertIsDisplayed()
            onNodeWithText(text = "성인").assertExists().assertIsDisplayed()
            onNodeWithText(text = "예고편 자동 재생").assertExists().assertIsDisplayed()
            onNodeWithText(text = "언어").assertExists().assertIsDisplayed()
            onNodeWithText(text = "ko-KR").assertExists().assertIsDisplayed()
            onNodeWithText(text = "이미지 퀄리티").assertExists().assertIsDisplayed()
            onNodeWithText(text = "original").assertExists().assertIsDisplayed()
            onNodeWithText(text = "버전 정보").assertExists().assertIsDisplayed()
//            onNodeWithText(text = getVersionName(context = composeTestRule.activity)).assertExists().assertIsDisplayed()
        }
    }
}