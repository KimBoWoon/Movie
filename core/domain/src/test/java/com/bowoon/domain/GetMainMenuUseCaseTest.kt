package com.bowoon.domain

import com.bowoon.model.InternalData
import com.bowoon.model.MovieAppData
import com.bowoon.testing.model.mainMenuTestData
import com.bowoon.testing.repository.TestMovieAppDataRepository
import com.bowoon.testing.repository.TestUserDataRepository
import com.bowoon.testing.utils.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class GetMainMenuUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var getMainMenuUseCase: GetMainMenuUseCase
    private lateinit var testUserDataRepository: TestUserDataRepository
    private lateinit var testMovieAppDataRepository: TestMovieAppDataRepository

    @Before
    fun setup() {
        testUserDataRepository = TestUserDataRepository()
        testMovieAppDataRepository = TestMovieAppDataRepository()
        getMainMenuUseCase = GetMainMenuUseCase(
            userDataRepository = testUserDataRepository,
            movieAppDataRepository = testMovieAppDataRepository
        )

        runBlocking {
            testUserDataRepository.updateUserData(
                InternalData(mainMenu = mainMenuTestData),
                false
            )
            testMovieAppDataRepository.setMovieAppData(MovieAppData())
        }
    }

    @Test
    fun getMainMenuTest() = runTest {
        val mainMenu = getMainMenuUseCase().first()

        assertEquals(
            mainMenu,
            mainMenuTestData.copy(
                nowPlaying = mainMenuTestData.nowPlaying.map {
                    it.copy(
                        imagePath = "${testMovieAppDataRepository.movieAppData.value.getImageUrl()}${it.imagePath}"
                    )
                },
                upComingMovies = mainMenuTestData.upComingMovies.map {
                    it.copy(
                        imagePath = "${testMovieAppDataRepository.movieAppData.value.getImageUrl()}${it.imagePath}"
                    )
                }
            )
        )
    }
}