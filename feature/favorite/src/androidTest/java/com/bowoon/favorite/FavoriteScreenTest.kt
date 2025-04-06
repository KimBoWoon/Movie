package com.bowoon.favorite

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.bowoon.model.Favorite
import org.junit.Rule
import org.junit.Test

class FavoriteScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun favoriteMovieEmptyTest() {
        composeTestRule.apply {
            setContent {
                FavoriteScreen(
                    favoriteMovies = emptyList(),
                    favoritePeoples = emptyList(),
                    onShowSnackbar = { _, _ -> true},
                    goToMovie = {},
                    goToPeople = {},
                    deleteFavoriteMovie = {},
                    deleteFavoritePeople = {}
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
                FavoriteScreen(
                    favoriteMovies = emptyList(),
                    favoritePeoples = emptyList(),
                    onShowSnackbar = { _, _ -> true},
                    goToMovie = {},
                    goToPeople = {},
                    deleteFavoriteMovie = {},
                    deleteFavoritePeople = {}
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
                FavoriteScreen(
                    favoriteMovies = listOf(Favorite(id = 0, title = "movie_1", imagePath = "/movieImage.png")),
                    favoritePeoples = listOf(Favorite(id = 0, title = "people_1", imagePath = "/peopleImage.png")),
                    onShowSnackbar = { _, _ -> true},
                    goToMovie = {},
                    goToPeople = {},
                    deleteFavoriteMovie = {},
                    deleteFavoritePeople = {}
                )
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
                FavoriteScreen(
                    favoriteMovies = listOf(Favorite(id = 0, title = "movie_1", imagePath = "/movieImage.png")),
                    favoritePeoples = listOf(Favorite(id = 0, title = "people_1", imagePath = "/peopleImage.png")),
                    onShowSnackbar = { _, _ -> true},
                    goToMovie = {},
                    goToPeople = {},
                    deleteFavoriteMovie = {},
                    deleteFavoritePeople = {}
                )
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
                var movieList by remember { mutableStateOf(listOf<Favorite>(Favorite(id = 0, title = "movie_1", imagePath = "/movieImage.png"))) }

                FavoriteScreen(
                    favoriteMovies = movieList,
                    favoritePeoples = listOf(Favorite(id = 0, title = "people_1", imagePath = "/peopleImage.png")),
                    onShowSnackbar = { _, _ -> true},
                    goToMovie = {},
                    goToPeople = {},
                    deleteFavoriteMovie = { movieList = movieList.filter { favorite -> favorite.id != it.id } },
                    deleteFavoritePeople = {}
                )
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
                var peopleList by remember { mutableStateOf(listOf<Favorite>(Favorite(id = 0, title = "people_1", imagePath = "/peopleImage.png"))) }

                FavoriteScreen(
                    favoriteMovies = listOf(Favorite(id = 0, title = "movie_1", imagePath = "/movieImage.png")),
                    favoritePeoples = peopleList,
                    onShowSnackbar = { _, _ -> true},
                    goToMovie = {},
                    goToPeople = {},
                    deleteFavoriteMovie = {},
                    deleteFavoritePeople = { peopleList = peopleList.filter { favorite -> favorite.id != it.id } }
                )
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