package com.bowoon.home

import androidx.paging.PagingSource
import com.bowoon.model.Movie
import com.bowoon.testing.TestSyncManager
import com.bowoon.testing.model.nowPlayingMovieTest
import com.bowoon.testing.model.upComingMovieTest
import com.bowoon.testing.repository.TestDatabaseRepository
import com.bowoon.testing.repository.TestPagingRepository
import com.bowoon.testing.repository.TestUserDataRepository
import com.bowoon.testing.utils.MainDispatcherRule
import com.bowoon.testing.utils.TestMovieAppDataManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import kotlin.test.assertEquals

class HomeVMTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var viewModel: HomeVM
    private lateinit var testSyncManager: TestSyncManager
    private lateinit var testUserDataRepository: TestUserDataRepository
    private lateinit var testDatabaseRepository: TestDatabaseRepository
    private lateinit var testPagingRepository: TestPagingRepository
    private lateinit var testMovieAppDataManager: TestMovieAppDataManager

    @Before
    fun setup() {
        testSyncManager = TestSyncManager()
        testUserDataRepository = TestUserDataRepository()
        testDatabaseRepository = TestDatabaseRepository()
        testPagingRepository = TestPagingRepository()
        testMovieAppDataManager = TestMovieAppDataManager()
        viewModel = HomeVM(
            userDataRepository = testUserDataRepository,
            databaseRepository = testDatabaseRepository
        )
    }

    @Test
    fun userDataLoadingTest() = runTest {
        backgroundScope.launch(context = UnconfinedTestDispatcher()) { viewModel.mainMenu.collect() }
        assertEquals(expected = viewModel.mainMenu.value, actual = MainMenuState.Loading)
    }

    @Test
    fun userDataSuccessTest() = runTest {
        backgroundScope.launch(context = UnconfinedTestDispatcher()) { viewModel.mainMenu.collect() }
        val movie = Movie(id = 1, title = "testMovie1", releaseDate = LocalDate.now().plusDays(1).toString())
        assertEquals(expected = viewModel.mainMenu.value, actual = MainMenuState.Loading)
//        testUserDataRepository.updateUserData(InternalData(mainMenu = mainMenuTestData), false)
        testDatabaseRepository.insertMovie(movie = movie)
        assertEquals(
            expected = viewModel.mainMenu.value,
            actual = MainMenuState.Success(
                nextWeekReleaseMovies = listOf(movie)
            )
        )
    }

    @Test
    fun nowPlayingMovieTest() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.nowPlayingMoviePager.collect { println(it) } }
        val result = testDatabaseRepository.getNowPlayingMovies()

        assertEquals(
            expected = PagingSource.LoadResult.Page(
                data = nowPlayingMovieTest,
                prevKey = null,
                nextKey = 2
            ),
            actual = result.load(
                params = PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = 2,
                    placeholdersEnabled = false
                )
            )
        )
    }

    @Test
    fun upComingMovieTest() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.upComingMoviePager.collect { println(it) } }
        val result = testDatabaseRepository.getUpComingMovies()

        assertEquals(
            expected = PagingSource.LoadResult.Page(
                data = upComingMovieTest,
                prevKey = null,
                nextKey = 2
            ),
            actual = result.load(
                params = PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = 2,
                    placeholdersEnabled = false
                )
            )
        )
    }
}