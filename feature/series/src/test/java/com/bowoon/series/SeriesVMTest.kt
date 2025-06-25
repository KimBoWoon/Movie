package com.bowoon.series

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.testing.invoke
import com.bowoon.series.navigation.SeriesRoute
import com.bowoon.testing.model.movieSeriesTestData
import com.bowoon.testing.repository.TestDetailRepository
import com.bowoon.testing.repository.TestMovieAppDataRepository
import com.bowoon.testing.utils.MainDispatcherRule
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SeriesVMTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var testDetailRepository: TestDetailRepository
    private lateinit var testMovieAppDataRepository: TestMovieAppDataRepository
    private lateinit var seriesVM: SeriesVM

    @Before
    fun setup() {
        savedStateHandle = SavedStateHandle(route = SeriesRoute(id = 0))
        testDetailRepository = TestDetailRepository()
        testMovieAppDataRepository = TestMovieAppDataRepository()

        seriesVM = SeriesVM(
            savedStateHandle = savedStateHandle,
            detailRepository = testDetailRepository,
            movieAppDataRepository = testMovieAppDataRepository
        )
    }

    @Test
    fun getSeriesTest() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { seriesVM.series.collect() }

        assertEquals(SeriesState.Loading, seriesVM.series.value)

        testDetailRepository.setMovieSeries(movieSeriesTestData)

        assertEquals(
            SeriesState.Success(
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
            ),
            seriesVM.series.value
        )
    }
}