package com.bowoon.home

import com.bowoon.model.InternalData
import com.bowoon.testing.TestSyncManager
import com.bowoon.testing.model.mainMenuTestData
import com.bowoon.testing.repository.TestUserDataRepository
import com.bowoon.testing.utils.MainDispatcherRule
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
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

    @Before
    fun setup() {
        testSyncManager = TestSyncManager()
        testUserDataRepository = TestUserDataRepository()
        viewModel = HomeVM(
            syncManager = testSyncManager,
            userDataRepository = testUserDataRepository
        )
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
}