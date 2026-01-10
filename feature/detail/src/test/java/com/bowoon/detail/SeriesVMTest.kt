package com.bowoon.detail

import com.bowoon.detail.series.SeriesState
import com.bowoon.detail.series.SeriesVM
import com.bowoon.testing.model.movieSeriesTestData
import com.bowoon.testing.repository.TestDetailRepository
import com.bowoon.testing.utils.MainDispatcherRule
import com.bowoon.testing.utils.TestMovieAppDataManager
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
    private lateinit var testDetailRepository: TestDetailRepository
    private lateinit var testMovieAppDataManager: TestMovieAppDataManager
    private lateinit var seriesVM: SeriesVM

    @Before
    fun setup() {
        testDetailRepository = TestDetailRepository()
        testMovieAppDataManager = TestMovieAppDataManager()

        seriesVM = SeriesVM(
            id = 0,
            detailRepository = testDetailRepository
        )
    }

    @Test
    fun getSeriesTest() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { seriesVM.series.collect() }

        assertEquals(SeriesState.Loading, seriesVM.series.value)

        testDetailRepository.setMovieSeries(movieSeriesTestData)

        assertEquals(
            SeriesState.Success(movieSeriesTestData),
            seriesVM.series.value
        )
    }
}