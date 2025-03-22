package com.bowoon.search

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.lifecycle.SavedStateHandle
import androidx.paging.compose.collectAsLazyPagingItems
import com.bowoon.testing.model.movieSearchTestData
import com.bowoon.testing.repository.TestPagingRepository
import com.bowoon.testing.repository.TestUserDataRepository
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SearchScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()
    private lateinit var viewModel: SearchVM
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var testPagingRepository: TestPagingRepository
    private lateinit var testUserDataRepository: TestUserDataRepository

    @Before
    fun setup() {
        savedStateHandle = SavedStateHandle()
        testPagingRepository = TestPagingRepository()
        testUserDataRepository = TestUserDataRepository()
        viewModel = SearchVM(
            savedStateHandle = savedStateHandle,
            pagingRepository = testPagingRepository,
            userDataRepository = testUserDataRepository
        )
    }

    @Test
    fun searchScreenInputKeywordHintDisplayTest() {
        composeTestRule.apply {
            setContent {
                SearchScreen(
                    state = viewModel.searchMovieState.collectAsLazyPagingItems(),
                    keyword = viewModel.keyword,
                    searchType = viewModel.searchType,
                    onMovieClick = {},
                    onPeopleClick = {},
                    onSearchClick = viewModel::searchMovies,
                    updateKeyword = viewModel::updateKeyword,
                    updateSearchType = viewModel::updateSearchType
                )
            }

            onNodeWithContentDescription(label = "searchBarIcon").assertExists().assertIsDisplayed()
            onNodeWithText(text = "검색어를 입력하세요.").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "searchKeywordClear").assertDoesNotExist()
            onNodeWithContentDescription(label = "searchMovies").assertDoesNotExist()
        }
    }

    @Test
    fun searchScreenInputKeywordTest() {
        composeTestRule.apply {
            setContent {
                viewModel.updateKeyword("mission")

                SearchScreen(
                    state = viewModel.searchMovieState.collectAsLazyPagingItems(),
                    keyword = viewModel.keyword,
                    searchType = viewModel.searchType,
                    onMovieClick = {},
                    onPeopleClick = {},
                    onSearchClick = viewModel::searchMovies,
                    updateKeyword = viewModel::updateKeyword,
                    updateSearchType = viewModel::updateSearchType
                )
            }

            onNodeWithContentDescription(label = "searchBarIcon").assertExists().assertIsDisplayed()
            onNodeWithText(text = "mission").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "searchKeywordClear").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "searchMovies").assertExists().assertIsDisplayed()
        }
    }

    @Test
    fun searchScreenSearchTest() {
        composeTestRule.apply {
            setContent {
                viewModel.updateKeyword("mission")

                SearchScreen(
                    state = viewModel.searchMovieState.collectAsLazyPagingItems(),
                    keyword = viewModel.keyword,
                    searchType = viewModel.searchType,
                    onMovieClick = {},
                    onPeopleClick = {},
                    onSearchClick = viewModel::searchMovies,
                    updateKeyword = viewModel::updateKeyword,
                    updateSearchType = viewModel::updateSearchType
                )
            }

            onNodeWithContentDescription(label = "searchBarIcon").assertExists().assertIsDisplayed()
            onNodeWithText(text = "mission").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "searchKeywordClear").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "searchMovies").assertExists().assertIsDisplayed().performClick()
            movieSearchTestData.results?.forEach { movie ->
                onNodeWithContentDescription(label = "searchResultList").performScrollToNode(hasContentDescription(value = "${movie.id}_${movie.title}")).assertExists().assertIsDisplayed()
            }
        }
    }
}