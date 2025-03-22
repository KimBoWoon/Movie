package com.bowoon.detail

import androidx.activity.ComponentActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.compose.collectAsLazyPagingItems
import com.bowoon.data.paging.TMDBSimilarMoviePagingSource
import com.bowoon.data.repository.LocalMovieAppDataComposition
import com.bowoon.model.Favorite
import com.bowoon.model.Movie
import com.bowoon.testing.TestMovieDataSource
import com.bowoon.testing.model.similarMoviesTestData
import com.bowoon.testing.repository.TestDatabaseRepository
import com.bowoon.testing.repository.favoriteMovieDetailTestData
import com.bowoon.testing.repository.unFavoriteMovieDetailTestData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class DetailScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()
    private val source = TMDBSimilarMoviePagingSource(
        apis = TestMovieDataSource(),
        id = 0,
        language = "ko",
        region = "KR"
    )
    private val pager = Pager(
        config = PagingConfig(pageSize = 20, initialLoadSize = 20, prefetchDistance = 5),
        pagingSourceFactory = { source }
    ).flow

    @Test
    fun detailLoadingTest() = runTest {
        composeTestRule.apply {
            setContent {
                DetailScreen(
                    movieInfoState = MovieDetailState.Loading,
                    similarMovieState = pager.collectAsLazyPagingItems(),
                    goToMovie = {},
                    goToPeople = {},
                    onBack = {},
                    onShowSnackbar = { _, _ -> true },
                    insertFavoriteMovie = {},
                    deleteFavoriteMovie = {},
                    restart = {}
                )
            }

            onNodeWithTag(testTag = "detailScreenLoading").assertExists().assertIsDisplayed()
        }
    }

    @Test
    fun detailErrorTest() = runTest {
        composeTestRule.apply {
            setContent {
                DetailScreen(
                    movieInfoState = MovieDetailState.Error(throwable = Throwable("something wrong...")),
                    similarMovieState = pager.collectAsLazyPagingItems(),
                    goToMovie = {},
                    goToPeople = {},
                    onBack = {},
                    onShowSnackbar = { _, _ -> true },
                    insertFavoriteMovie = {},
                    deleteFavoriteMovie = {},
                    restart = {}
                )
            }

            onNodeWithText(text = "something wrong...").assertExists().assertIsDisplayed()
        }
    }

    @Test
    fun detailSuccessTest() = runTest {
        composeTestRule.apply {
            setContent {
                DetailScreen(
                    movieInfoState = MovieDetailState.Success(movieDetail = favoriteMovieDetailTestData),
                    similarMovieState = pager.collectAsLazyPagingItems(),
                    goToMovie = {},
                    goToPeople = {},
                    onBack = {},
                    onShowSnackbar = { _, _ -> true },
                    insertFavoriteMovie = {},
                    deleteFavoriteMovie = {},
                    restart = {}
                )
            }

            onNodeWithContentDescription(label = "favorite").assertExists().assertIsDisplayed()
            onNodeWithTag(testTag = "titleComponent").assertTextEquals(favoriteMovieDetailTestData.title ?: "").assertIsDisplayed()
            onNodeWithTag(testTag = "movieTitle").assertTextEquals(favoriteMovieDetailTestData.title ?: "").assertIsDisplayed()
            onNodeWithText(text = favoriteMovieDetailTestData.originalTitle ?: "").assertExists().assertIsDisplayed()
            onNodeWithText(text = favoriteMovieDetailTestData.overview ?: "").assertExists().assertIsDisplayed()
        }
    }

    @Test
    fun castAndCrewTest() = runTest {
        composeTestRule.apply {
            setContent {
                DetailScreen(
                    movieInfoState = MovieDetailState.Success(movieDetail = favoriteMovieDetailTestData),
                    similarMovieState = pager.collectAsLazyPagingItems(),
                    goToMovie = {},
                    goToPeople = {},
                    onBack = {},
                    onShowSnackbar = { _, _ -> true },
                    insertFavoriteMovie = {},
                    deleteFavoriteMovie = {},
                    restart = {}
                )
            }

            onNodeWithText(text = "배우 / 감독").assertIsDisplayed()
            onNodeWithText(text = "배우 / 감독").performClick()
            onNodeWithText(text = favoriteMovieDetailTestData.credits?.cast?.get(0)?.name ?: "").assertIsDisplayed()
            onNodeWithTag(testTag = "castAndCrew").performScrollToNode(hasText(text = favoriteMovieDetailTestData.credits?.crew?.get(0)?.name ?: "")).assertIsDisplayed()
        }
    }

    @Test
    fun movieImagesTest() = runTest {
        composeTestRule.apply {
            var posterUrl = ""

            setContent {
                posterUrl = LocalMovieAppDataComposition.current.getImageUrl()

                DetailScreen(
                    movieInfoState = MovieDetailState.Success(movieDetail = favoriteMovieDetailTestData),
                    similarMovieState = pager.collectAsLazyPagingItems(),
                    goToMovie = {},
                    goToPeople = {},
                    onBack = {},
                    onShowSnackbar = { _, _ -> true },
                    insertFavoriteMovie = {},
                    deleteFavoriteMovie = {},
                    restart = {}
                )
            }

            onNodeWithText(text = "이미지").assertIsDisplayed()
            onNodeWithText(text = "이미지").performClick()
            assertEquals(
                favoriteMovieDetailTestData.images?.posters?.plus(favoriteMovieDetailTestData.images?.backdrops)?.size,
                4
            )
            ((favoriteMovieDetailTestData.images?.backdrops ?: emptyList()) + (favoriteMovieDetailTestData.images?.posters ?: emptyList())).forEach {
                it.filePath?.let { filePath ->
                    onNodeWithTag(testTag = "$posterUrl$filePath").assertExists().assertIsDisplayed()
                }
            }
        }
    }

    @Test
    fun similarMovieTest() = runTest {
        composeTestRule.apply {
            setContent {
                DetailScreen(
                    movieInfoState = MovieDetailState.Success(movieDetail = favoriteMovieDetailTestData),
                    similarMovieState = pager.collectAsLazyPagingItems(),
                    goToMovie = {},
                    goToPeople = {},
                    onBack = {},
                    onShowSnackbar = { _, _ -> true },
                    insertFavoriteMovie = {},
                    deleteFavoriteMovie = {},
                    restart = {}
                )
            }

            onNodeWithText(text = "다른 영화").assertIsDisplayed()
            onNodeWithText(text = "다른 영화").performClick()
            assertEquals(
                expected = PagingSource.LoadResult.Page<Int, Movie>(
                    data = similarMoviesTestData.results?.map {
                        Movie(
                            id = it.id,
                            title = it.title,
                            posterPath = it.posterPath
                        )
                    } ?: emptyList(),
                    prevKey = null,
                    nextKey = null
                ),
                actual = source.load(
                    PagingSource.LoadParams.Refresh(
                        key = null,
                        loadSize = 2,
                        placeholdersEnabled = false
                    )
                ),
            )
        }
    }

    @Test
    fun addFavoriteTest() = runTest {
        composeTestRule.apply {
            val testDatabaseRepository = TestDatabaseRepository()

            setContent {
                var movie by remember { mutableStateOf(unFavoriteMovieDetailTestData) }

                DetailScreen(
                    movieInfoState = MovieDetailState.Success(movie),
                    similarMovieState = pager.collectAsLazyPagingItems(),
                    goToMovie = {},
                    goToPeople = {},
                    onBack = {},
                    onShowSnackbar = { _, _ -> true },
                    insertFavoriteMovie = {
                        backgroundScope.launch(UnconfinedTestDispatcher()) { testDatabaseRepository.insertMovie(it) }
                        movie = movie.copy(isFavorite = true)
                    },
                    deleteFavoriteMovie = {
                        backgroundScope.launch(UnconfinedTestDispatcher()) { testDatabaseRepository.deleteMovie(it) }
                        movie = movie.copy(isFavorite = false)
                    },
                    restart = {}
                )
            }

            onNodeWithContentDescription(label = "unFavorite").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "favorite").assertIsNotDisplayed()
            onNodeWithTag(testTag = "titleComponent").assertTextEquals(favoriteMovieDetailTestData.title ?: "").assertIsDisplayed()
            onNodeWithTag(testTag = "movieTitle").assertTextEquals(favoriteMovieDetailTestData.title ?: "").assertIsDisplayed()
            onNodeWithText(text = favoriteMovieDetailTestData.originalTitle ?: "").assertExists().assertIsDisplayed()
            onNodeWithText(text = favoriteMovieDetailTestData.overview ?: "").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "unFavorite").performClick()
            onNodeWithContentDescription(label = "unFavorite").assertIsNotDisplayed()
            onNodeWithContentDescription(label = "favorite").assertIsDisplayed()
            assertEquals(
                testDatabaseRepository.getMovies().first().find { it.id == 324 }?.id,
                unFavoriteMovieDetailTestData.id
            )
        }
    }

    @Test
    fun detailFavoriteTest() = runTest {
        composeTestRule.apply {
            val testDatabaseRepository = TestDatabaseRepository()

            setContent {
                var movie by remember { mutableStateOf(favoriteMovieDetailTestData) }
                val pager = Pager(
                    config = PagingConfig(pageSize = 20, initialLoadSize = 20, prefetchDistance = 5),
                    pagingSourceFactory = {
                        TMDBSimilarMoviePagingSource(
                            apis = TestMovieDataSource(),
                            id = 0,
                            language = "ko",
                            region = "KR"
                        )
                    }
                ).flow

                LaunchedEffect("insertMovie") {
                    backgroundScope.launch(UnconfinedTestDispatcher()) {
                        testDatabaseRepository.insertMovie(
                            Favorite(
                                id = favoriteMovieDetailTestData.id,
                                title = favoriteMovieDetailTestData.title,
                                imagePath = favoriteMovieDetailTestData.posterPath
                            )
                        )
                    }
                }

                DetailScreen(
                    movieInfoState = MovieDetailState.Success(movie),
                    similarMovieState = pager.collectAsLazyPagingItems(),
                    goToMovie = {},
                    goToPeople = {},
                    onBack = {},
                    onShowSnackbar = { _, _ -> true },
                    insertFavoriteMovie = {
                        backgroundScope.launch(UnconfinedTestDispatcher()) { testDatabaseRepository.insertMovie(it) }
                        movie = movie.copy(isFavorite = true)
                    },
                    deleteFavoriteMovie = {
                        backgroundScope.launch(UnconfinedTestDispatcher()) { testDatabaseRepository.deleteMovie(it) }
                        movie = movie.copy(isFavorite = false)
                    },
                    restart = {}
                )
            }

            onNodeWithContentDescription(label = "favorite").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "unFavorite").assertIsNotDisplayed()
            onNodeWithTag(testTag = "titleComponent").assertTextEquals(favoriteMovieDetailTestData.title ?: "").assertIsDisplayed()
            onNodeWithTag(testTag = "movieTitle").assertTextEquals(favoriteMovieDetailTestData.title ?: "").assertIsDisplayed()
            onNodeWithText(text = favoriteMovieDetailTestData.originalTitle ?: "").assertExists().assertIsDisplayed()
            onNodeWithText(text = favoriteMovieDetailTestData.overview ?: "").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "favorite").performClick()
            onNodeWithContentDescription(label = "unFavorite").assertIsDisplayed()
            onNodeWithContentDescription(label = "favorite").assertIsNotDisplayed()
            assertEquals(
                testDatabaseRepository.getMovies().first().find { it.id == 0 },
                null
            )
        }
    }
}