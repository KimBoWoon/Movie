package com.bowoon.domain

import com.bowoon.model.MovieAppData
import com.bowoon.testing.model.movieSeriesTestData
import com.bowoon.testing.repository.TestDetailRepository
import com.bowoon.testing.repository.TestMovieAppDataRepository
import com.bowoon.testing.utils.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class GetSeriesMovieUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var getSeriesMovieUseCase: GetSeriesMovieUseCase
    private lateinit var testDetailRepository: TestDetailRepository
    private lateinit var testMovieAppDataRepository: TestMovieAppDataRepository

    @Before
    fun setup() {
        testDetailRepository = TestDetailRepository()
        testMovieAppDataRepository = TestMovieAppDataRepository()
        getSeriesMovieUseCase = GetSeriesMovieUseCase(
            detailRepository = testDetailRepository,
            movieAppDataRepository = testMovieAppDataRepository
        )

        runBlocking {
            testDetailRepository.setMovieSeries(movieSeriesTestData)
            testMovieAppDataRepository.setMovieAppData(MovieAppData())
        }
    }

    @Test
    fun getMovieSeries() = runTest {
        val result = getSeriesMovieUseCase(collectionId = 0)

        assertEquals(
            result.first(),
            movieSeriesTestData.copy(
                backdropPath = "${testMovieAppDataRepository.movieAppData.value.getImageUrl()}${movieSeriesTestData.backdropPath}",
                parts = movieSeriesTestData.parts?.map {
                    it.copy(
                        backdropPath = "${testMovieAppDataRepository.movieAppData.value.getImageUrl()}${it.backdropPath}",
                        posterPath = "${testMovieAppDataRepository.movieAppData.value.getImageUrl()}${it.posterPath}"
                    )
                },
                posterPath = "${testMovieAppDataRepository.movieAppData.value.getImageUrl()}${movieSeriesTestData.posterPath}"
            )
        )
    }
}