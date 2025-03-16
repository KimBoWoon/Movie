package com.bowoon.network

import com.bowoon.testing.utils.MainDispatcherRule
import com.bowoon.testing.TestMovieDataSource
import com.bowoon.testing.repository.combineCreditsTestData
import com.bowoon.testing.repository.externalIdsTestData
import com.bowoon.testing.model.certificationTestData
import com.bowoon.testing.model.configurationTestData
import com.bowoon.testing.model.genreListTestData
import com.bowoon.testing.model.languageListTestData
import com.bowoon.testing.model.nowPlayingMoviesTestData
import com.bowoon.testing.model.peopleSearchTestData
import com.bowoon.testing.model.regionTestData
import com.bowoon.testing.model.similarMoviesTestData
import com.bowoon.testing.model.upcomingMoviesTestData
import com.bowoon.testing.repository.movieDetailTestData
import com.bowoon.testing.repository.movieSearchTestData
import com.bowoon.testing.repository.peopleDetailTestData
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class MovieNetworkDataTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private val datasource = TestMovieDataSource()

    @Test
    fun getConfigurationTest() = runTest {
        val result = datasource.getConfiguration()

        assertEquals(result, configurationTestData)
    }

    @Test
    fun getCertificationTest() = runTest {
        val result = datasource.getCertification()

        assertEquals(result, certificationTestData)
    }

    @Test
    fun getAvailableRegionTest() = runTest {
        val result = datasource.getAvailableRegion()

        assertEquals(result, regionTestData)
    }

    @Test
    fun getAvailableLanguageTest() = runTest {
        val result = datasource.getAvailableLanguage()

        assertEquals(result, languageListTestData)
    }

    @Test
    fun getGenresTest() = runTest {
        val result = datasource.getGenres()

        assertEquals(result, genreListTestData)
    }

    @Test
    fun getNowPlayingTest() = runTest {
        val result = datasource.getNowPlaying()

        assertEquals(result, nowPlayingMoviesTestData)
    }

    @Test
    fun getUpcomingMovieTest() = runTest {
        val result = datasource.getUpcomingMovie()

        assertEquals(result, upcomingMoviesTestData)
    }

    @Test
    fun getMovieDetailTest() = runTest {
        val result = datasource.getMovieDetail(0)

        assertEquals(result, movieDetailTestData)
    }

    @Test
    fun getPeopleDetailTest() = runTest {
        val result = datasource.getPeopleDetail(0)

        assertEquals(result, peopleDetailTestData)
    }

    @Test
    fun getSimilarMovieTest() = runTest {
        val result = datasource.getSimilarMovies(0)

        assertEquals(result, similarMoviesTestData)
    }

    @Test
    fun getCombineCreditsTest() = runTest {
        val result = datasource.getCombineCredits(0)

        assertEquals(result, combineCreditsTestData)
    }

    @Test
    fun getExternalIdsTest() = runTest {
        val result = datasource.getExternalIds(0)

        assertEquals(result, externalIdsTestData)
    }

    @Test
    fun getDiscoverMovieTest() = runTest {
        val result = datasource.discoverMovie("2025-03-10", "2025-03-15")

        assertEquals(result, movieSearchTestData)
    }

    @Test
    fun getSearchMovieTest() = runTest {
        val result = datasource.searchMovies("미션")

        assertEquals(result, movieSearchTestData)
    }

    @Test
    fun getSearchPeopleTest() = runTest {
        val result = datasource.searchPeople("톰 크루즈")

        assertEquals(result, peopleSearchTestData)
    }
}