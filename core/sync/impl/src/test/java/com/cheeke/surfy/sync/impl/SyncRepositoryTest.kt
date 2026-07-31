package com.cheeke.surfy.sync.impl

import com.cheeke.surfy.network.api.TestSyncRemoteDataSource
import com.cheeke.surfy.sync.api.Synchronizer
import com.cheeke.surfy.sync.api.TestSyncRepository
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
    private lateinit var repository: TestSyncRepository
    private lateinit var synchronizer: Synchronizer

    @Before
    fun setup() {
        movieApis = TestSyncRemoteDataSource()
        repository = TestSyncRepository()
        synchronizer = TestSynchronizer()
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