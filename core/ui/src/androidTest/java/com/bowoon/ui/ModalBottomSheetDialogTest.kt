package com.bowoon.ui

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.bowoon.model.Image
import com.bowoon.ui.dialog.Indexer
import com.bowoon.ui.dialog.ModalBottomSheetDialog
import com.bowoon.ui.image.DynamicAsyncImageLoader
import com.bowoon.ui.utils.dp10
import com.bowoon.ui.utils.dp20
import kotlinx.coroutines.launch
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
                var isShowing by remember { mutableStateOf(false) }
                val images = listOf(Image(), Image(), Image(), Image())
                val scope = rememberCoroutineScope()
                val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                val index = 0

                Button(onClick = { isShowing = !isShowing }) { Text(text = "Click Me") }

                if (isShowing) {
                    ModalBottomSheetDialog(
                        state = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                        scope = rememberCoroutineScope(),
                        onClickCancel = {
                            scope.launch {
                                isShowing = false
                                modalBottomSheetState.hide()
                            }
                        },
                        content = {
                            val pagerState = rememberPagerState(initialPage = index) { images.size }
                            var currentIndex by remember { mutableIntStateOf(value = index + 1) }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(ratio = images.minOf { it.aspectRatio?.toFloat() ?: 1f }),
                                contentAlignment = Alignment.Center
                            ) {
                                HorizontalPager(
                                    modifier = Modifier.testTag(tag = "modalBottomSheetDialog"),
                                    state = pagerState
                                ) { index ->
                                    currentIndex = pagerState.currentPage + 1

                                    DynamicAsyncImageLoader(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(ratio = images[index].aspectRatio?.toFloat() ?: 1f),
                                        source = images[index].filePath ?: "",
                                        contentDescription = "PosterView"
                                    )
                                }

                                Indexer(
                                    modifier = Modifier
                                        .padding(top = dp10, end = dp20)
                                        .wrapContentSize()
                                        .background(color = Color(0x33000000), shape = RoundedCornerShape(dp20))
                                        .align(Alignment.TopEnd),
                                    current = currentIndex,
                                    size = images.size
                                )
                            }
                        }
                    )
                }
            }

            onNodeWithTag(testTag = "modalBottomSheetDialog").assertIsNotDisplayed()
            onNodeWithText(text = "Click Me").assertExists().assertHasClickAction().performClick()
            onNodeWithTag(testTag = "modalBottomSheetDialog").assertIsDisplayed()
        }
    }
}