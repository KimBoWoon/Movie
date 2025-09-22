package com.bowoon.movie

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.bowoon.data.util.NetworkMonitor
import com.bowoon.movie.ui.activities.MainActivity
import com.bowoon.testing.rules.GrantPostNotificationsPermissionRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class NavigationTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val postNotificationsPermission = GrantPostNotificationsPermissionRule()

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun navigationTest() {
        composeTestRule.apply {
            onNodeWithText(text = "홈").assertExists().assertIsDisplayed()
            onNodeWithText(text = "검색").assertExists().assertIsDisplayed()
            onNodeWithText(text = "찜").assertExists().assertIsDisplayed()
            onNodeWithText(text = "마이").assertExists().assertIsDisplayed()
        }
    }

    @Test
    fun firstScreenTest() {
        composeTestRule.apply {
//            onNodeWithText(text = "홈").assertExists().assertIsDisplayed().assertIsSelected()
            onNodeWithContentDescription(label = "selected_홈").assertExists().assertIsDisplayed()
        }
    }

    @Test
    fun changeScreen() {
        composeTestRule.apply {
            onNodeWithContentDescription(label = "홈").assertExists().assertIsDisplayed().assertIsSelected()
            onNodeWithContentDescription(label = "검색").assertExists().assertIsDisplayed().assertIsNotSelected()
            onNodeWithContentDescription(label = "찜").assertExists().assertIsDisplayed().assertIsNotSelected()
            onNodeWithContentDescription(label = "마이").assertExists().assertIsDisplayed().assertIsNotSelected()

            onNodeWithContentDescription(label = "검색").assertExists().assertIsDisplayed().assertIsNotSelected().performClick()

            onNodeWithContentDescription(label = "홈").assertExists().assertIsDisplayed().assertIsNotSelected()
            onNodeWithContentDescription(label = "검색").assertExists().assertIsDisplayed().assertIsSelected()
            onNodeWithContentDescription(label = "찜").assertExists().assertIsDisplayed().assertIsNotSelected()
            onNodeWithContentDescription(label = "마이").assertExists().assertIsDisplayed().assertIsNotSelected()

            onNodeWithContentDescription(label = "찜").assertExists().assertIsDisplayed().assertIsNotSelected().performClick()

            onNodeWithContentDescription(label = "홈").assertExists().assertIsDisplayed().assertIsNotSelected()
            onNodeWithContentDescription(label = "검색").assertExists().assertIsDisplayed().assertIsNotSelected()
            onNodeWithContentDescription(label = "찜").assertExists().assertIsDisplayed().assertIsSelected()
            onNodeWithContentDescription(label = "마이").assertExists().assertIsDisplayed().assertIsNotSelected()

            onNodeWithContentDescription(label = "마이").assertExists().assertIsDisplayed().assertIsNotSelected().performClick()

            onNodeWithContentDescription(label = "홈").assertExists().assertIsDisplayed().assertIsNotSelected()
            onNodeWithContentDescription(label = "검색").assertExists().assertIsDisplayed().assertIsNotSelected()
            onNodeWithContentDescription(label = "찜").assertExists().assertIsDisplayed().assertIsNotSelected()
            onNodeWithContentDescription(label = "마이").assertExists().assertIsDisplayed().assertIsSelected()

//            onNodeWithContentDescription(label = "selected_홈").assertExists().assertIsDisplayed()
//            onNodeWithContentDescription(label = "unselected_검색").assertExists().assertIsDisplayed()
//            onNodeWithContentDescription(label = "unselected_찜").assertExists().assertIsDisplayed()
//            onNodeWithContentDescription(label = "unselected_마이").assertExists().assertIsDisplayed()
//
//            onNodeWithContentDescription(label = "unselected_검색").assertExists().assertIsDisplayed().performClick()
//
//            onNodeWithContentDescription(label = "unselected_홈").assertExists().assertIsDisplayed()
//            onNodeWithContentDescription(label = "selected_검색").assertExists().assertIsDisplayed()
//            onNodeWithContentDescription(label = "unselected_찜").assertExists().assertIsDisplayed()
//            onNodeWithContentDescription(label = "unselected_마이").assertExists().assertIsDisplayed()
//
//            onNodeWithContentDescription(label = "unselected_찜").assertExists().assertIsDisplayed().performClick()
//
//            onNodeWithContentDescription(label = "unselected_홈").assertExists().assertIsDisplayed()
//            onNodeWithContentDescription(label = "unselected_검색").assertExists().assertIsDisplayed()
//            onNodeWithContentDescription(label = "selected_찜").assertExists().assertIsDisplayed()
//            onNodeWithContentDescription(label = "unselected_마이").assertExists().assertIsDisplayed()
//
//            onNodeWithContentDescription(label = "unselected_마이").assertExists().assertIsDisplayed().performClick()
//
//            onNodeWithContentDescription(label = "unselected_홈").assertExists().assertIsDisplayed()
//            onNodeWithContentDescription(label = "unselected_검색").assertExists().assertIsDisplayed()
//            onNodeWithContentDescription(label = "unselected_찜").assertExists().assertIsDisplayed()
//            onNodeWithContentDescription(label = "selected_마이").assertExists().assertIsDisplayed()
        }
    }
}