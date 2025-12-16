package com.bowoon.data.repository

import androidx.datastore.preferences.core.preferencesOf
import com.bowoon.data.TestSynchronizer
import com.bowoon.data.util.Synchronizer
import com.bowoon.datastore.InternalDataSource
import com.bowoon.datastore_test.InMemoryDataStore
import com.bowoon.testing.TestMovieDataSource
import com.bowoon.testing.repository.TestMainMenuRepository
import com.bowoon.testing.utils.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import kotlin.test.assertEquals

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
            datastore = InMemoryDataStore(initialValue = preferencesOf())
        )
        repository = TestMainMenuRepository()
        synchronizer = TestSynchronizer(datastore)
    }

    @Test
    fun syncNotNecessaryTest() = runTest {
        assertEquals(expected = false, actual = repository.syncWith(synchronizer))
        repository.setDate(LocalDate.now())
        assertEquals(expected = false, actual = repository.syncWith(synchronizer))
    }

    @Test
    fun syncTest() = runTest {
        assertEquals(expected = false, actual = repository.syncWith(synchronizer))
        repository.setDate(LocalDate.now().minusDays(3))
        assertEquals(expected = true, actual = repository.syncWith(synchronizer))
    }

    @Test
    fun forceSyncTest() = runTest {
        assertEquals(expected = false, actual = repository.syncWith(synchronizer))
        repository.setDate(LocalDate.now())
        repository.setIsForce(value = true)
        assertEquals(expected = true, actual = repository.syncWith(synchronizer))
    }
}