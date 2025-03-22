package com.bowoon.home

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.bowoon.testing.model.mainMenuTestData
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun homeScreenLoadingTest() {
        composeTestRule.apply {
            setContent {
                HomeScreen(
                    isSyncing = false,
                    state = MainMenuState.Loading,
                    onShowSnackbar = { _, _ -> true },
                    onMovieClick = {}
                )
            }

            onNodeWithContentDescription(label = "homeLoading").assertExists().assertIsDisplayed()
        }
    }

    @Test
    fun homeScreenSuccessTest() {
        composeTestRule.apply {
            setContent {
                HomeScreen(
                    isSyncing = false,
                    state = MainMenuState.Success(mainMenuTestData),
                    onShowSnackbar = { _, _ -> true },
                    onMovieClick = {}
                )
            }

            onNodeWithText(text = "상영중인 영화").assertExists().assertIsDisplayed()
            onNodeWithText(text = "nowPlaying_1").assertExists().assertIsDisplayed()
            onNodeWithText(text = "개봉 예정작").assertExists().assertIsDisplayed()
            onNodeWithText(text = "upcomingMovie_1").assertExists().assertIsDisplayed()
        }
    }
}