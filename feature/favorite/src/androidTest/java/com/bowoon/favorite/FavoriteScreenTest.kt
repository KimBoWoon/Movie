package com.bowoon.favorite

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
import com.bowoon.model.Movie
import com.bowoon.model.People
import com.bowoon.testing.repository.TestDatabaseRepository
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
            databaseRepository = testDataBaseRepository
        )
    }

    @Test
    fun favoriteMovieEmptyTest() {
        composeTestRule.apply {
            setContent {
                val favoriteMovies by viewModel.favoriteMovies.collectAsStateWithLifecycle()
                val favoritePeople by viewModel.favoritePeoples.collectAsStateWithLifecycle()

                FavoriteScreen(
                    favoriteMovies = favoriteMovies,
                    favoritePeoples = favoritePeople,
                    onShowSnackbar = { _, _ -> true},
                    goToMovie = {},
                    goToPeople = {},
                    deleteFavoriteMovie = viewModel::deleteMovie,
                    deleteFavoritePeople = viewModel::deletePeople
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

                FavoriteScreen(
                    favoriteMovies = favoriteMovies,
                    favoritePeoples = favoritePeople,
                    onShowSnackbar = { _, _ -> true},
                    goToMovie = {},
                    goToPeople = {},
                    deleteFavoriteMovie = viewModel::deleteMovie,
                    deleteFavoritePeople = viewModel::deletePeople
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

                FavoriteScreen(
                    favoriteMovies = favoriteMovies,
                    favoritePeoples = favoritePeople,
                    onShowSnackbar = { _, _ -> true},
                    goToMovie = {},
                    goToPeople = {},
                    deleteFavoriteMovie = viewModel::deleteMovie,
                    deleteFavoritePeople = viewModel::deletePeople
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

                FavoriteScreen(
                    favoriteMovies = favoriteMovies,
                    favoritePeoples = favoritePeople,
                    onShowSnackbar = { _, _ -> true},
                    goToMovie = {},
                    goToPeople = {},
                    deleteFavoriteMovie = viewModel::deleteMovie,
                    deleteFavoritePeople = viewModel::deletePeople
                )
            }

            runBlocking {
                testDataBaseRepository.insertPeople(people = People(id = 0, name = "people_1", profilePath = "/peopleImage.png"))
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

                FavoriteScreen(
                    favoriteMovies = favoriteMovies,
                    favoritePeoples = favoritePeople,
                    onShowSnackbar = { _, _ -> true},
                    goToMovie = {},
                    goToPeople = {},
                    deleteFavoriteMovie = viewModel::deleteMovie,
                    deleteFavoritePeople = viewModel::deletePeople
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

                FavoriteScreen(
                    favoriteMovies = favoriteMovies,
                    favoritePeoples = favoritePeople,
                    onShowSnackbar = { _, _ -> true},
                    goToMovie = {},
                    goToPeople = {},
                    deleteFavoriteMovie = viewModel::deleteMovie,
                    deleteFavoritePeople = viewModel::deletePeople
                )
            }

            runBlocking {
                testDataBaseRepository.insertPeople(people = People(id = 0, name = "people_1", profilePath = "/peopleImage.png"))
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