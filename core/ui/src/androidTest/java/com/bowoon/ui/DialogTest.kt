package com.bowoon.ui

import androidx.activity.ComponentActivity
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class DialogTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun showDialogTest() {
        composeTestRule.apply {
            setContent {
                var isShow by remember { mutableStateOf(false) }

                Button(onClick = { isShow = !isShow }) { Text(text = "Click Me") }

                if (isShow) {
                    ConfirmDialog(
                        title = "Hello",
                        message = "World",
                        confirmPair = "OK" to {  },
                        dismissPair = "Cancel" to {  }
                    )
                }
            }

            onNodeWithText(text = "Hello").assertIsNotDisplayed()
            onNodeWithText(text = "Click Me").assertExists().assertHasClickAction().performClick()
            onNodeWithText(text = "Hello").assertIsDisplayed()
        }
    }

    @Test
    fun dialogOkClickTest() {
        composeTestRule.apply {
            var isOkClick = false

            setContent {
                var isShow by remember { mutableStateOf(false) }

                Button(onClick = { isShow = !isShow }) { Text(text = "Click Me") }

                if (isShow) {
                    ConfirmDialog(
                        title = "Hello",
                        message = "World",
                        confirmPair = "OK" to { isOkClick = true },
                        dismissPair = "Cancel" to {  }
                    )
                }
            }

            assertEquals(isOkClick, false)
            onNodeWithText(text = "Hello").assertIsNotDisplayed()
            onNodeWithText(text = "OK").assertIsNotDisplayed()
            onNodeWithText(text = "Click Me").assertExists().assertHasClickAction().performClick()
            onNodeWithText(text = "Hello").assertIsDisplayed()
            onNodeWithText(text = "OK").assertIsDisplayed().performClick()
            onNodeWithText(text = "Hello").assertIsNotDisplayed()
            assertEquals(isOkClick, true)
        }
    }

    @Test
    fun dialogCancelClickTest() {
        composeTestRule.apply {
            var isCancelClick = false

            setContent {
                var isShow by remember { mutableStateOf(false) }

                Button(onClick = { isShow = !isShow }) { Text(text = "Click Me") }

                if (isShow) {
                    ConfirmDialog(
                        title = "Hello",
                        message = "World",
                        confirmPair = "OK" to {  },
                        dismissPair = "Cancel" to { isCancelClick = true }
                    )
                }
            }

            assertEquals(isCancelClick, false)
            onNodeWithText(text = "Hello").assertIsNotDisplayed()
            onNodeWithText(text = "Cancel").assertIsNotDisplayed()
            onNodeWithText(text = "Click Me").assertExists().assertHasClickAction().performClick()
            onNodeWithText(text = "Hello").assertIsDisplayed()
            onNodeWithText(text = "Cancel").assertIsDisplayed().performClick()
            onNodeWithText(text = "Hello").assertIsNotDisplayed()
            assertEquals(isCancelClick, true)
        }
    }

    @Test
    fun dialogOneButtonTest() {
        composeTestRule.apply {
            setContent {
                var isShow by remember { mutableStateOf(false) }

                Button(onClick = { isShow = !isShow }) { Text(text = "Click Me") }

                if (isShow) {
                    ConfirmDialog(
                        title = "Hello",
                        message = "World",
                        confirmPair = "OK" to {  }
                    )
                }
            }

            onNodeWithText(text = "Hello").assertIsNotDisplayed()
            onNodeWithText(text = "OK").assertIsNotDisplayed()
            onNodeWithText(text = "Cancel").assertIsNotDisplayed()
            onNodeWithText(text = "Click Me").assertExists().assertHasClickAction().performClick()
            onNodeWithText(text = "Hello").assertIsDisplayed()
            onNodeWithText(text = "Cancel").assertIsNotDisplayed()
            onNodeWithText(text = "OK").assertIsDisplayed().performClick()
            onNodeWithText(text = "Hello").assertIsNotDisplayed()
        }
    }
}