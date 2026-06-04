package com.cheeke.surfy.favorite

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.cheeke.surfy.model.Media
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.model.People
import com.cheeke.surfy.model.Tv
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class FavoriteScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun favoriteMovieEmptyTest() {
        composeTestRule.apply {
            setContent {
                val pagingData = PagingData.empty<Media>()
                val flow = MutableStateFlow(value = pagingData)

                FavoriteScreen(
                    selectedTab = FavoriteTab.MOVIE,
                    favoritePagingItems = flow.collectAsLazyPagingItems(),
                    onShowSnackbar = { _, _ -> true },
                    updateTabIndex = { _ -> },
                    goTo = { favoriteTab, media -> },
                    deleteFavorite = { _, _ -> }
                )
            }

            onNodeWithText(text = "영화").assertExists().assertIsDisplayed().performClick()
            onNodeWithContentDescription(label = "favoriteEmpty").assertExists().assertIsDisplayed()
            onNodeWithText(text = "찜한 영화가 없습니다.").assertExists().assertIsDisplayed()
        }
    }

    @Test
    fun favoritePeopleEmptyTest() {
        composeTestRule.apply {
            setContent {
                val pagingData = PagingData.empty<Media>()
                val flow = MutableStateFlow(value = pagingData)

                FavoriteScreen(
                    selectedTab = FavoriteTab.PEOPLE,
                    favoritePagingItems = flow.collectAsLazyPagingItems(),
                    onShowSnackbar = { _, _ -> true },
                    updateTabIndex = { _ -> },
                    goTo = { favoriteTab, media -> },
                    deleteFavorite = { _, _ -> }
                )
            }

            onNodeWithText(text = "인물").assertExists().assertIsDisplayed().performClick()
            onNodeWithContentDescription(label = "favoriteEmpty").assertExists().assertIsDisplayed()
            onNodeWithText(text = "찜한 인물이 없습니다.").assertExists().assertIsDisplayed()
        }
    }

    @Test
    fun favoriteTvEmptyTest() {
        composeTestRule.apply {
            setContent {
                val pagingData = PagingData.empty<Media>()
                val flow = MutableStateFlow(value = pagingData)

                FavoriteScreen(
                    selectedTab = FavoriteTab.TV,
                    favoritePagingItems = flow.collectAsLazyPagingItems(),
                    onShowSnackbar = { _, _ -> true },
                    updateTabIndex = { _ -> },
                    goTo = { favoriteTab, media -> },
                    deleteFavorite = { _, _ -> }
                )
            }

            onNodeWithText(text = "TV").assertExists().assertIsDisplayed().performClick()
            onNodeWithContentDescription(label = "favoriteEmpty").assertExists().assertIsDisplayed()
            onNodeWithText(text = "찜한 TV프로그램이 없습니다.").assertExists().assertIsDisplayed()
        }
    }

    @Test
    fun favoriteMovieTest() {
        composeTestRule.apply {
            setContent {
                val pagingData = PagingData.from(data = listOf(element = Movie(id = 0, title = "movie_1", posterPath = "/posterPath.png")))
                val flow = MutableStateFlow(value = pagingData)

                FavoriteScreen(
                    selectedTab = FavoriteTab.MOVIE,
                    favoritePagingItems = flow.collectAsLazyPagingItems(),
                    onShowSnackbar = { _, _ -> true },
                    updateTabIndex = { _ -> },
                    goTo = { favoriteTab, media -> },
                    deleteFavorite = { _, _ -> }
                )
            }

            onNodeWithText(text = "영화").assertExists().assertIsDisplayed().performClick()
            onNodeWithContentDescription(label = "favoriteEmpty").assertDoesNotExist()
            onNodeWithText(text = "찜한 영화가 없습니다.").assertIsNotDisplayed()
            onNodeWithContentDescription(label = "FavoriteImage").assertExists().assertIsDisplayed()
        }
    }

    @Test
    fun favoritePeopleTest() {
        composeTestRule.apply {
            setContent {
                val pagingData = PagingData.from(data = listOf(element = People(id = 0, title = "movie_1", posterPath = "/posterPath.png")))
                val flow = MutableStateFlow(value = pagingData)

                FavoriteScreen(
                    selectedTab = FavoriteTab.PEOPLE,
                    favoritePagingItems = flow.collectAsLazyPagingItems(),
                    onShowSnackbar = { _, _ -> true },
                    updateTabIndex = { _ -> },
                    goTo = { favoriteTab, media -> },
                    deleteFavorite = { _, _ -> }
                )
            }

            onNodeWithText(text = "인물").assertExists().assertIsDisplayed().performClick()
            onNodeWithContentDescription(label = "favoriteEmpty").assertDoesNotExist()
            onNodeWithText(text = "찜한 인물이 없습니다.").assertIsNotDisplayed()
            onNodeWithContentDescription(label = "FavoriteImage").assertExists().assertIsDisplayed()
        }
    }

    @Test
    fun favoriteTvTest() {
        composeTestRule.apply {
            setContent {
                val pagingData = PagingData.from(data = listOf(element = Tv(id = 0, title = "movie_1", posterPath = "/posterPath.png")))
                val flow = MutableStateFlow(value = pagingData)

                FavoriteScreen(
                    selectedTab = FavoriteTab.TV,
                    favoritePagingItems = flow.collectAsLazyPagingItems(),
                    onShowSnackbar = { _, _ -> true },
                    updateTabIndex = { _ -> },
                    goTo = { favoriteTab, media -> },
                    deleteFavorite = { _, _ -> }
                )
            }

            onNodeWithText(text = "TV").assertExists().assertIsDisplayed().performClick()
            onNodeWithContentDescription(label = "favoriteEmpty").assertDoesNotExist()
            onNodeWithText(text = "찜한 TV프로그램이 없습니다.").assertIsNotDisplayed()
            onNodeWithContentDescription(label = "FavoriteImage").assertExists().assertIsDisplayed()
        }
    }

    @Test
    fun deleteMovieTest() {
        val pagingData = PagingData.from(data = listOf(Movie(id = 3525, title = "movie_1", posterPath = "/movieImage.png")))
        val flow = MutableStateFlow(value = pagingData)
        composeTestRule.apply {
            setContent {
                FavoriteScreen(
                    selectedTab = FavoriteTab.MOVIE,
                    favoritePagingItems = flow.collectAsLazyPagingItems(),
                    onShowSnackbar = { _, _ -> true },
                    updateTabIndex = { _ -> },
                    goTo = { favoriteTab, media -> },
                    deleteFavorite = { _, _ -> flow.value = PagingData.empty() }
                )
            }

            onNodeWithText(text = "영화").assertExists().assertIsDisplayed().performClick()
            onNodeWithContentDescription(label = "favoriteEmpty").assertDoesNotExist()
            onNodeWithText(text = "찜한 영화가 없습니다.").assertIsNotDisplayed()
            onNodeWithContentDescription(label = "FavoriteImage").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "favoriteButton").assertExists().assertIsDisplayed().performClick()
            onNodeWithContentDescription(label = "FavoriteImage").assertIsNotDisplayed()
            onNodeWithContentDescription(label = "favoriteEmpty").assertExists().assertIsDisplayed()
            onNodeWithText(text = "찜한 영화가 없습니다.").assertExists().assertIsDisplayed()
        }
    }

    @Test
    fun deletePeopleTest() {
        val pagingData = PagingData.from(data = listOf(People(id = 3525, title = "people_1", posterPath = "/peopleImage.png")))
        val flow = MutableStateFlow(value = pagingData)
        composeTestRule.apply {
            setContent {
                FavoriteScreen(
                    selectedTab = FavoriteTab.PEOPLE,
                    favoritePagingItems = flow.collectAsLazyPagingItems(),
                    onShowSnackbar = { _, _ -> true },
                    updateTabIndex = { _ -> },
                    goTo = { favoriteTab, media -> },
                    deleteFavorite = { _, _ -> flow.value = PagingData.empty() }
                )
            }

            onNodeWithText(text = "인물").assertExists().assertIsDisplayed().performClick()
            onNodeWithContentDescription(label = "favoriteEmpty").assertDoesNotExist()
            onNodeWithText(text = "찜한 인물이 없습니다.").assertIsNotDisplayed()
            onNodeWithContentDescription(label = "FavoriteImage").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "favoriteButton").assertExists().assertIsDisplayed().performClick()
            onNodeWithContentDescription(label = "FavoriteImage").assertIsNotDisplayed()
            onNodeWithContentDescription(label = "favoriteEmpty").assertExists().assertIsDisplayed()
            onNodeWithText(text = "찜한 인물이 없습니다.").assertExists().assertIsDisplayed()
        }
    }

    @Test
    fun deleteTvTest() {
        val pagingData = PagingData.from(data = listOf(Tv(id = 3525, title = "tv_1", posterPath = "/tvImage.png")))
        val flow = MutableStateFlow(value = pagingData)
        composeTestRule.apply {
            setContent {
                FavoriteScreen(
                    selectedTab = FavoriteTab.TV,
                    favoritePagingItems = flow.collectAsLazyPagingItems(),
                    onShowSnackbar = { _, _ -> true },
                    updateTabIndex = { _ -> },
                    goTo = { favoriteTab, media -> },
                    deleteFavorite = { _, _ -> flow.value = PagingData.empty() }
                )
            }

            onNodeWithText(text = "TV").assertExists().assertIsDisplayed().performClick()
            onNodeWithContentDescription(label = "favoriteEmpty").assertDoesNotExist()
            onNodeWithText(text = "찜한 TV프로그램이 없습니다.").assertIsNotDisplayed()
            onNodeWithContentDescription(label = "FavoriteImage").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "favoriteButton").assertExists().assertIsDisplayed().performClick()
            onNodeWithContentDescription(label = "FavoriteImage").assertIsNotDisplayed()
            onNodeWithContentDescription(label = "favoriteEmpty").assertExists().assertIsDisplayed()
            onNodeWithText(text = "찜한 TV프로그램이 없습니다.").assertExists().assertIsDisplayed()
        }
    }
}