package com.bowoon.ui

import androidx.activity.ComponentActivity
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.bowoon.model.Image
import com.bowoon.ui.dialog.ModalBottomSheetDialog
import org.junit.Rule
import org.junit.Test

class ModalBottomSheetDialogTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun showDialogTest() {
        composeTestRule.apply {
            setContent {
                var isShow by remember { mutableStateOf(false) }

                Button(onClick = { isShow = !isShow }) { Text(text = "Click Me") }

                if (isShow) {
                    ModalBottomSheetDialog(
                        modifier = Modifier.testTag("modalBottomSheetDialog"),
                        state = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                        scope = rememberCoroutineScope(),
                        index = 0,
                        imageList = listOf(
                            Image(),
                            Image(),
                            Image(),
                            Image()
                        ),
                        onClickCancel = {}
                    )
                }
            }

            onNodeWithTag(testTag = "modalBottomSheetDialog").assertIsNotDisplayed()
            onNodeWithText(text = "Click Me").assertExists().assertHasClickAction().performClick()
            onNodeWithTag(testTag = "modalBottomSheetDialog").assertIsDisplayed()
        }
    }
}