package com.bowoon.domain

import com.bowoon.model.Configuration
import com.bowoon.model.ExternalData
import com.bowoon.model.Images
import com.bowoon.model.InternalData
import com.bowoon.model.MovieAppData
import com.bowoon.model.PosterSize
import com.bowoon.testing.utils.MainDispatcherRule
import com.bowoon.testing.repository.TestMyDataRepository
import com.bowoon.testing.repository.TestUserDataRepository
import com.bowoon.testing.model.certificationTestData
import com.bowoon.testing.model.genreListTestData
import com.bowoon.testing.model.languageListTestData
import com.bowoon.testing.model.regionTestData
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
            secureBaseUrl = "https://www.bowoon.com",
            configuration = Configuration(),
            certification = certificationTestData.certifications?.certifications,
            genres = genreListTestData,
            region = regionTestData,
            language = languageListTestData,
            posterSize = Images()
        )
        val movieAppData = MovieAppData(
            secureBaseUrl = externalData.secureBaseUrl,
            configuration = externalData.configuration,
            certification = certificationTestData.certifications?.certifications,
            genres = genreListTestData.genres,
            region = regionTestData.results,
            language = languageListTestData,
            posterSize = externalData.posterSize?.posterSizes?.map {
                PosterSize(size = it, isSelected = false)
            } ?: emptyList()
        )

        testMyDataRepository.setExternalData(externalData)
        testUserDataRepository.updateUserData(InternalData(), false)

        assertEquals(result.first(), movieAppData)
    }
}