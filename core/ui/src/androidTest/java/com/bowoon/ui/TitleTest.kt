package com.bowoon.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.bowoon.ui.components.Title
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class TitleTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun baseTitleTest() = runTest {
        composeTestRule.apply {
            setContent {
                Title(title = "showTitle")
            }

            onNodeWithText(text = "showTitle").assertIsDisplayed()
        }
    }

    @Test
    fun backButtonTitleTest() = runTest {
        composeTestRule.apply {
            var isBackClick = false

            setContent {
                Title(
                    title = "showTitle",
                    onBackClick = { isBackClick = true }
                )
            }

            onNodeWithText(text = "showTitle").assertIsDisplayed()
            onNodeWithTag(testTag = "backButton").assertIsDisplayed().performClick()
            assertEquals(isBackClick, true)
        }
    }

    @Test
    fun favoriteTitleTest() = runTest {
        composeTestRule.apply {
            setContent {
                var isFavorite by remember { mutableStateOf(false) }

                Title(
                    title = "showTitle",
                    onBackClick = {},
                    isFavorite = isFavorite,
                    onFavoriteClick = { isFavorite = !isFavorite }
                )
            }

            onNodeWithText(text = "showTitle").assertIsDisplayed()
            onNodeWithContentDescription(label = "unFavorite").assertIsDisplayed().performClick()
            onNodeWithContentDescription(label = "unFavorite").assertIsNotDisplayed()
            onNodeWithContentDescription(label = "favorite").assertIsDisplayed()
        }
    }
}