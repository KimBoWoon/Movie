package com.cheeke.surfy.favorite

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.model.People
import com.cheeke.surfy.testing.repository.TestDatabaseRepository
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FavoriteScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()
    private lateinit var viewModel: FavoriteVM
    private lateinit var testDataBaseRepository: TestDatabaseRepository

    @Before
    fun setup() {
        testDataBaseRepository = TestDatabaseRepository()
        viewModel = FavoriteVM(
            initialTabIndex = 0,
            databaseRepository = testDataBaseRepository
        )
    }

    @Test
    fun favoriteMovieEmptyTest() {
        composeTestRule.apply {
            setContent {
                val favoriteMovies by viewModel.favoriteMovies.collectAsStateWithLifecycle()
                val favoritePeople by viewModel.favoritePeoples.collectAsStateWithLifecycle()
                val favoriteTvs by viewModel.favoriteTvs.collectAsStateWithLifecycle()

                FavoriteScreen(
                    favoriteMovies = favoriteMovies,
                    favoritePeoples = favoritePeople,
                    favoriteTvs = favoriteTvs,
                    onShowSnackbar = { _, _ -> true },
                    goToTv = {},
                    goToMovie = {},
                    goToPeople = {},
                    updateTabIndex = viewModel::updateTabIndex,
                    deleteFavoriteMovie = viewModel::deleteMovie,
                    deleteFavoritePeople = viewModel::deletePeople,
                    deleteFavoriteTv = viewModel::deleteTv
                )
            }

            onNodeWithText(text = "영화").assertExists().assertIsDisplayed().performClick()
            onNodeWithTag(testTag = "favoriteMovieEmpty").assertExists().assertIsDisplayed()
            onNodeWithText(text = "찜한 영화가 없습니다.").assertExists().assertIsDisplayed()
        }
    }

    @Test
    fun favoritePeopleEmptyTest() {
        composeTestRule.apply {
            setContent {
                val favoriteMovies by viewModel.favoriteMovies.collectAsStateWithLifecycle()
                val favoritePeople by viewModel.favoritePeoples.collectAsStateWithLifecycle()
                val favoriteTvs by viewModel.favoriteTvs.collectAsStateWithLifecycle()

                FavoriteScreen(
                    favoriteMovies = favoriteMovies,
                    favoritePeoples = favoritePeople,
                    favoriteTvs = favoriteTvs,
                    onShowSnackbar = { _, _ -> true },
                    goToTv = {},
                    goToMovie = {},
                    goToPeople = {},
                    updateTabIndex = viewModel::updateTabIndex,
                    deleteFavoriteMovie = viewModel::deleteMovie,
                    deleteFavoritePeople = viewModel::deletePeople,
                    deleteFavoriteTv = viewModel::deleteTv
                )
            }

            onNodeWithText(text = "인물").assertExists().assertIsDisplayed().performClick()
            onNodeWithTag(testTag = "favoritePeopleEmpty").assertExists().assertIsDisplayed()
            onNodeWithText(text = "찜한 인물이 없습니다.").assertExists().assertIsDisplayed()
        }
    }

    @Test
    fun favoriteMovieTest() {
        composeTestRule.apply {
            setContent {
                val favoriteMovies by viewModel.favoriteMovies.collectAsStateWithLifecycle()
                val favoritePeople by viewModel.favoritePeoples.collectAsStateWithLifecycle()
                val favoriteTvs by viewModel.favoriteTvs.collectAsStateWithLifecycle()

                FavoriteScreen(
                    favoriteMovies = favoriteMovies,
                    favoritePeoples = favoritePeople,
                    favoriteTvs = favoriteTvs,
                    onShowSnackbar = { _, _ -> true },
                    goToTv = {},
                    goToMovie = {},
                    goToPeople = {},
                    updateTabIndex = viewModel::updateTabIndex,
                    deleteFavoriteMovie = viewModel::deleteMovie,
                    deleteFavoritePeople = viewModel::deletePeople,
                    deleteFavoriteTv = viewModel::deleteTv
                )
            }

            runBlocking {
                testDataBaseRepository.insertMovie(movie = Movie(id = 0, title = "movie_1", posterPath = "/movieImage.png"))
            }

            onNodeWithText(text = "영화").assertExists().assertIsDisplayed().performClick()
            onNodeWithTag(testTag = "favoriteMovieEmpty").assertIsNotDisplayed()
            onNodeWithText(text = "찜한 영화가 없습니다.").assertIsNotDisplayed()
            onNodeWithContentDescription(label = "FavoriteMoviePoster").assertExists().assertIsDisplayed()
        }
    }

    @Test
    fun favoritePeopleTest() {
        composeTestRule.apply {
            setContent {
                val favoriteMovies by viewModel.favoriteMovies.collectAsStateWithLifecycle()
                val favoritePeople by viewModel.favoritePeoples.collectAsStateWithLifecycle()
                val favoriteTvs by viewModel.favoriteTvs.collectAsStateWithLifecycle()

                FavoriteScreen(
                    favoriteMovies = favoriteMovies,
                    favoritePeoples = favoritePeople,
                    favoriteTvs = favoriteTvs,
                    onShowSnackbar = { _, _ -> true },
                    goToTv = {},
                    goToMovie = {},
                    goToPeople = {},
                    updateTabIndex = viewModel::updateTabIndex,
                    deleteFavoriteMovie = viewModel::deleteMovie,
                    deleteFavoritePeople = viewModel::deletePeople,
                    deleteFavoriteTv = viewModel::deleteTv
                )
            }

            runBlocking {
                testDataBaseRepository.insertPeople(people = People(id = 0, title = "people_1", posterPath = "/peopleImage.png"))
            }

            onNodeWithText(text = "인물").assertExists().assertIsDisplayed().performClick()
            onNodeWithTag(testTag = "favoritePeopleEmpty").assertIsNotDisplayed()
            onNodeWithText(text = "찜한 인물이 없습니다.").assertIsNotDisplayed()
            onNodeWithContentDescription(label = "FavoritePeopleProfileImage").assertExists().assertIsDisplayed()
        }
    }

    @Test
    fun deleteMovieTest() {
        composeTestRule.apply {
            setContent {
                val favoriteMovies by viewModel.favoriteMovies.collectAsStateWithLifecycle()
                val favoritePeople by viewModel.favoritePeoples.collectAsStateWithLifecycle()
                val favoriteTvs by viewModel.favoriteTvs.collectAsStateWithLifecycle()

                FavoriteScreen(
                    favoriteMovies = favoriteMovies,
                    favoritePeoples = favoritePeople,
                    favoriteTvs = favoriteTvs,
                    onShowSnackbar = { _, _ -> true },
                    goToTv = {},
                    goToMovie = {},
                    goToPeople = {},
                    updateTabIndex = viewModel::updateTabIndex,
                    deleteFavoriteMovie = viewModel::deleteMovie,
                    deleteFavoritePeople = viewModel::deletePeople,
                    deleteFavoriteTv = viewModel::deleteTv
                )
            }

            runBlocking {
                testDataBaseRepository.insertMovie(movie = Movie(id = 0, title = "movie_1", posterPath = "/movieImage.png"))
            }

            onNodeWithText(text = "영화").assertExists().assertIsDisplayed().performClick()
            onNodeWithTag(testTag = "favoriteMovieEmpty").assertIsNotDisplayed()
            onNodeWithText(text = "찜한 영화가 없습니다.").assertIsNotDisplayed()
            onNodeWithContentDescription(label = "FavoriteMoviePoster").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "favorite").assertExists().assertIsDisplayed().performClick()
            onNodeWithContentDescription(label = "FavoriteMoviePoster").assertIsNotDisplayed()
            onNodeWithTag(testTag = "favoriteMovieEmpty").assertExists().assertIsDisplayed()
            onNodeWithText(text = "찜한 영화가 없습니다.").assertExists().assertIsDisplayed()
        }
    }

    @Test
    fun deletePeopleTest() {
        composeTestRule.apply {
            setContent {
                val favoriteMovies by viewModel.favoriteMovies.collectAsStateWithLifecycle()
                val favoritePeople by viewModel.favoritePeoples.collectAsStateWithLifecycle()
                val favoriteTvs by viewModel.favoriteTvs.collectAsStateWithLifecycle()

                FavoriteScreen(
                    favoriteMovies = favoriteMovies,
                    favoritePeoples = favoritePeople,
                    favoriteTvs = favoriteTvs,
                    onShowSnackbar = { _, _ -> true },
                    goToTv = {},
                    goToMovie = {},
                    goToPeople = {},
                    updateTabIndex = viewModel::updateTabIndex,
                    deleteFavoriteMovie = viewModel::deleteMovie,
                    deleteFavoritePeople = viewModel::deletePeople,
                    deleteFavoriteTv = viewModel::deleteTv
                )
            }

            runBlocking {
                testDataBaseRepository.insertPeople(people = People(id = 0, title = "people_1", posterPath = "/peopleImage.png"))
            }

            onNodeWithText(text = "인물").assertExists().assertIsDisplayed().performClick()
            onNodeWithTag(testTag = "favoritePeopleEmpty").assertIsNotDisplayed()
            onNodeWithText(text = "찜한 인물이 없습니다.").assertIsNotDisplayed()
            onNodeWithContentDescription(label = "FavoritePeopleProfileImage").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "favorite").assertExists().assertIsDisplayed().performClick()
            onNodeWithContentDescription(label = "FavoritePeopleProfileImage").assertIsNotDisplayed()
            onNodeWithTag(testTag = "favoritePeopleEmpty").assertExists().assertIsDisplayed()
            onNodeWithText(text = "찜한 인물이 없습니다.").assertExists().assertIsDisplayed()
        }
    }
}