package com.bowoon.domain

import com.bowoon.model.InternalData
import com.bowoon.model.MovieAppData
import com.bowoon.model.PosterSize
import com.bowoon.testing.model.configurationTestData
import com.bowoon.testing.model.genreListTestData
import com.bowoon.testing.model.languageListTestData
import com.bowoon.testing.model.regionTestData
import com.bowoon.testing.repository.TestMovieAppDataRepository
import com.bowoon.testing.repository.TestUserDataRepository
import com.bowoon.testing.utils.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class GetMovieAppDataUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private val testMovieAppDataRepository = TestMovieAppDataRepository()
    private val testUserDataRepository = TestUserDataRepository()

    @Test
    fun getMovieAppDataTest() = runTest {
        val result = testMovieAppDataRepository.movieAppData
        val movieAppData = MovieAppData(
            secureBaseUrl = configurationTestData.images?.secureBaseUrl,
            genres = genreListTestData.genres ?: emptyList(),
            region = regionTestData.results,
            language = languageListTestData,
            posterSize = configurationTestData.images?.posterSizes?.map {
                PosterSize(size = it, isSelected = it == "original")
            } ?: emptyList()
        )

        testMovieAppDataRepository.setMovieAppData(movieAppData)
        testUserDataRepository.updateUserData(InternalData(region = "ko", language = "ko"), false)

        assertEquals(result.value, movieAppData)
    }
}