package com.bowoon.series

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToNode
import com.bowoon.data.repository.LocalMovieAppDataComposition
import com.bowoon.testing.model.movieSeriesTestData
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SeriesScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()
    private lateinit var title: String
    private lateinit var message: String
    private lateinit var confirmString: String
    private lateinit var dismissString: String

    @Before
    fun setup() {
        composeTestRule.activity.apply {
            this@SeriesScreenTest.title = getString(com.bowoon.movie.core.network.R.string.network_failed)
            this@SeriesScreenTest.message = "error test!!"
            this@SeriesScreenTest.confirmString = getString(com.bowoon.movie.core.ui.R.string.retry_message)
            this@SeriesScreenTest.dismissString = getString(com.bowoon.movie.core.ui.R.string.confirm_message)
        }
    }

    @Test
    fun seriesScreenLoadingTest() {
        composeTestRule.apply {
            setContent {
                SeriesScreen(
                    seriesState = SeriesState.Loading,
                    onBack = {},
                    goToMovie = {}
                )
            }

            onNodeWithContentDescription(label = "seriesLoading").assertExists().assertIsDisplayed()
        }
    }

    @Test
    fun seriesScreenErrorTest() {
        composeTestRule.apply {
            setContent {
                SeriesScreen(
                    seriesState = SeriesState.Error(Throwable(message)),
                    onBack = {},
                    goToMovie = {}
                )
            }

            onNodeWithText(text = title).assertExists().assertIsDisplayed()
            onNodeWithText(text = message).assertExists().assertIsDisplayed()
            onNodeWithText(text = confirmString).assertExists().assertIsDisplayed()
            onNodeWithText(text = dismissString).assertExists().assertIsDisplayed()
        }
    }

    @Test
    fun seriesScreenSuccessTest() {
        composeTestRule.apply {
            var posterUrl = ""

            setContent {
                posterUrl = LocalMovieAppDataComposition.current.getImageUrl()

                SeriesScreen(
                    seriesState = SeriesState.Success(movieSeriesTestData),
                    onBack = {},
                    goToMovie = {}
                )
            }

            onNodeWithContentDescription(label = "seriesOverview").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "$posterUrl${movieSeriesTestData.posterPath}").assertExists().assertIsDisplayed()
            movieSeriesTestData.parts?.forEach {
                onNodeWithContentDescription(label = "seriesList").performScrollToNode(hasContentDescription(value = "seriesTitle_${it?.id}")).assertExists().assertIsDisplayed()
                onNodeWithContentDescription(label = "seriesList").performScrollToNode(hasContentDescription(value = "seriesReleaseDate_${it?.id}")).assertExists().assertIsDisplayed()
                onNodeWithContentDescription(label = "seriesList").performScrollToNode(hasContentDescription(value = "seriesOverview_${it?.id}")).assertExists().assertIsDisplayed()
            }
        }
    }
}