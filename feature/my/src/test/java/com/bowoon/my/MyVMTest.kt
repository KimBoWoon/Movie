package com.bowoon.my

import com.bowoon.model.DarkThemeConfig
import com.bowoon.model.InternalData
import com.bowoon.testing.model.mainMenuTestData
import com.bowoon.testing.repository.TestUserDataRepository
import com.bowoon.testing.utils.MainDispatcherRule
import com.bowoon.testing.utils.TestMovieAppDataManager
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

class MyVMTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var viewModel: MyVM
    private lateinit var testUserDataRepository: TestUserDataRepository
    private lateinit var testMovieAppDataManager: TestMovieAppDataManager

    @Before
    fun setup() {
        testUserDataRepository = TestUserDataRepository()
        testMovieAppDataManager = TestMovieAppDataManager()
        viewModel = MyVM(
            userDataRepository = testUserDataRepository,
            appData = testMovieAppDataManager
        )
        runBlocking {
            testUserDataRepository.updateUserData(InternalData(), false)
        }
    }

    @Test
    fun myDataTest() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.myData.collect() }

        assertEquals(viewModel.myData.value, InternalData())
    }

    @Test
    fun updateUserDataTest() = runTest {
        val userdata = InternalData(
            isAdult = true,
            autoPlayTrailer = false,
            isDarkMode = DarkThemeConfig.LIGHT,
            updateDate = "2025-03-12",
            mainMenu = mainMenuTestData,
            region = "US",
            language = "en",
            imageQuality = "w92"
        )

        assertEquals(testUserDataRepository.internalData.first(), InternalData())
        viewModel.updateUserData(userdata, isSync = false)
        assertEquals(
            testUserDataRepository.internalData.first(),
            userdata
        )
    }
}