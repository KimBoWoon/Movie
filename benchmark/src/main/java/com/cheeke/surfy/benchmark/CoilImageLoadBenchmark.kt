package com.cheeke.surfy.benchmark

import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.TraceSectionMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CoilImageLoadBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @OptIn(ExperimentalMetricApi::class)
    @Test
    fun coilImageLoadTest() = benchmarkRule.measureRepeated(
        packageName = PACKAGE_NAME,
        metrics = listOf(TraceSectionMetric(sectionName = "CoilImageLoad")),
        iterations = 5,
        startupMode = StartupMode.COLD
    ) {
        pressHome()
        startActivityAndWait()
        device.waitAndFindObject(
            selector = By.desc("nowPlayingMovies"),
            timeout = 5_000
        )
        device.waitAndFindObject(
            selector = By.desc("upComingMovies"),
            timeout = 5_000
        )
        device.findObject(By.desc("nowPlayingMovies")).fling(Direction.RIGHT)
        device.findObject(By.desc("upComingMovies")).fling(Direction.RIGHT)
        device.waitForIdle()
    }
}