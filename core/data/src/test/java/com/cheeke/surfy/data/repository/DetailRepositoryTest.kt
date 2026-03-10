package com.cheeke.surfy.data.repository

import com.cheeke.surfy.datastore.InternalDataSource
import com.cheeke.surfy.datastore_test.InMemoryDataStore
import com.cheeke.surfy.core.datastore.InternalDataPreferences
import com.cheeke.surfy.network.TMDBApis
import com.cheeke.surfy.testing.TestMovieDataSource
import com.cheeke.surfy.testing.model.combineCreditsTestData
import com.cheeke.surfy.testing.model.externalIdsTestData
import com.cheeke.surfy.testing.model.favoriteMovieDetailTestData
import com.cheeke.surfy.testing.model.movieSearchTestData
import com.cheeke.surfy.testing.model.peopleDetailTestData
import com.cheeke.surfy.testing.repository.TestDetailRepository
import com.cheeke.surfy.testing.utils.MainDispatcherRule
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.flow.first
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
import kotlin.test.assertEquals

private const val BASE_URL = "https://localhost/"

class DetailRepositoryTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var movieApis: TestMovieDataSource
    private lateinit var datastore: InternalDataSource
    private lateinit var repository: DetailRepositoryImpl

    @Before
    fun setup() {
        movieApis = TestMovieDataSource()
        datastore = InternalDataSource(
            datastore = InMemoryDataStore(initialValue = InternalDataPreferences.getDefaultInstance())
        )
        repository = DetailRepositoryImpl(
            apis = movieApis,
            datastore = datastore
        )
    }

    @Test
    fun getMovieDetailTest() = runTest {
        val result = repository.getMovie(0)

        assertEquals(result.first(), favoriteMovieDetailTestData)
    }

    @Test
    fun getPeopleDetailTest() = runTest {
        val result = repository.getPeople(0)

        assertEquals(result.first(), peopleDetailTestData)
    }

    @Test
    fun getDiscoverMovieTest() = runTest {
        val result = repository.discoverMovie(releaseDateGte = "1992-06-24", releaseDateLte = "1992-06-24")

        assertEquals(result.first(), movieSearchTestData)
    }

    @Test
    fun getCombineCreditsTest() = runTest {
        val result = repository.getCombineCredits(0)

        assertEquals(result.first(), combineCreditsTestData)
    }

    @Test
    fun getExternalIdsTest() = runTest {
        val result = repository.getExternalIds(0)

        assertEquals(result.first(), externalIdsTestData)
    }
}

class DetailRepositoryUseRetrofitTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var server: MockWebServer
    private lateinit var tmdbApi: TMDBApis
    private lateinit var retrofit: Retrofit
    private val repository = TestDetailRepository()

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
    fun getMovieDetailTest() = runTest {
        repository.setMovie(favoriteMovieDetailTestData)

        server.enqueue(MockResponse().setBody(favoriteMovieDetailTestData.toString()))
        val result = repository.getMovie(0)

        assertEquals(result.first(), favoriteMovieDetailTestData)
    }

    @Test
    fun getPeopleDetailTest() = runTest {
        repository.setPeopleDetail(peopleDetailTestData)

        server.enqueue(MockResponse().setBody(peopleDetailTestData.toString()))
        val result = repository.getPeople(0)

        assertEquals(result.first(), peopleDetailTestData)
    }

    @Test
    fun getDiscoverMovieTest() = runTest {
        repository.setDiscoverMovie(movieSearchTestData)

        server.enqueue(MockResponse().setBody(movieSearchTestData.toString()))
        val result = repository.discoverMovie(releaseDateGte = "1992-06-24", releaseDateLte = "1992-06-24")

        assertEquals(result.first(), movieSearchTestData)
    }

    @Test
    fun getCombineCreditsTest() = runTest {
        repository.setCombineCredits(combineCreditsTestData)

        server.enqueue(MockResponse().setBody(combineCreditsTestData.toString()))
        val result = repository.getCombineCredits(0)

        assertEquals(result.first(), combineCreditsTestData)
    }

    @Test
    fun getExternalIdsTest() = runTest {
        repository.setExternalIds(externalIdsTestData)

        server.enqueue(MockResponse().setBody(externalIdsTestData.toString()))
        val result = repository.getExternalIds(0)

        assertEquals(result.first(), externalIdsTestData)
    }
}