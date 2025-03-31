package com.bowoon.search

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bowoon.model.InternalData
import com.bowoon.testing.model.movieSearchTestData
import com.bowoon.testing.repository.TestPagingRepository
import com.bowoon.testing.repository.TestUserDataRepository
import kotlinx.coroutines.runBlocking
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
        runBlocking {
            testUserDataRepository.updateUserData(InternalData(), false)
        }
    }

    @Test
    fun searchScreenInputKeywordHintDisplayTest() {
        composeTestRule.apply {
            setContent {
                val searchState by viewModel.searchResult.collectAsStateWithLifecycle()

                SearchScreen(
                    searchState = searchState,
                    keyword = viewModel.searchQuery,
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
                val searchState by viewModel.searchResult.collectAsStateWithLifecycle()
                viewModel.updateKeyword("mission")

                SearchScreen(
                    searchState = searchState,
                    keyword = viewModel.searchQuery,
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
                val searchState by viewModel.searchResult.collectAsStateWithLifecycle()
                viewModel.updateKeyword("mission")

                SearchScreen(
                    searchState = searchState,
                    keyword = viewModel.searchQuery,
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

    @Test
    fun searchScreenErrorTest() {
        composeTestRule.apply {
            setContent {
                val searchState by viewModel.searchResult.collectAsStateWithLifecycle()
                viewModel.updateKeyword(" ")

                SearchScreen(
                    searchState = searchState,
                    keyword = viewModel.searchQuery,
                    searchType = viewModel.searchType,
                    onMovieClick = {},
                    onPeopleClick = {},
                    onSearchClick = viewModel::searchMovies,
                    updateKeyword = viewModel::updateKeyword,
                    updateSearchType = viewModel::updateSearchType
                )
            }

            onNodeWithContentDescription(label = "searchBarIcon").assertExists().assertIsDisplayed()
            onNodeWithText(text = " ").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "searchKeywordClear").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "searchMovies").assertExists().assertIsDisplayed().performClick()
            onNodeWithText(text = "검색어를 입력하세요.").assertExists().assertIsDisplayed()
        }
    }
}