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
class FavoriteBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @OptIn(ExperimentalMetricApi::class)
    @Test
    fun favoriteClickSectionTest() = benchmarkRule.measureRepeated(
        packageName = PACKAGE_NAME,
        metrics = listOf(TraceSectionMetric(sectionName = "FavoriteClicked")),
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
        device.waitAndFindObject(
            selector = By.desc("unFavorite"),
            timeout = 5_000
        )
        device.findObject(By.desc("unFavorite")).click()
        device.waitForIdle()
    }

    @OptIn(ExperimentalMetricApi::class)
    @Test
    fun unFavoriteClickSectionTest() = benchmarkRule.measureRepeated(
        packageName = PACKAGE_NAME,
        metrics = listOf(TraceSectionMetric(sectionName = "UnFavoriteClicked")),
        iterations = 5,
        startupMode = StartupMode.COLD
    ) {
        pressHome()
        startActivityAndWait()
        device.findObject(By.text("찜")).click()
        device.waitAndFindObject(
            selector = By.desc("favoriteList"),
            timeout = 5_000
        )
        device.findFirstObject(selector = By.desc("favoriteList")).click()
        device.waitAndFindObject(
            selector = By.desc("favorite"),
            timeout = 5_000
        )
        device.findObject(By.desc("favorite")).click()
        device.waitForIdle()
    }
}