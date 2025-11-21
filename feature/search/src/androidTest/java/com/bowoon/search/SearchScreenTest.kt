package com.bowoon.search

import androidx.activity.ComponentActivity
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
import androidx.paging.PagingData
import com.bowoon.model.Genre
import com.bowoon.model.InternalData
import com.bowoon.model.MovieAppData
import com.bowoon.model.SearchType
import com.bowoon.testing.model.genreListTestData
import com.bowoon.testing.model.testRecommendedKeyword
import com.bowoon.testing.repository.TestPagingRepository
import com.bowoon.testing.repository.TestUserDataRepository
import kotlinx.coroutines.flow.flowOf
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
    private lateinit var movieAppDataRepository: TestMovieAppDataRepository
    private val genres = genreListTestData.genres ?: emptyList()

    @Before
    fun setup() {
        savedStateHandle = SavedStateHandle()
        testPagingRepository = TestPagingRepository()
        testUserDataRepository = TestUserDataRepository()
        movieAppDataRepository = TestMovieAppDataRepository()
        movieAppDataRepository.setMovieAppData(movieAppData = MovieAppData(genres = genres))
        viewModel = SearchVM(
            savedStateHandle = savedStateHandle,
            movieAppData = movieAppDataRepository,
            pagingRepository = testPagingRepository,
            userDataRepository = testUserDataRepository
        )
        runBlocking {
            testUserDataRepository.updateUserData(userData = InternalData(), isSync = false)
        }
    }

    @Test
    fun searchScreenInputKeywordHintDisplayTest() {
        composeTestRule.apply {
            setContent {
                val searchState by viewModel.searchResult.collectAsStateWithLifecycle()
                val searchType by viewModel.searchType.collectAsStateWithLifecycle()
                val selectedGenre by viewModel.selectedGenre.collectAsStateWithLifecycle()
                val movieAppData by movieAppDataRepository.movieAppData.collectAsStateWithLifecycle()

                SearchScreen(
                    searchUiState = searchState,
                    recommendKeyword = RecommendKeywordUiState.Loading,
                    keyword = viewModel.searchQuery,
                    searchType = searchType,
                    movieAppData = movieAppData,
                    selectedGenre = selectedGenre,
                    goToMovie = {},
                    goToPeople = {},
                    goToSeries = {},
                    onSearchClick = viewModel::searchMovies,
                    updateKeyword = viewModel::updateKeyword,
                    updateSearchType = viewModel::updateSearchType,
                    updateGenre = viewModel::updateGenre
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
                val searchType by viewModel.searchType.collectAsStateWithLifecycle()
                val selectedGenre by viewModel.selectedGenre.collectAsStateWithLifecycle()

                viewModel.updateKeyword("mission")

                val movieAppData by movieAppDataRepository.movieAppData.collectAsStateWithLifecycle()

                SearchScreen(
                    searchUiState = searchState,
                    recommendKeyword = RecommendKeywordUiState.Loading,
                    keyword = viewModel.searchQuery,
                    searchType = searchType,
                    movieAppData = movieAppData,
                    selectedGenre = selectedGenre,
                    goToMovie = {},
                    goToPeople = {},
                    goToSeries = {},
                    onSearchClick = viewModel::searchMovies,
                    updateKeyword = viewModel::updateKeyword,
                    updateSearchType = viewModel::updateSearchType,
                    updateGenre = viewModel::updateGenre
                )
            }

            onNodeWithContentDescription(label = "searchBarIcon").assertExists().assertIsDisplayed()
            onNodeWithText(text = "mission").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "searchKeywordClear").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "searchMovies").assertExists().assertIsDisplayed()
        }
    }

    @Test
    fun recommendKeywordTest() {
        composeTestRule.apply {
            setContent {
                RecommendKeywordComponent(
                    recommendKeyword = RecommendKeywordUiState.Success(pagingData = flowOf(PagingData.from(data = testRecommendedKeyword))),
                    keyword = viewModel.searchQuery,
                    updateKeyword = viewModel::updateKeyword,
                    onSearchClick = viewModel::searchMovies,
                    recommendKeywordVisible = {}
                )
            }

            onNodeWithText(text = "추천 검색어").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "recommendedKeywordClose").assertExists().assertIsDisplayed()
            (0 ..5).forEach {
                onNodeWithContentDescription(label = "recommendKeywordList").performScrollToNode(matcher = hasContentDescription(value = "mission$it")).assertExists().assertIsDisplayed()
            }

        }
    }

    @Test
    fun searchScreenMovieSearchTest() {
        composeTestRule.apply {
            setContent {
                val searchState by viewModel.searchResult.collectAsStateWithLifecycle()
                val searchType by viewModel.searchType.collectAsStateWithLifecycle()
                val selectedGenre by viewModel.selectedGenre.collectAsStateWithLifecycle()
                val movieAppData by movieAppDataRepository.movieAppData.collectAsStateWithLifecycle()

                viewModel.updateKeyword("mission")

                SearchScreen(
                    searchUiState = searchState,
                    recommendKeyword = RecommendKeywordUiState.Loading,
                    keyword = viewModel.searchQuery,
                    searchType = searchType,
                    movieAppData = movieAppData,
                    selectedGenre = selectedGenre,
                    goToMovie = {},
                    goToPeople = {},
                    goToSeries = {},
                    onSearchClick = viewModel::searchMovies,
                    updateKeyword = viewModel::updateKeyword,
                    updateSearchType = viewModel::updateSearchType,
                    updateGenre = viewModel::updateGenre
                )
            }

            onNodeWithContentDescription(label = "searchBarIcon").assertExists().assertIsDisplayed()
            onNodeWithText(text = "mission").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "searchKeywordClear").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "searchMovies").assertExists().assertIsDisplayed().performClick()
            (1..100).forEach {
                onNodeWithContentDescription(label = "searchResultList").performScrollToNode(hasContentDescription(value = "${it}_title_$it")).assertExists().assertIsDisplayed()
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

                val movieAppData by movieAppDataRepository.movieAppData.collectAsStateWithLifecycle()

                SearchScreen(
                    searchUiState = searchState,
                    recommendKeyword = RecommendKeywordUiState.Loading,
                    keyword = viewModel.searchQuery,
                    searchType = searchType,
                    movieAppData = movieAppData,
                    selectedGenre = selectedGenre,
                    goToMovie = {},
                    goToPeople = {},
                    goToSeries = {},
                    onSearchClick = viewModel::searchMovies,
                    updateKeyword = viewModel::updateKeyword,
                    updateSearchType = viewModel::updateSearchType,
                    updateGenre = viewModel::updateGenre
                )
            }

            onNodeWithContentDescription(label = "searchBarIcon").assertExists().assertIsDisplayed()
            onNodeWithTag(testTag = "searchType").assertExists().assertIsDisplayed().performClick()
            onNodeWithText(text = SearchType.PEOPLE.label).assertExists().assertIsDisplayed().performClick()
            onNodeWithText(text = "name").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "searchKeywordClear").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "searchMovies").assertExists().assertIsDisplayed().performClick()
            (1..100).forEach {
                onNodeWithContentDescription(label = "searchResultList").performScrollToNode(hasContentDescription(value = "${it}_title_$it")).assertExists().assertIsDisplayed()
            }
        }
    }

//    @Test
//    fun searchScreenSnackbarTest() = runTest {
//        composeTestRule.apply {
//            backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.showSnackbar.collect { println(it) } }
//            setContent {
//                val searchState by viewModel.searchResult.collectAsStateWithLifecycle()
//                val searchType by viewModel.searchType.collectAsStateWithLifecycle()
//                val selectedGenre by viewModel.selectedGenre.collectAsStateWithLifecycle()
//                val movieAppData by movieAppDataRepository.movieAppData.collectAsStateWithLifecycle()
//
//                viewModel.updateKeyword(keyword = " ")
//                viewModel.showSnackbar.tryEmit(value = Unit)
//
//                SearchScreen(
//                    searchUiState = searchState,
//                    recommendKeyword = RecommendKeywordUiState.Loading,
//                    keyword = viewModel.searchQuery,
//                    searchType = searchType,
//                    movieAppData = movieAppData,
//                    selectedGenre = selectedGenre,
//                    goToMovie = {},
//                    goToPeople = {},
//                    goToSeries = {},
//                    onSearchClick = viewModel::searchMovies,
//                    updateKeyword = viewModel::updateKeyword,
//                    updateSearchType = viewModel::updateSearchType,
//                    updateGenre = viewModel::updateGenre
//                )
//            }
//
//            onNodeWithContentDescription(label = "searchBarIcon").assertExists().assertIsDisplayed()
//            onNodeWithText(text = " ").assertExists().assertIsDisplayed()
//            onNodeWithContentDescription(label = "searchKeywordClear").assertExists().assertIsDisplayed()
//            onNodeWithContentDescription(label = "searchMovies").assertExists().assertIsDisplayed().performClick()
//            onNodeWithText(text = "검색어를 입력하세요.").assertExists().assertIsDisplayed()
//        }
//    }

    @Test
    fun searchScreenFilterTest() {
        composeTestRule.apply {
            setContent {
                val searchState by viewModel.searchResult.collectAsStateWithLifecycle()
                val searchType by viewModel.searchType.collectAsStateWithLifecycle()
                val selectedGenre by viewModel.selectedGenre.collectAsStateWithLifecycle()
                val movieAppData by movieAppDataRepository.movieAppData.collectAsStateWithLifecycle()

                viewModel.updateKeyword("mission")

                SearchScreen(
                    searchUiState = searchState,
                    recommendKeyword = RecommendKeywordUiState.Loading,
                    keyword = viewModel.searchQuery,
                    searchType = searchType,
                    movieAppData = movieAppData,
                    selectedGenre = selectedGenre,
                    goToMovie = {},
                    goToPeople = {},
                    goToSeries = {},
                    onSearchClick = viewModel::searchMovies,
                    updateKeyword = viewModel::updateKeyword,
                    updateSearchType = viewModel::updateSearchType,
                    updateGenre = viewModel::updateGenre
                )
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
            (1..100).forEach {
                onNodeWithContentDescription(label = "searchResultList").performScrollToNode(hasContentDescription(value = "${it}_title_$it")).assertExists().assertIsDisplayed()
            }
        }
    }
}