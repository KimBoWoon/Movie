package com.cheeke.surfy.benchmark

import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.TraceSectionMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DetailBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @OptIn(ExperimentalMetricApi::class)
    @Test
    fun getMovieDetailSectionTest() = benchmarkRule.measureRepeated(
        packageName = PACKAGE_NAME,
        metrics = listOf(
            TraceSectionMetric(sectionName = "GetMovieDetail"),
            TraceSectionMetric(sectionName = "GetPeopleDetail"),
            TraceSectionMetric(sectionName = "GetTvDetail"),
            TraceSectionMetric(sectionName = "GetSeriesDetail"),
        ),
        iterations = 5,
        startupMode = StartupMode.COLD
    ) {
        pressHome()
        startActivityAndWait()
        device.waitAndFindObject(
            selector = By.desc("nowPlayingMovies"),
            timeout = 5_000
        )
        device.findFirstObject(selector = By.desc("nowPlayingMovies")).click()
        device.waitForIdle()
    }
}