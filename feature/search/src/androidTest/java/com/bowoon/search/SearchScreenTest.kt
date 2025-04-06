package com.bowoon.search

import androidx.activity.ComponentActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bowoon.data.repository.LocalMovieAppDataComposition
import com.bowoon.model.Genre
import com.bowoon.model.InternalData
import com.bowoon.model.Movie
import com.bowoon.model.MovieAppData
import com.bowoon.model.SearchType
import com.bowoon.testing.model.movieSearchTestData
import com.bowoon.testing.model.peopleSearchTestData
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
    private val genres = listOf(Genre(id = 0, name = "name1"), Genre(id = 1, name = "name2"), Genre(id = 2, name = "Action"), Genre(id = 3, name = "name3"), Genre(id = 4, name = "name4"), Genre(id = 5, name = "name5"))

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
                val searchType by viewModel.searchType.collectAsStateWithLifecycle()
                val selectedGenre by viewModel.selectedGenre.collectAsStateWithLifecycle()

                CompositionLocalProvider(
                    LocalMovieAppDataComposition provides MovieAppData(genres = genres)
                ) {
                    SearchScreen(
                        searchState = searchState,
                        keyword = viewModel.searchQuery,
                        searchType = searchType,
                        selectedGenre = selectedGenre,
                        goToMovie = {},
                        goToPeople = {},
                        onSearchClick = viewModel::searchMovies,
                        updateKeyword = viewModel::updateKeyword,
                        updateSearchType = viewModel::updateSearchType,
                        updateGenre = viewModel::updateGenre
                    )
                }
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
                val searchType by viewModel.searchType.collectAsStateWithLifecycle()
                val selectedGenre by viewModel.selectedGenre.collectAsStateWithLifecycle()

                viewModel.updateKeyword("mission")

                CompositionLocalProvider(
                    LocalMovieAppDataComposition provides MovieAppData(genres = genres)
                ) {
                    SearchScreen(
                        searchState = searchState,
                        keyword = viewModel.searchQuery,
                        searchType = searchType,
                        selectedGenre = selectedGenre,
                        goToMovie = {},
                        goToPeople = {},
                        onSearchClick = viewModel::searchMovies,
                        updateKeyword = viewModel::updateKeyword,
                        updateSearchType = viewModel::updateSearchType,
                        updateGenre = viewModel::updateGenre
                    )
                }
            }

            onNodeWithContentDescription(label = "searchBarIcon").assertExists().assertIsDisplayed()
            onNodeWithText(text = "mission").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "searchKeywordClear").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "searchMovies").assertExists().assertIsDisplayed()
        }
    }

    @Test
    fun searchScreenMovieSearchTest() {
        composeTestRule.apply {
            setContent {
                val searchState by viewModel.searchResult.collectAsStateWithLifecycle()
                val searchType by viewModel.searchType.collectAsStateWithLifecycle()
                val selectedGenre by viewModel.selectedGenre.collectAsStateWithLifecycle()

                viewModel.updateKeyword("mission")

                CompositionLocalProvider(
                    LocalMovieAppDataComposition provides MovieAppData(genres = genres)
                ) {
                    SearchScreen(
                        searchState = searchState,
                        keyword = viewModel.searchQuery,
                        searchType = searchType,
                        selectedGenre = selectedGenre,
                        goToMovie = {},
                        goToPeople = {},
                        onSearchClick = viewModel::searchMovies,
                        updateKeyword = viewModel::updateKeyword,
                        updateSearchType = viewModel::updateSearchType,
                        updateGenre = viewModel::updateGenre
                    )
                }
            }

            onNodeWithContentDescription(label = "searchBarIcon").assertExists().assertIsDisplayed()
            onNodeWithText(text = "mission").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "searchKeywordClear").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "searchMovies").assertExists().assertIsDisplayed().performClick()
            movieSearchTestData.results?.forEach { movie ->
                onNodeWithContentDescription(label = "searchResultList").performScrollToNode(hasContentDescription(value = "${movie.id}_${movie.name}")).assertExists().assertIsDisplayed()
            }
        }
    }

    @Test
    fun searchScreenPeopleSearchTest() {
        composeTestRule.apply {
            setContent {
                val searchState by viewModel.searchResult.collectAsStateWithLifecycle()
                val searchType by viewModel.searchType.collectAsStateWithLifecycle()
                val selectedGenre by viewModel.selectedGenre.collectAsStateWithLifecycle()

                viewModel.updateKeyword("name")

                CompositionLocalProvider(
                    LocalMovieAppDataComposition provides MovieAppData(genres = genres)
                ) {
                    SearchScreen(
                        searchState = searchState,
                        keyword = viewModel.searchQuery,
                        searchType = searchType,
                        selectedGenre = selectedGenre,
                        goToMovie = {},
                        goToPeople = {},
                        onSearchClick = viewModel::searchMovies,
                        updateKeyword = viewModel::updateKeyword,
                        updateSearchType = viewModel::updateSearchType,
                        updateGenre = viewModel::updateGenre
                    )
                }
            }

            onNodeWithContentDescription(label = "searchBarIcon").assertExists().assertIsDisplayed()
            onNodeWithTag(testTag = "searchType").assertExists().assertIsDisplayed().performClick()
            onNodeWithText(text = SearchType.PEOPLE.label).assertExists().assertIsDisplayed().performClick()
            onNodeWithText(text = "name").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "searchKeywordClear").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "searchMovies").assertExists().assertIsDisplayed().performClick()
            peopleSearchTestData.results?.forEach { movie ->
                onNodeWithContentDescription(label = "searchResultList").performScrollToNode(hasContentDescription(value = "${movie.id}_${movie.name}")).assertExists().assertIsDisplayed()
            }
        }
    }

    @Test
    fun searchScreenErrorTest() {
        composeTestRule.apply {
            setContent {
                val searchState by viewModel.searchResult.collectAsStateWithLifecycle()
                val searchType by viewModel.searchType.collectAsStateWithLifecycle()
                val selectedGenre by viewModel.selectedGenre.collectAsStateWithLifecycle()

                viewModel.updateKeyword(" ")

                CompositionLocalProvider(
                    LocalMovieAppDataComposition provides MovieAppData(genres = genres)
                ) {
                    SearchScreen(
                        searchState = searchState,
                        keyword = viewModel.searchQuery,
                        searchType = searchType,
                        selectedGenre = selectedGenre,
                        goToMovie = {},
                        goToPeople = {},
                        onSearchClick = viewModel::searchMovies,
                        updateKeyword = viewModel::updateKeyword,
                        updateSearchType = viewModel::updateSearchType,
                        updateGenre = viewModel::updateGenre
                    )
                }
            }

            onNodeWithContentDescription(label = "searchBarIcon").assertExists().assertIsDisplayed()
            onNodeWithText(text = " ").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "searchKeywordClear").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "searchMovies").assertExists().assertIsDisplayed().performClick()
            onNodeWithText(text = "검색어를 입력하세요.").assertExists().assertIsDisplayed()
        }
    }

    @Test
    fun searchScreenFilterTest() {
        composeTestRule.apply {
            setContent {
                val searchState by viewModel.searchResult.collectAsStateWithLifecycle()
                val searchType by viewModel.searchType.collectAsStateWithLifecycle()
                val selectedGenre by viewModel.selectedGenre.collectAsStateWithLifecycle()

                viewModel.updateKeyword("mission")

                CompositionLocalProvider(
                    LocalMovieAppDataComposition provides MovieAppData(genres = genres)
                ) {
                    SearchScreen(
                        searchState = searchState,
                        keyword = viewModel.searchQuery,
                        searchType = searchType,
                        selectedGenre = selectedGenre,
                        goToMovie = {},
                        goToPeople = {},
                        onSearchClick = viewModel::searchMovies,
                        updateKeyword = viewModel::updateKeyword,
                        updateSearchType = viewModel::updateSearchType,
                        updateGenre = viewModel::updateGenre
                    )
                }
            }

            onNodeWithContentDescription(label = "searchBarIcon").assertExists().assertIsDisplayed()
            onNodeWithText(text = "mission").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "searchKeywordClear").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "searchMovies").assertExists().assertIsDisplayed().performClick()
            genres.forEach {
                onNodeWithTag(testTag = "FilterRow").performScrollToNode(hasContentDescription(value = "${it.name}")).assertExists().assertIsDisplayed()
            }
            viewModel.updateGenre(Genre(id = 2, name = "Action"))
            onNodeWithContentDescription(label = "Action").assertExists().assertIsDisplayed().performClick()
            movieSearchTestData.results?.filter { it is Movie && 2 in (it.genreIds ?: emptyList()) }?.forEach { movie ->
                onNodeWithContentDescription(label = "searchResultList").performScrollToNode(hasContentDescription(value = "${movie.id}_${movie.name}")).assertExists().assertIsDisplayed()
            }
        }
    }
}