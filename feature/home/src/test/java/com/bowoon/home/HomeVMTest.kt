package com.bowoon.home

import com.bowoon.model.InternalData
import com.bowoon.testing.TestSyncManager
import com.bowoon.testing.model.mainMenuTestData
import com.bowoon.testing.repository.TestPagingRepository
import com.bowoon.testing.repository.TestUserDataRepository
import com.bowoon.testing.utils.MainDispatcherRule
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HomeVMTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var viewModel: HomeVM
    private lateinit var testSyncManager: TestSyncManager
    private lateinit var testUserDataRepository: TestUserDataRepository
    private lateinit var testPagingRepository: TestPagingRepository

    @Before
    fun setup() {
        testSyncManager = TestSyncManager()
        testUserDataRepository = TestUserDataRepository()
        testPagingRepository = TestPagingRepository()
        viewModel = HomeVM(
            syncManager = testSyncManager,
            userDataRepository = testUserDataRepository,
            pagingRepository = testPagingRepository
        )

//        runBlocking {
//            testUserDataRepository.updateUserData(InternalData(), false)
//        }
    }

    @Test
    fun syncTest() = runTest {
        assertEquals(testSyncManager.isSyncing.first(), false)
        testSyncManager.testSync(true)
        assertEquals(testSyncManager.isSyncing.first(), true)
    }

    @Test
    fun userDataLoadingTest() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.mainMenu.collect() }
        assertEquals(viewModel.mainMenu.value, MainMenuState.Loading)
    }

    @Test
    fun userDataSuccessTest() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.mainMenu.collect() }
        assertEquals(viewModel.mainMenu.value, MainMenuState.Loading)
        testUserDataRepository.updateUserData(InternalData(mainMenu = mainMenuTestData), false)
        assertEquals(viewModel.mainMenu.value, MainMenuState.Success(mainMenuTestData))
    }

//    @Test
//    fun nowPlayingMovieTest() = runTest {
//        val userdata = testUserDataRepository.internalData.first()
//        val language = "${userdata.language}-${userdata.region}"
//        val region = userdata.region
//        val result = testPagingRepository.getNowPlaying(language = language, region = region, "2025-02-24", "2025-03-24")
//
//        assertEquals(
//            viewModel.nowPlaying.first(),
//            result.load(
//                params = PagingSource.LoadParams.Refresh(
//                    key = 1,
//                    loadSize = 1,
//                    placeholdersEnabled = false
//                )
//            )
//        )
//    }
//
//    @Test
//    fun upComingMovieTest() = runTest {
//        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.upComing.collect { println(it) } }
//        val userdata = testUserDataRepository.internalData.first()
//        val language = "${userdata.language}-${userdata.region}"
//        val region = userdata.region
//        val result = testPagingRepository.getUpComingMovies(language = language, region = region, "2025-03-24", "2025-04-24")
//
//        assertEquals(
//            viewModel.upComing.first(),
//            result.load(
//                params = PagingSource.LoadParams.Refresh(
//                    key = 1,
//                    loadSize = 1,
//                    placeholdersEnabled = false
//                )
//            )
//        )
//    }
}