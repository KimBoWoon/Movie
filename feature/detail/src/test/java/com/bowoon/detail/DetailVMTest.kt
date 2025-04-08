package com.bowoon.detail

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.testing.invoke
import androidx.paging.PagingSource
import com.bowoon.data.paging.TMDBSimilarMoviePagingSource
import com.bowoon.detail.navigation.DetailRoute
import com.bowoon.domain.GetMovieDetailUseCase
import com.bowoon.model.Favorite
import com.bowoon.model.InternalData
import com.bowoon.model.Movie
import com.bowoon.testing.TestMovieDataSource
import com.bowoon.testing.model.movieSeriesTestData
import com.bowoon.testing.model.similarMoviesTestData
import com.bowoon.testing.repository.TestDatabaseRepository
import com.bowoon.testing.repository.TestDetailRepository
import com.bowoon.testing.repository.TestPagingRepository
import com.bowoon.testing.repository.TestUserDataRepository
import com.bowoon.testing.repository.favoriteMovieDetailTestData
import com.bowoon.testing.repository.unFavoriteMovieDetailTestData
import com.bowoon.testing.utils.MainDispatcherRule
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@RunWith(RobolectricTestRunner::class)
class DetailVMTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private val testDataBaseRepository = TestDatabaseRepository()
    private val testPagingRepository = TestPagingRepository()
    private val testDetailRepository = TestDetailRepository()
    private val testUserDataRepository = TestUserDataRepository()
    private val getMovieDetailUseCase = GetMovieDetailUseCase(
        detailRepository = testDetailRepository,
        userDataRepository = testUserDataRepository,
        databaseRepository = testDataBaseRepository
    )
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: DetailVM

    @Before
    fun setup() {
        savedStateHandle = SavedStateHandle(route = DetailRoute(id = 0))
        viewModel = DetailVM(
            savedStateHandle = savedStateHandle,
            databaseRepository = testDataBaseRepository,
            pagingRepository = testPagingRepository,
            getMovieDetail = getMovieDetailUseCase,
            userDataRepository = testUserDataRepository,
            detailRepository = testDetailRepository
        )
        runBlocking {
            testUserDataRepository.updateUserData(InternalData(), false)
            testDataBaseRepository.insertMovie(Favorite(id = 0, title = "movie_1", imagePath = "/movieImagePath.png"))
            testDetailRepository.setMovieSeries(movieSeriesTestData)
        }
    }

    @Test
    fun getFavoriteMovieDetailTest() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.movieInfo.collect() }

        assertEquals(viewModel.movieInfo.value, MovieDetailState.Loading)

        testDetailRepository.setMovieDetail(favoriteMovieDetailTestData)
        getMovieDetailUseCase(0)

        assertEquals(viewModel.movieInfo.value, MovieDetailState.Success(favoriteMovieDetailTestData))
    }

    @Test
    fun getUnFavoriteMovieDetailFlowTest() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.movieInfo.collect() }

        assertEquals(viewModel.movieInfo.value, MovieDetailState.Loading)

        testDetailRepository.setMovieDetail(unFavoriteMovieDetailTestData)
        getMovieDetailUseCase(324)

        assertEquals(viewModel.movieInfo.value, MovieDetailState.Success(unFavoriteMovieDetailTestData))
    }

    @Test
    fun getSimilarMoviesTest() = runTest {
        val source = TMDBSimilarMoviePagingSource(
            apis = TestMovieDataSource(),
            id = 0,
            language = "ko",
            region = "KR"
        )

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

    @Test
    fun insertFavoriteTest() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.movieInfo.collect() }
        val movie = Favorite(id = 23, title = "movie_0", imagePath = "/imagePath.png")

        testDetailRepository.setMovieDetail(favoriteMovieDetailTestData.copy(id = 23))

        assertNotEquals(
            (viewModel.movieInfo.value as? MovieDetailState.Success)?.movieDetail?.isFavorite,
            true
        )
        viewModel.insertMovie(movie)
        assertEquals(
            (viewModel.movieInfo.value as? MovieDetailState.Success)?.movieDetail?.isFavorite,
            true
        )
    }

    @Test
    fun deleteFavoriteTest() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.movieInfo.collect() }
        val movie = Favorite(id = 0, title = "movie_20", imagePath = "/imagePath.png")

        testDetailRepository.setMovieDetail(favoriteMovieDetailTestData)

        assertEquals(
            (viewModel.movieInfo.value as? MovieDetailState.Success)?.movieDetail?.isFavorite,
            true
        )
        viewModel.deleteMovie(movie)
        assertNotEquals(
            (viewModel.movieInfo.value as? MovieDetailState.Success)?.movieDetail?.isFavorite,
            true
        )
    }

    @Test
    fun restartFlowTest() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.movieInfo.collect() }

        assertEquals(viewModel.movieInfo.value, MovieDetailState.Loading)

        testDetailRepository.setMovieDetail(favoriteMovieDetailTestData)
        getMovieDetailUseCase(0)

        assertEquals(viewModel.movieInfo.value, MovieDetailState.Success(favoriteMovieDetailTestData))

        viewModel.restart()

//        assertEquals(viewModel.movieInfo.value, MovieDetailState.Loading)

//        testDetailRepository.setMovieDetail(favoriteMovieDetailTestData)
//        testUserDataRepository.updateUserData(InternalData(), false)
//        testDataBaseRepository.insertMovie(Favorite(id = 0, title = "title_1"))
//        getMovieDetailUseCase(0)

        assertEquals(viewModel.movieInfo.value, MovieDetailState.Success(favoriteMovieDetailTestData))
    }

    @Test
    fun getMovieSeriesTest() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.movieSeries.collect() }

        assertEquals(viewModel.movieSeries.value, null)

        viewModel.getMovieSeries(0)

        assertEquals(viewModel.movieSeries.value, movieSeriesTestData)
    }
}