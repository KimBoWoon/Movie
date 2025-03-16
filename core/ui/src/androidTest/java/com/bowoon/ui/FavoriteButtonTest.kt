package com.bowoon.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class FavoriteButtonTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun showFavoriteButtonTest() {
        composeTestRule.apply {
            setContent {
                FavoriteButton(
                    modifier = Modifier.testTag("favoriteButton"),
                    isFavorite = true,
                    onClick = {  }
                )
            }

            onNodeWithTag(testTag = "favoriteButton").assertIsDisplayed()
            onNodeWithContentDescription(label = "favorite").assertIsDisplayed()
            onNodeWithContentDescription(label = "unFavorite").assertIsNotDisplayed()
        }
    }

    @Test
    fun showUnFavoriteButtonTest() {
        composeTestRule.apply {
            setContent {
                FavoriteButton(
                    modifier = Modifier.testTag("favoriteButton"),
                    isFavorite = false,
                    onClick = {  }
                )
            }

            onNodeWithTag(testTag = "favoriteButton").assertIsDisplayed()
            onNodeWithContentDescription(label = "unFavorite").assertIsDisplayed()
            onNodeWithContentDescription(label = "favorite").assertIsNotDisplayed()
        }
    }

    @Test
    fun showFavoriteButtonClickTest() {
        composeTestRule.apply {
            setContent {
                var isFavorite by remember { mutableStateOf(true) }

                if (isFavorite) {
                    FavoriteButton(
                        modifier = Modifier.testTag("favoriteButton"),
                        isFavorite = isFavorite,
                        onClick = { isFavorite = false }
                    )
                } else {
                    FavoriteButton(
                        modifier = Modifier.testTag("favoriteButton"),
                        isFavorite = isFavorite,
                        onClick = { isFavorite = true }
                    )
                }
            }

            onNodeWithTag(testTag = "favoriteButton").assertIsDisplayed()
            onNodeWithContentDescription(label = "favorite").assertIsDisplayed()
            onNodeWithContentDescription(label = "unFavorite").assertIsNotDisplayed()
            onNodeWithTag(testTag = "favoriteButton").performClick()
            onNodeWithContentDescription(label = "favorite").assertIsNotDisplayed()
            onNodeWithContentDescription(label = "unFavorite").assertIsDisplayed()
        }
    }

    @Test
    fun showUnFavoriteButtonClickTest() {
        composeTestRule.apply {
            setContent {
                var isFavorite by remember { mutableStateOf(false) }

                if (isFavorite) {
                    FavoriteButton(
                        modifier = Modifier.testTag("favoriteButton"),
                        isFavorite = isFavorite,
                        onClick = { isFavorite = false }
                    )
                } else {
                    FavoriteButton(
                        modifier = Modifier.testTag("favoriteButton"),
                        isFavorite = isFavorite,
                        onClick = { isFavorite = true }
                    )
                }
            }

            onNodeWithTag(testTag = "favoriteButton").assertIsDisplayed()
            onNodeWithContentDescription(label = "unFavorite").assertIsDisplayed()
            onNodeWithContentDescription(label = "favorite").assertIsNotDisplayed()
            onNodeWithTag(testTag = "favoriteButton").performClick()
            onNodeWithContentDescription(label = "unFavorite").assertIsNotDisplayed()
            onNodeWithContentDescription(label = "favorite").assertIsDisplayed()
        }
    }
}