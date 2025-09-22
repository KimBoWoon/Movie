package com.bowoon.ui

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.bowoon.movie.core.ui.R
import com.bowoon.ui.components.ExternalIdLinkComponent
import org.junit.Rule
import org.junit.Test

class ExternalIdLinkTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun showExternalIdLinkTest() {
        composeTestRule.apply {
            setContent {
                Row(
                    modifier = Modifier.fillMaxSize()
                ) {
                    ExternalIdLinkComponent(
                        link = "https://www.youtube.com",
                        resourceId = R.drawable.ic_youtube,
                        contentDescription = "https://www.youtube.com"
                    )
                    ExternalIdLinkComponent(
                        link = "https://www.wiki.com",
                        resourceId = R.drawable.ic_wiki,
                        contentDescription = "https://www.wiki.com"
                    )
                    ExternalIdLinkComponent(
                        link = "https://www.tiktok.com",
                        resourceId = R.drawable.ic_tiktok,
                        contentDescription = "https://www.tiktok.com"
                    )
                    ExternalIdLinkComponent(
                        link = "https://www.twitter.com",
                        resourceId = R.drawable.ic_twitter,
                        contentDescription = "https://www.twitter.com"
                    )
                    ExternalIdLinkComponent(
                        link = "https://www.facebook.com",
                        resourceId = R.drawable.ic_facebook,
                        contentDescription = "https://www.facebook.com"
                    )
                    ExternalIdLinkComponent(
                        link = "https://www.instagram.com",
                        resourceId = R.drawable.ic_instagram,
                        contentDescription = "https://www.instagram.com"
                    )
                }
            }

            onNodeWithContentDescription(label = "https://www.youtube.com").assertExists().assertIsDisplayed().assertHasClickAction()
            onNodeWithContentDescription(label = "https://www.wiki.com").assertExists().assertIsDisplayed().assertHasClickAction()
            onNodeWithContentDescription(label = "https://www.tiktok.com").assertExists().assertIsDisplayed().assertHasClickAction()
            onNodeWithContentDescription(label = "https://www.twitter.com").assertExists().assertIsDisplayed().assertHasClickAction()
            onNodeWithContentDescription(label = "https://www.facebook.com").assertExists().assertIsDisplayed().assertHasClickAction()
            onNodeWithContentDescription(label = "https://www.instagram.com").assertExists().assertIsDisplayed().assertHasClickAction()
        }
    }
}