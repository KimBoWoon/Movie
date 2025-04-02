package com.bowoon.data.repository

import androidx.datastore.preferences.core.preferencesOf
import com.bowoon.datastore.InternalDataSource
import com.bowoon.datastore_test.InMemoryDataStore
import com.bowoon.testing.TestMovieDataSource
import com.bowoon.testing.model.configurationTestData
import com.bowoon.testing.model.genreListTestData
import com.bowoon.testing.model.languageListTestData
import com.bowoon.testing.model.regionTestData
import com.bowoon.testing.utils.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class MyDataRepositoryTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var movieApis: TestMovieDataSource
    private lateinit var datastore: InternalDataSource
    private lateinit var repository: MyDataRepositoryImpl

    @Before
    fun setup() {
        movieApis = TestMovieDataSource()
        datastore = InternalDataSource(
            datastore = InMemoryDataStore(preferencesOf()),
            json = Json { ignoreUnknownKeys = true }
        )
        repository = MyDataRepositoryImpl(
            apis = movieApis,
            datastore = datastore
        )
    }

    @Test
    fun getConfigurationTest() = runTest {
        val result = repository.getConfiguration()

        assertEquals(result.first(), configurationTestData)
    }

    @Test
    fun getAvailableRegionTest() = runTest {
        val result = repository.getAvailableRegion()

        assertEquals(result.first(), regionTestData)
    }

    @Test
    fun getAvailableLanguageTest() = runTest {
        val result = repository.getAvailableLanguage()

        assertEquals(result.first(), languageListTestData)
    }

    @Test
    fun getGenresTest() = runTest {
        val result = repository.getGenres()

        assertEquals(result.first(), genreListTestData)
    }
}