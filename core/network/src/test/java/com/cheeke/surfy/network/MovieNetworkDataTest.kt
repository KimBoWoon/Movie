package com.cheeke.surfy.network

import com.cheeke.surfy.testing.TestMovieDataSource
import com.cheeke.surfy.testing.model.certificationTestData
import com.cheeke.surfy.testing.model.combineCreditsTestData
import com.cheeke.surfy.testing.model.configurationTestData
import com.cheeke.surfy.testing.model.externalIdsTestData
import com.cheeke.surfy.testing.model.favoriteMovieDetailTestData
import com.cheeke.surfy.testing.model.genreListTestData
import com.cheeke.surfy.testing.model.languageListTestData
import com.cheeke.surfy.testing.model.movieSearchTestData
import com.cheeke.surfy.testing.model.nowPlayingMoviesTestData
import com.cheeke.surfy.testing.model.peopleDetailTestData
import com.cheeke.surfy.testing.model.peopleSearchTestData
import com.cheeke.surfy.testing.model.regionTestData
import com.cheeke.surfy.testing.model.similarMoviesTestData
import com.cheeke.surfy.testing.model.upcomingMoviesTestData
import com.cheeke.surfy.testing.utils.MainDispatcherRule
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
    fun getMovieGenresTest() = runTest {
        val result = datasource.getMovieGenres()

        assertEquals(result, genreListTestData)
    }

    @Test
    fun getTvGenresTest() = runTest {
        val result = datasource.getTvGenres(language = "ko-KR")

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
        val result = datasource.getMovie(0)

        assertEquals(result, favoriteMovieDetailTestData)
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