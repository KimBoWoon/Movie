package com.cheeke.surfy.detail

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
import com.cheeke.surfy.core.network.R
import com.cheeke.surfy.core.network.R
import com.cheeke.surfy.detail.series.SeriesScreen
import com.cheeke.surfy.detail.series.SeriesState
import com.cheeke.surfy.detail.series.SeriesVM
import com.cheeke.surfy.model.PosterSize
import com.cheeke.surfy.model.SurfyAppData
import com.cheeke.surfy.testing.model.configurationTestData
import com.cheeke.surfy.testing.model.genreListTestData
import com.cheeke.surfy.testing.model.languageListTestData
import com.cheeke.surfy.testing.model.movieSeriesTestData
import com.cheeke.surfy.testing.model.regionTestData
import com.cheeke.surfy.testing.repository.TestDetailRepository
import com.cheeke.surfy.testing.utils.TestMovieAppDataManager
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
    private val surfyAppData = SurfyAppData(
        secureBaseUrl = configurationTestData.images?.secureBaseUrl ?: "",
        movieGenres = genreListTestData.genres ?: emptyList(),
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
            id = 0,
            detailRepository = testDetailRepository
        )
        composeTestRule.activity.apply {
            this@SeriesScreenTest.title = getString(R.string.network_failed)
            this@SeriesScreenTest.message = "error test!!"
            this@SeriesScreenTest.confirmString = getString(com.cheeke.surfy.core.ui.R.string.retry_message)
            this@SeriesScreenTest.dismissString = getString(com.cheeke.surfy.core.ui.R.string.back_message)
        }
        testMovieAppDataManager = TestMovieAppDataManager()
        testMovieAppDataManager.setMovieAppData(surfyAppData)
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