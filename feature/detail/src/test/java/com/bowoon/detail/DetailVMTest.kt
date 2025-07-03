package com.bowoon.detail

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.testing.invoke
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.testing.TestPager
import androidx.paging.testing.asSnapshot
import com.bowoon.data.paging.SimilarMoviePagingSource
import com.bowoon.detail.navigation.DetailRoute
import com.bowoon.domain.GetMovieDetailUseCase
import com.bowoon.model.DisplayItem
import com.bowoon.model.Favorite
import com.bowoon.model.InternalData
import com.bowoon.model.MovieInfo
import com.bowoon.testing.TestMovieDataSource
import com.bowoon.testing.model.movieSeriesTestData
import com.bowoon.testing.model.similarMoviesTestData
import com.bowoon.testing.repository.TestDatabaseRepository
import com.bowoon.testing.repository.TestDetailRepository
import com.bowoon.testing.repository.TestMovieAppDataRepository
import com.bowoon.testing.repository.TestPagingRepository
import com.bowoon.testing.repository.TestUserDataRepository
import com.bowoon.testing.repository.favoriteMovieDetailTestData
import com.bowoon.testing.repository.unFavoriteMovieDetailTestData
import com.bowoon.testing.utils.MainDispatcherRule
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
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
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class DetailVMTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private val testDataBaseRepository = TestDatabaseRepository()
    private val testPagingRepository = TestPagingRepository()
    private val testDetailRepository = TestDetailRepository()
    private val testUserDataRepository = TestUserDataRepository()
    private val testMovieAppDataRepository = TestMovieAppDataRepository()
    private val getMovieDetailUseCase = GetMovieDetailUseCase(
        detailRepository = testDetailRepository,
        userDataRepository = testUserDataRepository,
        databaseRepository = testDataBaseRepository,
        pagingRepository = testPagingRepository
    )
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: DetailVM

    @Before
    fun setup() {
        savedStateHandle = SavedStateHandle(route = DetailRoute(id = 0))
        viewModel = DetailVM(
            savedStateHandle = savedStateHandle,
            databaseRepository = testDataBaseRepository,
            getMovieDetail = getMovieDetailUseCase
        )
        runBlocking {
            testUserDataRepository.updateUserData(InternalData(), false)
            testDataBaseRepository.insertMovie(Favorite(id = 0, title = "movie_1", imagePath = "/movieImagePath.png"))
            testDetailRepository.setMovieSeries(movieSeriesTestData)
        }
    }

    @Test
    fun getFavoriteMovieDetailTest() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.detail.collect() }

        val testPager = TestPager(
            config = PagingConfig(pageSize = 0, initialLoadSize = 7, prefetchDistance = 5),
            pagingSource = testPagingRepository.getSimilarMoviePagingSource(id = 0, language = "ko-KR")
        )

        assertEquals(viewModel.detail.value, DetailState.Loading)

        testDetailRepository.setMovie(favoriteMovieDetailTestData)

        assertTrue(viewModel.detail.value is DetailState.Success)

        val similarMovies = assertIs<DetailState.Success>(viewModel.detail.value).movieInfo.similarMovies

        assertEquals(
            similarMovies.asSnapshot(),
            (testPager.refresh(initialKey = 0) as PagingSource.LoadResult.Page).data
        )

        assertEquals(
            viewModel.detail.value,
            DetailState.Success(
                MovieInfo(
                    detail = favoriteMovieDetailTestData,
                    series = movieSeriesTestData,
                    similarMovies = similarMovies
                )
            )
        )
    }

    @Test
    fun getUnFavoriteMovieDetailFlowTest() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.detail.collect() }

        val testPager = TestPager(
            config = PagingConfig(pageSize = 0, initialLoadSize = 7, prefetchDistance = 5),
            pagingSource = testPagingRepository.getSimilarMoviePagingSource(id = 0, language = "ko-KR")
        )

        assertEquals(viewModel.detail.value, DetailState.Loading)

        testDetailRepository.setMovie(unFavoriteMovieDetailTestData)

        assertTrue(viewModel.detail.value is DetailState.Success)

        val similarMovies = assertIs<DetailState.Success>(viewModel.detail.value).movieInfo.similarMovies

        assertEquals(
            similarMovies.asSnapshot(),
            (testPager.refresh(initialKey = 0) as PagingSource.LoadResult.Page).data
        )

        assertEquals(
            viewModel.detail.value,
            DetailState.Success(
                MovieInfo(
                    detail = unFavoriteMovieDetailTestData,
                    series = movieSeriesTestData,
                    similarMovies = similarMovies
                )
            )
        )
    }

    @Test
    fun getSimilarMoviesTest() = runTest {
        val source = SimilarMoviePagingSource(
            apis = TestMovieDataSource(),
            id = 0,
            language = "ko"
        )

        assertEquals(
            expected = PagingSource.LoadResult.Page<Int, DisplayItem>(
                data = similarMoviesTestData.results?.map {
                    DisplayItem(
                        id = it.id,
                        title = it.title,
                        imagePath = it.posterPath
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

    @Test
    fun insertFavoriteTest() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.detail.collect() }
        val movie = Favorite(id = 23, title = "movie_0", imagePath = "/imagePath.png")

        testDetailRepository.setMovie(favoriteMovieDetailTestData.copy(id = 23))

        assertEquals(
            assertIs<DetailState.Success>(viewModel.detail.value).movieInfo.detail.isFavorite,
            false
        )
        viewModel.insertMovie(movie)
        assertNotEquals(
            assertIs<DetailState.Success>(viewModel.detail.value).movieInfo.detail.isFavorite,
            true
        )

//        assertEquals(
//            testDataBaseRepository.getMovies().first(),
//            emptyList()
//        )
//        viewModel.insertMovie(movie)
//        assertEquals(
//            testDataBaseRepository.getMovies().first(),
//            listOf(movie)
//        )
    }

    @Test
    fun deleteFavoriteTest() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.detail.collect { println(it) } }
        val movie = Favorite(id = 0, title = "movie_1", imagePath = "/movieImagePath.png")

        testDetailRepository.setMovie(favoriteMovieDetailTestData)

//        assertEquals(
//            assertIs<DetailState.Success>(viewModel.detail.value).detail?.isFavorite,
//            true
//        )
//        viewModel.deleteMovie(movie)
//        assertEquals(
//            assertIs<DetailState.Success>(viewModel.detail.value).detail?.isFavorite,
//            false
//        )

        assertEquals(
            testDataBaseRepository.getMovies().first(),
            listOf(movie)
        )
        viewModel.deleteMovie(movie)
        assertEquals(
            testDataBaseRepository.getMovies().first(),
            emptyList()
        )
    }

    @Test
    fun restartFlowTest() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.detail.collect() }

        val testPager = TestPager(
            config = PagingConfig(pageSize = 0, initialLoadSize = 7, prefetchDistance = 5),
            pagingSource = testPagingRepository.getSimilarMoviePagingSource(id = 0, language = "ko-KR")
        )

        assertEquals(viewModel.detail.value, DetailState.Loading)

        testDetailRepository.setMovie(favoriteMovieDetailTestData)

        assertTrue(viewModel.detail.value is DetailState.Success)

        val similarMovies = assertIs<DetailState.Success>(viewModel.detail.value).movieInfo.similarMovies

        assertEquals(
            similarMovies.asSnapshot(),
            (testPager.refresh(initialKey = 0) as PagingSource.LoadResult.Page).data
        )

        assertEquals(
            viewModel.detail.value,
            DetailState.Success(
                MovieInfo(
                    detail = favoriteMovieDetailTestData,
                    series = movieSeriesTestData,
                    similarMovies = similarMovies
                )
            )
        )

        viewModel.restart()

//        assertEquals(viewModel.movieInfo.value, MovieDetailState.Loading)

//        testDetailRepository.setMovieDetail(favoriteMovieDetailTestData)
//        testUserDataRepository.updateUserData(InternalData(), false)
//        testDataBaseRepository.insertMovie(Favorite(id = 0, title = "title_1"))
//        getMovieDetailUseCase(0)

//        assertEquals(viewModel.detail.value, DetailState.Success(favoriteMovieDetailTestData, movieSeriesTestData, emptyFlow()))
    }
}