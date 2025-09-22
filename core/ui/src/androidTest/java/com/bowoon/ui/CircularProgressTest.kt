package com.bowoon.ui

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.bowoon.ui.components.CircularProgressComponent
import org.junit.Rule
import org.junit.Test

class CircularProgressTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun showProgressBarTest() {
        composeTestRule.apply {
            setContent {
                var isShow by remember { mutableStateOf(false) }

                Button(onClick = { isShow = !isShow }) { Text(text = "Click Me") }

                if (isShow) {
                    CircularProgressComponent(
                        modifier = Modifier.wrapContentSize()
                    )
                }
            }

            onNodeWithContentDescription(label = "LottieProgressView").assertIsNotDisplayed()
            onNodeWithText(text = "Click Me").assertExists().assertHasClickAction().performClick()
            onNodeWithContentDescription(label = "LottieProgressView").assertIsDisplayed()
        }
    }
}