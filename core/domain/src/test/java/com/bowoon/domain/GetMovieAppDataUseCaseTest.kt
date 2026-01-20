package com.bowoon.domain

import com.bowoon.data.util.MovieAppDataState
import com.bowoon.model.MovieAppData
import com.bowoon.model.PosterSize
import com.bowoon.testing.model.configurationTestData
import com.bowoon.testing.model.genreListTestData
import com.bowoon.testing.model.languageListTestData
import com.bowoon.testing.model.regionTestData
import com.bowoon.testing.utils.MainDispatcherRule
import com.bowoon.testing.utils.TestMovieAppDataManager
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class GetMovieAppDataUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private val testMovieAppDataManager = TestMovieAppDataManager()
    private val movieAppData = MovieAppData(
        secureBaseUrl = configurationTestData.images?.secureBaseUrl ?: "",
        genres = genreListTestData.genres ?: emptyList(),
        region = regionTestData.results ?: emptyList(),
        language = languageListTestData,
        posterSize = configurationTestData.images?.posterSizes?.map {
            PosterSize(size = it, isSelected = it == "original")
        } ?: emptyList()
    )

    @Test
    fun getMovieAppDataTest() = runTest {
        val result = testMovieAppDataManager.movieAppData

        assertIs<MovieAppDataState.Loading>(value = result.value)

        testMovieAppDataManager.setMovieAppData(movieAppData)

        assertEquals(
            expected = assertIs<MovieAppDataState.Success>(value = result.value).data,
            actual = movieAppData
        )
    }
}