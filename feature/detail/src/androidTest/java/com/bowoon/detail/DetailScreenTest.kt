package com.bowoon.detail

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingSource
import androidx.paging.compose.collectAsLazyPagingItems
import com.bowoon.data.paging.SimilarMoviePagingSource
import com.bowoon.domain.GetMovieDetailUseCase
import com.bowoon.model.Genre
import com.bowoon.model.Movie
import com.bowoon.model.MovieAppData
import com.bowoon.model.PosterSize
import com.bowoon.testing.TestMovieDataSource
import com.bowoon.testing.model.configurationTestData
import com.bowoon.testing.model.favoriteMovieDetailTestData
import com.bowoon.testing.model.genreListTestData
import com.bowoon.testing.model.languageListTestData
import com.bowoon.testing.model.movieSeriesTestData
import com.bowoon.testing.model.regionTestData
import com.bowoon.testing.model.similarMoviesTestData
import com.bowoon.testing.model.unFavoriteMovieDetailTestData
import com.bowoon.testing.repository.TestDatabaseRepository
import com.bowoon.testing.repository.TestDetailRepository
import com.bowoon.testing.repository.TestPagingRepository
import com.bowoon.testing.repository.TestUserDataRepository
import com.bowoon.testing.utils.TestMovieAppDataManager
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class DetailScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()
    private lateinit var viewModel: DetailVM
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var testUserDataRepository: TestUserDataRepository
    private lateinit var movieDetailUseCase: GetMovieDetailUseCase
    private lateinit var testDatabaseRepository: TestDatabaseRepository
    private lateinit var testDetailRepository: TestDetailRepository
    private lateinit var testPagingRepository: TestPagingRepository
    private lateinit var testMovieAppDataManager: TestMovieAppDataManager
    private val movieAppData = MovieAppData(
        secureBaseUrl = configurationTestData.images?.secureBaseUrl ?: "",
        genres = genreListTestData.genres ?: emptyList(),
        region = regionTestData.results ?: emptyList(),
        language = languageListTestData.map { it.copy(isSelected = it.name == "en") },
        posterSize = configurationTestData.images?.posterSizes?.map {
            PosterSize(size = it, isSelected = it == "original")
        } ?: emptyList()
    )

    @Before
    fun setup() {
//        savedStateHandle = SavedStateHandle(route = DetailRoute(id = 0))
        savedStateHandle = SavedStateHandle().apply {
            set("id", 0)
        }
        testUserDataRepository = TestUserDataRepository()
        testDetailRepository = TestDetailRepository()
        testDatabaseRepository = TestDatabaseRepository()
        testPagingRepository = TestPagingRepository()
        testMovieAppDataManager = TestMovieAppDataManager()
        movieDetailUseCase = GetMovieDetailUseCase(
            userDataRepository = testUserDataRepository,
            databaseRepository = testDatabaseRepository,
            detailRepository = testDetailRepository
        )
        viewModel = DetailVM(
            savedStateHandle = savedStateHandle,
            getMovieDetail = movieDetailUseCase,
            databaseRepository = testDatabaseRepository,
            pagingRepository = testPagingRepository
        )

        AndroidThreeTen.init(composeTestRule.activity)
        runBlocking {
            testMovieAppDataManager.setMovieAppData(movieAppData)
        }
    }

    @Test
    fun detailLoadingTest() = runTest {
        composeTestRule.apply {
            setContent {
                val movie by viewModel.detail.collectAsStateWithLifecycle()
                val similarMovie = viewModel.similarMovies.collectAsLazyPagingItems()
                val movieReview = viewModel.movieReviews.collectAsLazyPagingItems()

                DetailScreen(
                    detailState = movie,
                    similarMovies = similarMovie,
                    movieReviews = movieReview,
                    goToMovie = {},
                    goToPeople = {},
                    goToBack = {},
                    onShowSnackbar = { _, _ -> true },
                    insertFavoriteMovie = viewModel::insertMovie,
                    deleteFavoriteMovie = viewModel::deleteMovie,
                    restart = viewModel::restart
                )
            }

            onNodeWithTag(testTag = "detailScreenLoading").assertExists().assertIsDisplayed()
        }
    }

    @Test
    fun detailErrorTest() = runTest {
        composeTestRule.apply {
            setContent {
                val similarMovie = viewModel.similarMovies.collectAsLazyPagingItems()
                val movieReview = viewModel.movieReviews.collectAsLazyPagingItems()

                DetailScreen(
                    detailState = DetailState.Error(throwable = Throwable("something wrong...")),
                    similarMovies = similarMovie,
                    movieReviews = movieReview,
                    goToMovie = {},
                    goToPeople = {},
                    goToBack = {},
                    onShowSnackbar = { _, _ -> true },
                    insertFavoriteMovie = viewModel::insertMovie,
                    deleteFavoriteMovie = viewModel::deleteMovie,
                    restart = viewModel::restart
                )
            }

            onNodeWithText(text = "something wrong...").assertExists().assertIsDisplayed()
        }
    }

    @Test
    fun detailSuccessTest() = runTest {
        composeTestRule.apply {
            setContent {
                val movie by viewModel.detail.collectAsStateWithLifecycle()
                val similarMovie = viewModel.similarMovies.collectAsLazyPagingItems()
                val movieReview = viewModel.movieReviews.collectAsLazyPagingItems()

                DetailScreen(
                    detailState = movie,
                    similarMovies = similarMovie,
                    movieReviews = movieReview,
                    goToMovie = {},
                    goToPeople = {},
                    goToBack = {},
                    onShowSnackbar = { _, _ -> true },
                    insertFavoriteMovie = viewModel::insertMovie,
                    deleteFavoriteMovie = viewModel::deleteMovie,
                    restart = viewModel::restart
                )
            }

            onNodeWithTag(testTag = "detailScreenLoading").assertExists().assertIsDisplayed()

            testDatabaseRepository.insertMovie(movie = favoriteMovieDetailTestData)
            testDetailRepository.setMovie(detail = favoriteMovieDetailTestData)
            testPagingRepository.getSimilarMoviePagingSource(id = 0)
            testDetailRepository.setMovieSeries(movieSeries = movieSeriesTestData)

            onNodeWithContentDescription(label = "favorite").assertExists().assertIsDisplayed()
            onNodeWithTag(testTag = "titleComponent").assertTextEquals(favoriteMovieDetailTestData.title ?: "").assertIsDisplayed()
            onNodeWithTag(testTag = "movieTitle").assertTextEquals(favoriteMovieDetailTestData.title ?: "").assertIsDisplayed()
            onNodeWithText(text = favoriteMovieDetailTestData.originalTitle ?: "").assertExists().assertIsDisplayed()
            onNodeWithText(text = favoriteMovieDetailTestData.overview ?: "").assertExists().assertIsDisplayed()
        }
    }

    @Test
    fun seriesTest() = runTest {
        composeTestRule.apply {
            setContent {
                val movie by viewModel.detail.collectAsStateWithLifecycle()
                val similarMovie = viewModel.similarMovies.collectAsLazyPagingItems()
                val movieReview = viewModel.movieReviews.collectAsLazyPagingItems()

                DetailScreen(
                    detailState = movie,
                    similarMovies = similarMovie,
                    movieReviews = movieReview,
                    goToMovie = {},
                    goToPeople = {},
                    goToBack = {},
                    onShowSnackbar = { _, _ -> true },
                    insertFavoriteMovie = viewModel::insertMovie,
                    deleteFavoriteMovie = viewModel::deleteMovie,
                    restart = viewModel::restart
                )
            }

            onNodeWithTag(testTag = "detailScreenLoading").assertExists().assertIsDisplayed()

            testDatabaseRepository.insertMovie(movie = favoriteMovieDetailTestData)
            testDetailRepository.setMovie(detail = favoriteMovieDetailTestData)
            testPagingRepository.getSimilarMoviePagingSource(id = 0)
            testDetailRepository.setMovieSeries(movieSeries = movieSeriesTestData)

            onNodeWithText(text = "시리즈")
                .performScrollTo()
                .performClick()
                .assertExists()
                .assertIsDisplayed()
                .performClick()

            onNodeWithText(text = movieSeriesTestData.name!!).assertExists().assertIsDisplayed()
            onNodeWithText(text = movieSeriesTestData.overview!!).assertExists().assertIsDisplayed()
            movieSeriesTestData.parts?.forEach { part ->
                onNodeWithContentDescription(label = "seriesList").performScrollToNode(matcher = hasContentDescription(value = part.posterPath!!)).assertExists().assertIsDisplayed()
                onNodeWithContentDescription(label = "seriesList").performScrollToNode(matcher = hasText(text = part.title!!)).assertExists().assertIsDisplayed()
                onNodeWithContentDescription(label = "seriesList").performScrollToNode(matcher = hasText(text = part.releaseDate!!)).assertExists().assertIsDisplayed()
                onNodeWithContentDescription(label = "seriesList").performScrollToNode(matcher = hasText(text = part.overview!!)).assertExists().assertIsDisplayed()
            }
        }
    }

    @Test
    fun castAndCrewTest() = runTest {
        composeTestRule.apply {
            setContent {
                val movie by viewModel.detail.collectAsStateWithLifecycle()
                val similarMovie = viewModel.similarMovies.collectAsLazyPagingItems()
                val movieReview = viewModel.movieReviews.collectAsLazyPagingItems()

                DetailScreen(
                    detailState = movie,
                    similarMovies = similarMovie,
                    movieReviews = movieReview,
                    goToMovie = {},
                    goToPeople = {},
                    goToBack = {},
                    onShowSnackbar = { _, _ -> true },
                    insertFavoriteMovie = viewModel::insertMovie,
                    deleteFavoriteMovie = viewModel::deleteMovie,
                    restart = viewModel::restart
                )
            }

            onNodeWithTag(testTag = "detailScreenLoading").assertExists().assertIsDisplayed()

            testDatabaseRepository.insertMovie(movie = favoriteMovieDetailTestData)
            testDetailRepository.setMovie(detail = favoriteMovieDetailTestData)
            testPagingRepository.getSimilarMoviePagingSource(id = 0)
            testDetailRepository.setMovieSeries(movieSeries = movieSeriesTestData)

            onNodeWithText(text = "배우 / 감독")
                .performScrollTo()
                .performClick()
                .assertExists()
                .assertIsDisplayed()
                .performClick()

            onNodeWithText(text = favoriteMovieDetailTestData.credits?.cast?.get(0)?.name ?: "").assertIsDisplayed()
            onNodeWithTag(testTag = "castAndCrew").performScrollToNode(hasText(text = favoriteMovieDetailTestData.credits?.crew?.get(0)?.name ?: "")).assertIsDisplayed()
        }
    }

    @Test
    fun movieImagesTest() = runTest {
        composeTestRule.apply {
            setContent {
                val movie by viewModel.detail.collectAsStateWithLifecycle()
                val similarMovie = viewModel.similarMovies.collectAsLazyPagingItems()
                val movieReview = viewModel.movieReviews.collectAsLazyPagingItems()

                DetailScreen(
                    detailState = movie,
                    similarMovies = similarMovie,
                    movieReviews = movieReview,
                    goToMovie = {},
                    goToPeople = {},
                    goToBack = {},
                    onShowSnackbar = { _, _ -> true },
                    insertFavoriteMovie = viewModel::insertMovie,
                    deleteFavoriteMovie = viewModel::deleteMovie,
                    restart = viewModel::restart
                )
            }

            onNodeWithTag(testTag = "detailScreenLoading").assertExists().assertIsDisplayed()

            testDatabaseRepository.insertMovie(movie = favoriteMovieDetailTestData)
            testDetailRepository.setMovie(detail = favoriteMovieDetailTestData)
            testPagingRepository.getSimilarMoviePagingSource(id = 0)
            testDetailRepository.setMovieSeries(movieSeries = movieSeriesTestData)

            onNodeWithText(text = "이미지")
                .performScrollTo()
                .performClick()
                .assertExists()
                .assertIsDisplayed()
                .performClick()

            assertEquals(
                expected = favoriteMovieDetailTestData.images?.posters?.plus(element = favoriteMovieDetailTestData.images?.backdrops)?.size,
                actual = 4
            )
            ((favoriteMovieDetailTestData.images?.backdrops ?: emptyList()) + (favoriteMovieDetailTestData.images?.posters ?: emptyList())).forEach {
                it.filePath?.let { filePath ->
//                    onNodeWithTag(testTag = "$posterUrl$filePath").assertExists().assertIsDisplayed()
                    onNodeWithTag(testTag = filePath).assertExists().assertIsDisplayed()
                }
            }
        }
    }

    @Test
    fun similarMovieTest() = runTest {
        val source = SimilarMoviePagingSource(
            apis = TestMovieDataSource(),
            id = 0,
            userDataRepository = testUserDataRepository
        )

        composeTestRule.apply {
            setContent {
                val movie by viewModel.detail.collectAsStateWithLifecycle()
                val similarMovie = viewModel.similarMovies.collectAsLazyPagingItems()
                val movieReview = viewModel.movieReviews.collectAsLazyPagingItems()

                DetailScreen(
                    detailState = movie,
                    similarMovies = similarMovie,
                    movieReviews = movieReview,
                    goToMovie = {},
                    goToPeople = {},
                    goToBack = {},
                    onShowSnackbar = { _, _ -> true },
                    insertFavoriteMovie = viewModel::insertMovie,
                    deleteFavoriteMovie = viewModel::deleteMovie,
                    restart = viewModel::restart
                )
            }

            onNodeWithTag(testTag = "detailScreenLoading").assertExists().assertIsDisplayed()

            testDatabaseRepository.insertMovie(movie = favoriteMovieDetailTestData)
            testDetailRepository.setMovie(detail = favoriteMovieDetailTestData)
            testPagingRepository.getSimilarMoviePagingSource(id = 0)
            testDetailRepository.setMovieSeries(movieSeries = movieSeriesTestData)

            onNodeWithText(text = "다른 영화")
                .performScrollTo()
                .performClick()
                .assertExists()
                .assertIsDisplayed()
                .performClick()

            assertEquals(
                expected = PagingSource.LoadResult.Page<Int, Movie>(
                    data = similarMoviesTestData.results?.map {
                        Movie(
                            id = it.id,
                            title = it.title,
                            posterPath = it.posterPath,
                            genres = it.genreIds?.map { id -> Genre(id = id) },
                            releaseDate = it.releaseDate
                        )
                    } ?: emptyList(),
                    prevKey = null,
                    nextKey = null
                ),
                actual = source.load(
                    params = PagingSource.LoadParams.Refresh(
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
            setContent {
                val movie by viewModel.detail.collectAsStateWithLifecycle()
                val similarMovie = viewModel.similarMovies.collectAsLazyPagingItems()
                val movieReview = viewModel.movieReviews.collectAsLazyPagingItems()

                DetailScreen(
                    detailState = movie,
                    similarMovies = similarMovie,
                    movieReviews = movieReview,
                    goToMovie = {},
                    goToPeople = {},
                    goToBack = {},
                    onShowSnackbar = { _, _ -> true },
                    insertFavoriteMovie = viewModel::insertMovie,
                    deleteFavoriteMovie = viewModel::deleteMovie,
                    restart = viewModel::restart
                )
            }

            onNodeWithTag(testTag = "detailScreenLoading").assertExists().assertIsDisplayed()

            testDatabaseRepository.insertMovie(movie = favoriteMovieDetailTestData)
            testDetailRepository.setMovie(detail = unFavoriteMovieDetailTestData)
            testPagingRepository.getSimilarMoviePagingSource(id = 0)
            testDetailRepository.setMovieSeries(movieSeries = movieSeriesTestData)

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
    fun deleteFavoriteTest() = runTest {
        composeTestRule.apply {
            setContent {
                val movie by viewModel.detail.collectAsStateWithLifecycle()
                val similarMovie = viewModel.similarMovies.collectAsLazyPagingItems()
                val movieReview = viewModel.movieReviews.collectAsLazyPagingItems()

                DetailScreen(
                    detailState = movie,
                    similarMovies = similarMovie,
                    movieReviews = movieReview,
                    goToMovie = {},
                    goToPeople = {},
                    goToBack = {},
                    onShowSnackbar = { _, _ -> true },
                    insertFavoriteMovie = viewModel::insertMovie,
                    deleteFavoriteMovie = viewModel::deleteMovie,
                    restart = viewModel::restart
                )
            }

            onNodeWithTag(testTag = "detailScreenLoading").assertExists().assertIsDisplayed()

            testDatabaseRepository.insertMovie(movie = favoriteMovieDetailTestData)
            testDetailRepository.setMovie(detail = favoriteMovieDetailTestData)
            testPagingRepository.getSimilarMoviePagingSource(id = 0)
            testDetailRepository.setMovieSeries(movieSeries = movieSeriesTestData)

            onNodeWithContentDescription(label = "favorite").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "unFavorite").assertIsNotDisplayed()
            onNodeWithTag(testTag = "titleComponent").assertTextEquals(favoriteMovieDetailTestData.title ?: "").assertIsDisplayed()
            onNodeWithTag(testTag = "movieTitle").assertTextEquals(favoriteMovieDetailTestData.title ?: "").assertIsDisplayed()
            onNodeWithText(text = favoriteMovieDetailTestData.originalTitle ?: "").assertExists().assertIsDisplayed()
            onNodeWithText(text = favoriteMovieDetailTestData.overview ?: "").assertExists().assertIsDisplayed()
            onNodeWithContentDescription(label = "favorite").assertExists().assertIsDisplayed().performClick()
            onNodeWithContentDescription(label = "unFavorite").assertIsDisplayed()
            onNodeWithContentDescription(label = "favorite").assertIsNotDisplayed()
            assertEquals(
                testDatabaseRepository.getMovies().first().find { it.id == 0 },
                null
            )
        }
    }
}