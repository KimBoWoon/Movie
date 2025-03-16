package com.bowoon.data.repository

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.bowoon.datastore.InternalDataSource
import com.bowoon.network.TMDBApis
import com.bowoon.testing.utils.MainDispatcherRule
import com.bowoon.testing.repository.TestMainMenuRepository
import com.bowoon.testing.TestMovieDataSource
import com.bowoon.testing.model.nowPlayingMoviesTestData
import com.bowoon.testing.model.upcomingMoviesTestData
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import java.io.File
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

private const val BASE_URL = "https://localhost/"

private val movieApis = TestMovieDataSource()
private val repository = MainMenuRepositoryImpl(
    apis = movieApis,
    datastore = InternalDataSource(
        datastore = PreferenceDataStoreFactory.create(
            scope = TestScope(UnconfinedTestDispatcher()),
            produceFile = { File("movie-test-store.preferences_pb") }
        ),
        json = Json { ignoreUnknownKeys = true }
    )
)
private val testRepository = TestMainMenuRepository()

class MainMenuRepositoryTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun syncTest() = runTest {
        assertEquals(false, testRepository.syncWith(false))
        testRepository.setDate(LocalDate.now().minusDays(3))
        assertEquals(true, testRepository.syncWith(false))
    }

    @Test
    fun forceSyncTest() = runTest {
        assertEquals(true, testRepository.syncWith(true))
        assertNotEquals(true, testRepository.syncWith(false))
    }

    @Test
    fun getNowPlayingMoviesTest() = runTest {
        val result = repository.getNowPlaying()

        assertEquals(result, nowPlayingMoviesTestData)
    }

    @Test
    fun upcomingMoviesTest() = runTest {
        val result = repository.getUpcomingMovies()

        assertEquals(result, upcomingMoviesTestData)
    }
}

class MainMenuRepositoryUseRetrofitTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var server: MockWebServer
    private lateinit var tmdbApi: TMDBApis
    private lateinit var retrofit: Retrofit
    private val repository = TestMainMenuRepository()

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        retrofit = Retrofit.Builder()
            .baseUrl(server.url(BASE_URL))
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()
        tmdbApi = retrofit.create(TMDBApis::class.java)
    }

    @After
    fun cleanUp() {
        server.shutdown()
    }

    @Test
    fun syncTest() = runTest {
        assertEquals(false, repository.syncWith(false))
        repository.setDate(LocalDate.now().minusDays(2))
        assertEquals(true, repository.syncWith(false))
    }

    @Test
    fun forceSyncTest() = runTest {
        assertEquals(true, repository.syncWith(true))
        assertNotEquals(true, repository.syncWith(false))
    }

    @Test
    fun getNowPlayingMoviesTest() = runTest {
        server.enqueue(MockResponse().setBody(nowPlayingMoviesTestData.toString()))
        val result = repository.getNowPlaying()

        assertEquals(result, nowPlayingMoviesTestData)
    }

    @Test
    fun upcomingMoviesTest() = runTest {
        server.enqueue(MockResponse().setBody(upcomingMoviesTestData.toString()))
        val result = repository.getUpcomingMovies()

        assertEquals(result, upcomingMoviesTestData)
    }
}