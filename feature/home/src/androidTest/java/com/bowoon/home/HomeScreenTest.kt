package com.bowoon.home

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToNode
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bowoon.testing.model.nowPlayingMovieTest
import com.bowoon.testing.model.upComingMovieTest
import com.bowoon.testing.repository.TestDatabaseRepository
import com.bowoon.testing.repository.TestPagingRepository
import com.bowoon.testing.repository.TestUserDataRepository
import com.bowoon.testing.utils.TestNetworkMonitor
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()
    private lateinit var viewModel: HomeVM
    private lateinit var testDatabaseRepository: TestDatabaseRepository
    private lateinit var testPagingRepository: TestPagingRepository
    private lateinit var testNetworkMonitor: TestNetworkMonitor
    private lateinit var testUserRepository: TestUserDataRepository

    @Before
    fun setup() {
        testDatabaseRepository = TestDatabaseRepository()
        viewModel = HomeVM(
            databaseRepository = testDatabaseRepository,
            pagingRepository = testPagingRepository,
            userDataRepository = testUserRepository,
            networkMonitor = testNetworkMonitor
        )
    }

    @Test
    fun homeScreenLoadingTest() {
        composeTestRule.apply {
            setContent {
                HomeScreen(
                    mainMenuState = MainMenuState.Loading,
                    trendingTvTimeWindow = viewModel.trendingTvTimeWindow.collectAsStateWithLifecycle().value,
                    updateTrendingTvTimeWindow = viewModel::updateTrendingTvTimeWindow,
                    trendingPeopleTimeWindow = viewModel.trendingPeopleTimeWindow.collectAsStateWithLifecycle().value,
                    updateTrendingPeopleTimeWindow = viewModel::updateTrendingPeopleTimeWindow,
                    trendingMovieTimeWindow = viewModel.trendingTvTimeWindow.collectAsStateWithLifecycle().value,
                    updateTrendingMovieTimeWindow = viewModel::updateTrendingMovieTimeWindow,
                    goToMovie = {},
                    goToPeople = {},
                    goToTv = {}
                )
            }

            onNodeWithContentDescription(label = "homeLoading").assertExists().assertIsDisplayed()
        }
    }

    @Test
    fun homeScreenSuccessTest() {
        composeTestRule.apply {
            setContent {
                val mainMenuState by viewModel.mainMenu.collectAsStateWithLifecycle()

                HomeScreen(
                    mainMenuState = MainMenuState.Loading,
                    trendingTvTimeWindow = viewModel.trendingTvTimeWindow.collectAsStateWithLifecycle().value,
                    updateTrendingTvTimeWindow = viewModel::updateTrendingTvTimeWindow,
                    trendingPeopleTimeWindow = viewModel.trendingPeopleTimeWindow.collectAsStateWithLifecycle().value,
                    updateTrendingPeopleTimeWindow = viewModel::updateTrendingPeopleTimeWindow,
                    trendingMovieTimeWindow = viewModel.trendingTvTimeWindow.collectAsStateWithLifecycle().value,
                    updateTrendingMovieTimeWindow = viewModel::updateTrendingMovieTimeWindow,
                    goToMovie = {},
                    goToPeople = {},
                    goToTv = {}
                )
            }

            onNodeWithText(text = "상영중인 영화").assertExists().assertIsDisplayed()
            nowPlayingMovieTest.forEachIndexed { index, entity ->
                onNodeWithContentDescription(label = "nowPlayingMovies")
                    .assertExists()
                    .assertIsDisplayed()
                    .performScrollToNode(matcher = hasText(text = "nowPlaying_$index") and hasText(text = "nowPlaying_releaseDate_$index"))
                    .assertExists()
                    .assertIsDisplayed()
            }
            onNodeWithText(text = "개봉예정 영화").assertExists().assertIsDisplayed()
            upComingMovieTest.forEachIndexed { index, entity ->
                onNodeWithContentDescription(label = "upComingMovies")
                    .assertExists()
                    .assertIsDisplayed()
                    .performScrollToNode(matcher = hasText(text = "upcomingMovie_$index") and hasText(text = "upcomingMovie_releaseDate_$index"))
                    .assertExists()
                    .assertIsDisplayed()
            }
        }
    }

    @Test
    fun homeScreenErrorTest() {
        composeTestRule.apply {
            setContent {
                HomeScreen(
                    mainMenuState = MainMenuState.Error(throwable = RuntimeException("something wrong...")),
                    trendingTvTimeWindow = viewModel.trendingTvTimeWindow.collectAsStateWithLifecycle().value,
                    updateTrendingTvTimeWindow = viewModel::updateTrendingTvTimeWindow,
                    trendingPeopleTimeWindow = viewModel.trendingPeopleTimeWindow.collectAsStateWithLifecycle().value,
                    updateTrendingPeopleTimeWindow = viewModel::updateTrendingPeopleTimeWindow,
                    trendingMovieTimeWindow = viewModel.trendingTvTimeWindow.collectAsStateWithLifecycle().value,
                    updateTrendingMovieTimeWindow = viewModel::updateTrendingMovieTimeWindow,
                    goToMovie = {},
                    goToPeople = {},
                    goToTv = {}
                )
            }

            onNodeWithText(text = "something wrong...").assertExists().assertIsDisplayed()
        }
    }
}