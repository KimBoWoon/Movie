package com.cheeke.surfy.data.repository

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.testing.TestPager
import com.cheeke.surfy.data.testdouble.TestTvDao
import com.cheeke.surfy.database.model.TvEntity
import com.cheeke.surfy.testing.utils.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import kotlin.test.assertEquals

class TvDataBaseRepositoryTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private val tvDao = TestTvDao()
    private val repository = TvDataBaseRepositoryImpl(tvDao = tvDao)
    private val tv = TvEntity(id = 0, posterPath = "posterPath_0", timestamp = 0L, name = "tv_0", firstAirDate = LocalDate.now().plusDays(1).toString(), lastAirDate = "lastAirDate_0")

    @Test
    fun getFavoriteTest() = runTest {
        val favoriteMovieSource = repository.getFavorite()
        val favoriteMoviePager = TestPager(
            config = PagingConfig(pageSize = 0, initialLoadSize = 20, prefetchDistance = 5),
            pagingSource = repository.getFavorite()
        )

        assertEquals(
            expected = favoriteMoviePager.refresh(initialKey = 0),
            actual = favoriteMovieSource.load(
                params = PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = 20,
                    placeholdersEnabled = false
                )
            )
        )
    }

    @Test
    fun isFavorite() = runTest {
        assertEquals(
            expected = tvDao.isFavoriteTv(id = 0).first(),
            actual = false
        )

        tvDao.insertOrIgnoreTvs(tv = tv)

        assertEquals(
            expected = tvDao.isFavoriteTv(id = 0).first(),
            actual = true
        )
    }

    @Test
    fun insert() = runTest {
        val favoritePagerBeforeInsert = TestPager(
            config = PagingConfig(pageSize = 0, initialLoadSize = 20, prefetchDistance = 5),
            pagingSource = repository.getFavorite()
        )

        var result = favoritePagerBeforeInsert.refresh() as PagingSource.LoadResult.Page

        assertEquals(expected = result.data.isEmpty(), actual = true)

        tvDao.insertOrIgnoreTvs(tv = tv)

        val favoritePagerAfterInsert = TestPager(
            config = PagingConfig(pageSize = 0, initialLoadSize = 20, prefetchDistance = 5),
            pagingSource = repository.getFavorite()
        )

        result = favoritePagerAfterInsert.refresh() as PagingSource.LoadResult.Page

        assertEquals(expected = result.data.isEmpty(), actual = false)
        assertEquals(expected = tv, actual = result.data.first())
    }

    @Test
    fun delete() = runTest {
        val favoritePagerBeforeInsert = TestPager(
            config = PagingConfig(pageSize = 0, initialLoadSize = 20, prefetchDistance = 5),
            pagingSource = repository.getFavorite()
        )

        var result = favoritePagerBeforeInsert.refresh() as PagingSource.LoadResult.Page

        assertEquals(expected = result.data.isEmpty(), actual = true)

        tvDao.insertOrIgnoreTvs(tv = tv)

        val favoritePagerAfterInsert = TestPager(
            config = PagingConfig(pageSize = 0, initialLoadSize = 20, prefetchDistance = 5),
            pagingSource = repository.getFavorite()
        )

        result = favoritePagerAfterInsert.refresh() as PagingSource.LoadResult.Page

        assertEquals(expected = result.data.isEmpty(), actual = false)
        assertEquals(expected = tv, actual = result.data.first())

        tvDao.deleteTv(id = tv.id)

        val favoritePagerAfterDelete = TestPager(
            config = PagingConfig(pageSize = 0, initialLoadSize = 20, prefetchDistance = 5),
            pagingSource = repository.getFavorite()
        )

        result = favoritePagerAfterDelete.refresh() as PagingSource.LoadResult.Page

        assertEquals(expected = result.data.isEmpty(), actual = true)
    }

    @Test
    fun upsert() = runTest {
        val favoritePagerBeforeInsert = TestPager(
            config = PagingConfig(pageSize = 0, initialLoadSize = 20, prefetchDistance = 5),
            pagingSource = repository.getFavorite()
        )

        var result = favoritePagerBeforeInsert.refresh() as PagingSource.LoadResult.Page

        assertEquals(expected = result.data.isEmpty(), actual = true)

        tvDao.upsertTvs(entities = listOf(tv))

        val favoritePagerAfterInsert = TestPager(
            config = PagingConfig(pageSize = 0, initialLoadSize = 20, prefetchDistance = 5),
            pagingSource = repository.getFavorite()
        )

        result = favoritePagerAfterInsert.refresh() as PagingSource.LoadResult.Page

        assertEquals(expected = result.data.isEmpty(), actual = false)
        assertEquals(expected = tv, actual = result.data.first())
    }

    @Test
    fun getNextWeekReleaseTvs() = runTest {
        val tv1 = TvEntity(id = 0, posterPath = "posterPath_0", timestamp = 0L, name = "tv_0", firstAirDate = LocalDate.now().plusDays(1).toString(), lastAirDate = "lastAirDate_0")
        val tv2 = TvEntity(id = 1, posterPath = "posterPath_1", timestamp = 0L, name = "tv_1", firstAirDate = LocalDate.now().minusDays(1).toString(), lastAirDate = "lastAirDate_1")
        val tv3 = TvEntity(id = 2, posterPath = "posterPath_2", timestamp = 0L, name = "tv_2", firstAirDate = LocalDate.now().plusDays(2).toString(), lastAirDate = "lastAirDate_2")

        tvDao.insertOrIgnoreTvs(tv = tv1)
        tvDao.insertOrIgnoreTvs(tv = tv2)
        tvDao.insertOrIgnoreTvs(tv = tv3)

        assertEquals(
            expected = tvDao.getNextWeekReleaseTvs(),
            actual = listOf(tv1, tv3)
        )
    }
}