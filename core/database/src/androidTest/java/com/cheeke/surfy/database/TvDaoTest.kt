package com.cheeke.surfy.database

import androidx.paging.PagingSource
import com.cheeke.surfy.database.model.TvEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import kotlin.test.assertEquals

internal class TvDaoTest : DatabaseTest() {
    val favoriteTvs = listOf(
        TvEntity(
            id = 1,
            timestamp = Instant.now().epochSecond,
            posterPath = "/Movie_1.png",
            name = "tv_1",
            firstAirDate = "2025-05-23",
            lastAirDate = "2026-07-29"
        ),
        TvEntity(
            id = 2,
            timestamp = Instant.now().epochSecond,
            posterPath = "/Tv_1.png",
            name = "tv_2",
            firstAirDate = "2025-05-23",
            lastAirDate = "2026-07-29"
        ),
        TvEntity(
            id = 3,
            timestamp = Instant.now().epochSecond,
            posterPath = "/Tv_3.png",
            name = "tv_3",
            firstAirDate = "2025-05-23",
            lastAirDate = "2026-07-29"
        )
    )

    @Test
    fun getTvTest() = runTest {
        val emptyResult = tvDao.getFavoriteTv().load(
            params = PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        ) as PagingSource.LoadResult.Page

        assertEquals(expected = emptyResult.data.isEmpty(), actual = true)

        tvDao.upsertTvs(entities = favoriteTvs)

        val result = tvDao.getFavoriteTv().load(
            params = PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        ) as PagingSource.LoadResult.Page

        assertEquals(expected = favoriteTvs, actual = result.data)
    }

    @Test
    fun deleteTvTest() = runTest {
        tvDao.upsertTvs(entities = favoriteTvs)

        val favoritePagerBeforeDelete = tvDao.getFavoriteTv().load(
            params = PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        ) as PagingSource.LoadResult.Page

        assertEquals(expected = favoriteTvs, actual = favoritePagerBeforeDelete.data)

        tvDao.deleteTv(id = favoriteTvs.first().id)

        val favoritePagerAfterDelete = tvDao.getFavoriteTv().load(
            params = PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        ) as PagingSource.LoadResult.Page

        assertEquals(expected = favoriteTvs.filter { it.id != favoriteTvs.first().id }, actual = favoritePagerAfterDelete.data)
    }

    @Test
    fun insertOrIgnoreTest() = runTest {
        val tv = TvEntity(
            id = 4,
            timestamp = Instant.now().epochSecond,
            posterPath = "/Movie_4.png",
            name = "movie_4",
            firstAirDate = "2025-01-04",
            lastAirDate = "2026-03-08"
        )

        tvDao.upsertTvs(entities = favoriteTvs)

        val favoritePagerBeforeInsert = tvDao.getFavoriteTv().load(
            params = PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        ) as PagingSource.LoadResult.Page

        assertEquals(expected = favoriteTvs, actual = favoritePagerBeforeInsert.data)

        tvDao.insertOrIgnoreTvs(
            tv = TvEntity(
                id = 3,
                timestamp = Instant.now().epochSecond,
                posterPath = "/Tv_3.png",
                name = "tv_3",
                firstAirDate = "2025-05-23",
                lastAirDate = "2026-07-29"
            )
        )

        val favoritePagerAfterInsert = tvDao.getFavoriteTv().load(
            params = PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        ) as PagingSource.LoadResult.Page

        assertEquals(expected = favoriteTvs, actual = favoritePagerAfterInsert.data)

        tvDao.insertOrIgnoreTvs(tv = tv)

        val favoritePagerAfterInsert2 = tvDao.getFavoriteTv().load(
            params = PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        ) as PagingSource.LoadResult.Page

        assertEquals(expected = favoriteTvs + tv, actual = favoritePagerAfterInsert2.data)
    }

    @Test
    fun isFavoriteTest() = runTest {
        val tv = TvEntity(
            id = 3,
            timestamp = Instant.now().epochSecond,
            posterPath = "/Movie_4.png",
            name = "movie_4",
            firstAirDate = "2025-01-04",
            lastAirDate = "2026-03-08"
        )

        assertEquals(
            expected = tvDao.isFavoriteTv(id = 3).first(),
            actual = false
        )

        tvDao.insertOrIgnoreTvs(tv = tv)

        assertEquals(
            expected = tvDao.isFavoriteTv(id = 3).first(),
            actual = true
        )
    }

    @Test
    fun deleteAllTvs() = runTest {
        val favoritePagerBeforeInsert = tvDao.getFavoriteTv().load(
            params = PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        ) as PagingSource.LoadResult.Page

        assertEquals(expected = true, actual = favoritePagerBeforeInsert.data.isEmpty())

        tvDao.upsertTvs(entities = favoriteTvs)

        val favoritePagerAfterInsert = tvDao.getFavoriteTv().load(
            params = PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        ) as PagingSource.LoadResult.Page

        assertEquals(expected = false, actual = favoritePagerAfterInsert.data.isEmpty())
        assertEquals(expected = favoriteTvs, actual = favoritePagerAfterInsert.data)

        tvDao.deleteAllFavoriteTvs()

        val favoritePagerAfterDelete = tvDao.getFavoriteTv().load(
            params = PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        ) as PagingSource.LoadResult.Page

        assertEquals(expected = true, actual = favoritePagerAfterDelete.data.isEmpty())
    }

    @Test
    fun getNextWeekReleaseTvs() = runTest {
        val tv = favoriteTvs[1].copy(firstAirDate = LocalDate.now().toString())

        assertEquals(
            expected = tvDao.getNextWeekReleaseTvs(),
            actual = emptyList()
        )

        tvDao.insertOrIgnoreTvs(tv = tv)

        assertEquals(
            expected = tvDao.getNextWeekReleaseTvs(),
            actual = listOf(tv)
        )
    }
}