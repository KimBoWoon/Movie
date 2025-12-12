package com.bowoon.data.repository

import androidx.datastore.preferences.core.preferencesOf
import com.bowoon.data.TestSynchronizer
import com.bowoon.data.util.Synchronizer
import com.bowoon.datastore.InternalDataSource
import com.bowoon.datastore_test.InMemoryDataStore
import com.bowoon.testing.TestMovieDataSource
import com.bowoon.testing.model.nowPlayingMoviesTestData
import com.bowoon.testing.model.upcomingMoviesTestData
import com.bowoon.testing.repository.TestMainMenuRepository
import com.bowoon.testing.utils.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import kotlin.test.assertEquals

private const val BASE_URL = "https://localhost/"

class MainMenuRepositoryTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var movieApis: TestMovieDataSource
    private lateinit var datastore: InternalDataSource
    private lateinit var repository: TestMainMenuRepository
    private lateinit var synchronizer: Synchronizer

    @Before
    fun setup() {
        movieApis = TestMovieDataSource()
        datastore = InternalDataSource(
            datastore = InMemoryDataStore(initialValue = preferencesOf()),
            json = Json { ignoreUnknownKeys = true }
        )
        repository = TestMainMenuRepository()
        synchronizer = TestSynchronizer(datastore)
    }

    @Test
    fun syncTest() = runTest {
        assertEquals(false, repository.syncWith(synchronizer))
        repository.setDate(LocalDate.now().minusDays(3))
        assertEquals(true, repository.syncWith(synchronizer))
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

//class MainMenuRepositoryUseRetrofitTest {
//    @get:Rule
//    val mainDispatcherRule = MainDispatcherRule()
//    private lateinit var server: MockWebServer
//    private lateinit var tmdbApi: TMDBApis
//    private lateinit var retrofit: Retrofit
//    private val repository = TestMainMenuRepository()
//
//    @Before
//    fun setUp() {
//        server = MockWebServer()
//        server.start()
//        retrofit = Retrofit.Builder()
//            .baseUrl(server.url(BASE_URL))
//            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
//            .build()
//        tmdbApi = retrofit.create(TMDBApis::class.java)
//    }
//
//    @After
//    fun cleanUp() {
//        server.shutdown()
//    }
//
//    @Test
//    fun syncTest() = runTest {
//        assertEquals(false, repository.syncWith(false))
//        repository.setDate(LocalDate.now().minusDays(2))
//        assertEquals(true, repository.syncWith(false))
//    }
//
//    @Test
//    fun forceSyncTest() = runTest {
//        assertEquals(true, repository.syncWith(true))
//        assertNotEquals(true, repository.syncWith(false))
//    }
//
//    @Test
//    fun getNowPlayingMoviesTest() = runTest {
//        server.enqueue(MockResponse().setBody(nowPlayingMoviesTestData.toString()))
//        val result = repository.getNowPlaying()
//
//        assertEquals(result, nowPlayingMoviesTestData)
//    }
//
//    @Test
//    fun upcomingMoviesTest() = runTest {
//        server.enqueue(MockResponse().setBody(upcomingMoviesTestData.toString()))
//        val result = repository.getUpcomingMovies()
//
//        assertEquals(result, upcomingMoviesTestData)
//    }
//}