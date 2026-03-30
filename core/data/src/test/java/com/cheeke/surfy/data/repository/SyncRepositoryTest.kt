package com.cheeke.surfy.data.repository

import com.cheeke.surfy.core.datastore.InternalDataPreferences
import com.cheeke.surfy.data.TestSynchronizer
import com.cheeke.surfy.data.util.Synchronizer
import com.cheeke.surfy.datastore.InternalDataSource
import com.cheeke.surfy.datastore_test.InMemoryDataStore
import com.cheeke.surfy.testing.TestSyncRemoteDataSource
import com.cheeke.surfy.testing.repository.TestSyncRepository
import com.cheeke.surfy.testing.utils.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import kotlin.test.assertEquals

class SyncRepositoryTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var movieApis: TestSyncRemoteDataSource
    private lateinit var datastore: InternalDataSource
    private lateinit var repository: TestSyncRepository
    private lateinit var synchronizer: Synchronizer

    @Before
    fun setup() {
        movieApis = TestSyncRemoteDataSource()
        datastore = InternalDataSource(
            datastore = InMemoryDataStore(initialValue = InternalDataPreferences.getDefaultInstance())
        )
        repository = TestSyncRepository()
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