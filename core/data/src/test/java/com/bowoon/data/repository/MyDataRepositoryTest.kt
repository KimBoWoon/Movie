package com.bowoon.data.repository

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.bowoon.datastore.InternalDataSource
import com.bowoon.testing.utils.MainDispatcherRule
import com.bowoon.testing.TestMovieDataSource
import com.bowoon.testing.configurationTestData
import com.bowoon.testing.model.certificationTestData
import com.bowoon.testing.model.genreListTestData
import com.bowoon.testing.model.languageListTestData
import com.bowoon.testing.model.regionTestData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Rule
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals

private val movieApis = TestMovieDataSource()
private val repository = MyDataRepositoryImpl(
    apis = movieApis,
    datastore = InternalDataSource(
        datastore = PreferenceDataStoreFactory.create(
            scope = TestScope(UnconfinedTestDispatcher()),
            produceFile = { File("movie-test-store.preferences_pb") }
        ),
        json = Json { ignoreUnknownKeys = true }
    )
)

class MyDataRepositoryTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun getConfigurationTest() = runTest {
        val result = repository.getConfiguration()

        assertEquals(result.first(), configurationTestData)
    }

    @Test
    fun getCertificationTest() = runTest {
        val result = repository.getCertification()

        assertEquals(result.first(), certificationTestData)
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