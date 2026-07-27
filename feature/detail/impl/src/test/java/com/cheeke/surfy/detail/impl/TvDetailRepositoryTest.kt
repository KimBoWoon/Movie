package com.cheeke.surfy.detail.impl

import com.cheeke.surfy.core.datastore.InternalDataPreferences
import com.cheeke.surfy.datastore.InternalDataSource
import com.cheeke.surfy.datastore_test.InMemoryDataStore
import com.cheeke.surfy.detail.api.DetailRequestOptionsProvider
import com.cheeke.surfy.detail.impl.tv.TvDetailRepositoryImpl
import com.cheeke.surfy.testing.TestTvRemoteDataSource
import com.cheeke.surfy.testing.model.tvEpisodeTestData
import com.cheeke.surfy.testing.model.tvSeasonTestData
import com.cheeke.surfy.testing.model.tvTestData
import com.cheeke.surfy.testing.utils.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class TvDetailRepositoryTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var tvApis: TestTvRemoteDataSource
    private lateinit var datastore: InternalDataSource
    private lateinit var repository: TvDetailRepositoryImpl

    @Before
    fun setup() {
        tvApis = TestTvRemoteDataSource()
        datastore = InternalDataSource(
            datastore = InMemoryDataStore(initialValue = InternalDataPreferences.getDefaultInstance())
        )
        repository = TvDetailRepositoryImpl(
            apis = tvApis,
            requestOptionsProvider = DetailRequestOptionsProvider(datastore = datastore),
        )
    }

    @Test
    fun getTvDetailTest() = runTest {
        val result = repository.getData(id = 0)

        assertEquals(expected = result.first(), actual = tvTestData)
    }

    @Test
    fun getTvSeasonTest() = runTest {
        val result = repository.getTvSeasons(seriesId = 0, seasonNumber = 1)

        assertEquals(expected = result.first(), actual = tvSeasonTestData)
    }

    @Test
    fun getTvEpisodeTest() = runTest {
        val result = repository.getTvEpisode(seriesId = 0, seasonNumber = 1, episodeNumber = 1)

        assertEquals(expected = result.first(), actual = tvEpisodeTestData)
    }
}