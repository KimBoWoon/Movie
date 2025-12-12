package com.bowoon.home

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.bowoon.model.Movie
import com.bowoon.testing.repository.TestDatabaseRepository
import com.bowoon.testing.repository.TestUserDataRepository
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

class HomeScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()
    private lateinit var viewModel: HomeVM
    private lateinit var testDatabaseRepository: TestDatabaseRepository
    private lateinit var testUserDataRepository: TestUserDataRepository

    @Before
    fun setup() {
        testDatabaseRepository = TestDatabaseRepository()
        testUserDataRepository = TestUserDataRepository()
        viewModel = HomeVM(
            userDataRepository = testUserDataRepository,
            databaseRepository = testDatabaseRepository
        )
    }

    @Test
    fun homeScreenLoadingTest() {
        composeTestRule.apply {
            setContent {
                val mainMenuState by viewModel.mainMenu.collectAsStateWithLifecycle()
                val isShowNextWeekReleaseMovie = viewModel.isShowNextWeekReleaseMovie
                val nowPlayingMovies = viewModel.nowPlayingMoviePager.collectAsLazyPagingItems()
                val upComingMovies = viewModel.upComingMoviePager.collectAsLazyPagingItems()

                HomeScreen(
                    mainMenuState = MainMenuState.Loading,
                    nowPlayingMovies = nowPlayingMovies,
                    upComingMovies = upComingMovies,
                    isShowNextWeekReleaseMovie = isShowNextWeekReleaseMovie,
                    goToMovie = {},
                    onNoShowToday = {}
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
                val isShowNextWeekReleaseMovie = viewModel.isShowNextWeekReleaseMovie
                val nowPlayingMovies = viewModel.nowPlayingMoviePager.collectAsLazyPagingItems()
                val upComingMovies = viewModel.upComingMoviePager.collectAsLazyPagingItems()

                HomeScreen(
                    mainMenuState = mainMenuState,
                    nowPlayingMovies = nowPlayingMovies,
                    upComingMovies = upComingMovies,
                    isShowNextWeekReleaseMovie = isShowNextWeekReleaseMovie,
                    goToMovie = {},
                    onNoShowToday = {}
                )
            }

            runBlocking {
                AndroidThreeTen.init(composeTestRule.activity)
                val releaseDate = LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("uuuu-MM-dd"))
                testDatabaseRepository.insertMovie(movie = Movie(id = 0, title = "movie_1", posterPath = "/moviePoster.png", releaseDate = releaseDate))
            }

            onNodeWithText(text = "상영중인 영화").assertExists().assertIsDisplayed()
            onNodeWithText(text = "nowPlaying_1").assertExists().assertIsDisplayed()
            onNodeWithText(text = "개봉예정 영화").assertExists().assertIsDisplayed()
            onNodeWithText(text = "upcomingMovie_1").assertExists().assertIsDisplayed()
        }
    }

//    @Test
//    fun homeScreenErrorTest() {
//        composeTestRule.apply {
//            setContent {
//                val mainMenuState by viewModel.mainMenu.collectAsStateWithLifecycle()
//                val isShowNextWeekReleaseMovie = viewModel.isShowNextWeekReleaseMovie
//                val nowPlayingMovies = viewModel.nowPlayingMoviePager.collectAsLazyPagingItems()
//                val upComingMovies = viewModel.upComingMoviePager.collectAsLazyPagingItems()
//
//                HomeScreen(
//                    mainMenuState = MainMenuState.Error(RuntimeException("something wrong...")),
//                    nowPlayingMovies = nowPlayingMovies,
//                    upComingMovies = upComingMovies,
//                    isShowNextWeekReleaseMovie = isShowNextWeekReleaseMovie,
//                    goToMovie = {},
//                    onNoShowToday = {}
//                )
//            }
//
//            onNodeWithText(text = "something wrong...").assertExists().assertIsDisplayed()
//        }
//    }
}