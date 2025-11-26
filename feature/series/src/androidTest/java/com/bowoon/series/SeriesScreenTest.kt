package com.bowoon.series

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToNode
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bowoon.model.MovieAppData
import com.bowoon.model.PosterSize
import com.bowoon.movie.core.network.R
import com.bowoon.testing.model.configurationTestData
import com.bowoon.testing.model.genreListTestData
import com.bowoon.testing.model.languageListTestData
import com.bowoon.testing.model.movieSeriesTestData
import com.bowoon.testing.model.regionTestData
import com.bowoon.testing.repository.TestDetailRepository
import com.bowoon.testing.utils.TestMovieAppDataManager
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SeriesScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()
    private lateinit var viewModel: SeriesVM
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var testDetailRepository: TestDetailRepository
    private lateinit var testMovieAppDataManager: TestMovieAppDataManager
    private lateinit var title: String
    private lateinit var message: String
    private lateinit var confirmString: String
    private lateinit var dismissString: String
    private val movieAppData = MovieAppData(
        secureBaseUrl = configurationTestData.images?.secureBaseUrl ?: "",
        genres = genreListTestData.genres ?: emptyList(),
        region = regionTestData.results ?: emptyList(),
        language = languageListTestData,
        posterSize = configurationTestData.images?.posterSizes?.map {
            PosterSize(size = it, isSelected = it == "original")
        } ?: emptyList()
    )

    @Before
    fun setup() {
        savedStateHandle = SavedStateHandle(initialState = mapOf("id" to 0))
        testDetailRepository = TestDetailRepository()
        viewModel = SeriesVM(
            savedStateHandle = savedStateHandle,
            detailRepository = testDetailRepository
        )
        composeTestRule.activity.apply {
            this@SeriesScreenTest.title = getString(R.string.network_failed)
            this@SeriesScreenTest.message = "error test!!"
            this@SeriesScreenTest.confirmString = getString(com.bowoon.movie.core.ui.R.string.retry_message)
            this@SeriesScreenTest.dismissString = getString(com.bowoon.movie.core.ui.R.string.back_message)
        }
        testMovieAppDataManager = TestMovieAppDataManager()
        testMovieAppDataManager.setMovieAppData(movieAppData)
    }

    @Test
    fun seriesScreenLoadingTest() {
        composeTestRule.apply {
            setContent {
                val series by viewModel.series.collectAsStateWithLifecycle()

                SeriesScreen(
                    seriesState = series,
                    goToBack = {},
                    goToMovie = {},
                    restart = {}
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
                    goToBack = {},
                    goToMovie = {},
                    restart = {}
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
            setContent {
                val series by viewModel.series.collectAsStateWithLifecycle()

                SeriesScreen(
                    seriesState = series,
                    goToBack = {},
                    goToMovie = {},
                    restart = {}
                )
            }

            runBlocking {
                testDetailRepository.setMovieSeries(movieSeries = movieSeriesTestData)
            }

            onNodeWithContentDescription(label = "seriesOverview").assertExists().assertIsDisplayed().assertTextEquals(values = arrayOf(movieSeriesTestData.overview!!))
            movieSeriesTestData.parts?.forEach {
                onNodeWithContentDescription(label = "seriesList")
                    .performScrollToNode(matcher = hasContentDescription(value = it.posterPath!!))
                    .assertExists()
                    .assertIsDisplayed()

                onNodeWithContentDescription(label = "seriesList")
                    .performScrollToNode(matcher = hasText(text = "movieSeries_${it.id}"))
                    .assertExists()
                    .assertIsDisplayed()

                onNodeWithContentDescription(label = "seriesList")
                    .performScrollToNode(matcher = hasText(text = "2024-09-23_${it.id}"))
                    .assertExists()
                    .assertIsDisplayed()

                onNodeWithContentDescription(label = "seriesList")
                    .performScrollToNode(matcher = hasText(text = "movieSeries_${it.id}_overview"))
                    .assertExists()
                    .assertIsDisplayed()
            }
        }
    }
}