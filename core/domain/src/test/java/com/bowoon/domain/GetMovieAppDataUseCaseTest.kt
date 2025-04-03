package com.bowoon.domain

import com.bowoon.model.ExternalData
import com.bowoon.model.InternalData
import com.bowoon.model.MovieAppData
import com.bowoon.model.PosterSize
import com.bowoon.testing.model.configurationTestData
import com.bowoon.testing.model.genreListTestData
import com.bowoon.testing.model.languageListTestData
import com.bowoon.testing.model.regionTestData
import com.bowoon.testing.repository.TestMyDataRepository
import com.bowoon.testing.repository.TestUserDataRepository
import com.bowoon.testing.utils.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class GetMovieAppDataUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private val testMyDataRepository = TestMyDataRepository()
    private val testUserDataRepository = TestUserDataRepository()
    private val getMovieAppDataUseCase = GetMovieAppDataUseCase(
        myDataRepository = testMyDataRepository,
        userDataRepository = testUserDataRepository
    )

    @Test
    fun getMovieAppDataTest() = runTest {
        val result = getMovieAppDataUseCase()
        val externalData = ExternalData(
            configuration = configurationTestData,
            region = regionTestData,
            language = languageListTestData,
        )
        val movieAppData = MovieAppData(
            secureBaseUrl = configurationTestData.images?.secureBaseUrl,
            genres = genreListTestData.genres ?: emptyList(),
            region = regionTestData.results,
            language = languageListTestData,
            posterSize = configurationTestData.images?.posterSizes?.map {
                PosterSize(size = it, isSelected = if (it == "original") true else false)
            } ?: emptyList()
        )

        testMyDataRepository.setExternalData(externalData)
        testUserDataRepository.updateUserData(InternalData(region = "ko", language = "ko", genres = genreListTestData.genres ?: emptyList()), false)

        assertEquals(result.first(), movieAppData)
    }
}