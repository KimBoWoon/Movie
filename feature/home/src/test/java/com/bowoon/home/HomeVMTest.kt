package com.bowoon.home

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.testing.TestPager
import com.bowoon.testing.repository.TestDatabaseRepository
import com.bowoon.testing.utils.MainDispatcherRule
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class HomeVMTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var viewModel: HomeVM
    private lateinit var testDatabaseRepository: TestDatabaseRepository

    @Before
    fun setup() {
        testDatabaseRepository = TestDatabaseRepository()
        viewModel = HomeVM(databaseRepository = testDatabaseRepository)
    }

    @Test
    fun userDataLoadingTest() = runTest {
        assertEquals(expected = viewModel.mainMenu.value, actual = MainMenuState.Loading)
        backgroundScope.launch(context = UnconfinedTestDispatcher()) { viewModel.mainMenu.collect() }
    }

    @Test
    fun userDataSuccessTest() = runTest {
        assertEquals(expected = viewModel.mainMenu.value, actual = MainMenuState.Loading)
        backgroundScope.launch(context = UnconfinedTestDispatcher()) { viewModel.mainMenu.collect() }

        val nowPlayingMovieResult = testDatabaseRepository.getNowPlayingMovies()
        val nowPlayingTestPager = TestPager(
            config = PagingConfig(pageSize = 0, initialLoadSize = 20, prefetchDistance = 5),
            pagingSource = testDatabaseRepository.getNowPlayingMovies()
        )

        assertEquals(
            expected = nowPlayingTestPager.refresh(initialKey = 0),
            actual = nowPlayingMovieResult.load(
                params = PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = 20,
                    placeholdersEnabled = false
                )
            )
        )

        val upComingMovieResult = testDatabaseRepository.getUpComingMovies()
        val upComingMovieTestPager = TestPager(
            config = PagingConfig(pageSize = 0, initialLoadSize = 20, prefetchDistance = 5),
            pagingSource = testDatabaseRepository.getUpComingMovies()
        )

        assertEquals(
            expected = upComingMovieTestPager.refresh(initialKey = 0),
            actual = upComingMovieResult.load(
                params = PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = 20,
                    placeholdersEnabled = false
                )
            )
        )
    }
}