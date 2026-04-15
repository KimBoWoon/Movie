package com.cheeke.surfy.database

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
            lastAirDate = "2026-07-29",
        ),
        TvEntity(
            id = 2,
            timestamp = Instant.now().epochSecond,
            posterPath = "/Tv_1.png",
            name = "tv_2",
            firstAirDate = "2025-05-23",
            lastAirDate = "2026-07-29",
        ),
        TvEntity(
            id = 3,
            timestamp = Instant.now().epochSecond,
            posterPath = "/Tv_3.png",
            name = "tv_3",
            firstAirDate = "2025-05-23",
            lastAirDate = "2026-07-29",
        )
    )

    @Test
    fun getTvTest() = runTest {
        assertEquals(
            expected = tvDao.getTvEntities().first(),
            actual = emptyList()
        )

        tvDao.upsertTvs(entities = favoriteTvs)

        assertEquals(
            expected = tvDao.getTvEntities().first(),
            actual = favoriteTvs
        )
    }

    @Test
    fun deleteTvTest() = runTest {
        tvDao.upsertTvs(entities = favoriteTvs)

        assertEquals(
            expected = tvDao.getTvEntities().first(),
            actual = favoriteTvs
        )

        tvDao.deleteTv(id = 2)

        assertEquals(
            expected = tvDao.getTvEntities().first(),
            actual = favoriteTvs.filter { it.id != 2 }
        )
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

        assertEquals(
            expected = tvDao.getTvEntities().first(),
            actual = favoriteTvs
        )

        tvDao.insertOrIgnoreTvs(
            tv = TvEntity(
                id = 3,
                timestamp = Instant.now().epochSecond,
                posterPath = "/Movie_4.png",
                name = "movie_4",
                firstAirDate = "2025-01-04",
                lastAirDate = "2026-03-08"
            )
        )

        assertEquals(
            expected = tvDao.getTvEntities().first(),
            actual = favoriteTvs
        )

        tvDao.insertOrIgnoreTvs(tv = tv)

        assertEquals(
            expected = tvDao.getTvEntities().first(),
            actual = favoriteTvs + tv
        )
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
        assertEquals(
            expected = tvDao.getTvEntities().first(),
            actual = emptyList()
        )

        tvDao.upsertTvs(entities = favoriteTvs)

        assertEquals(
            expected = tvDao.getTvEntities().first(),
            actual = favoriteTvs
        )

        tvDao.deleteAllFavoriteTvs()

        assertEquals(
            expected = tvDao.getTvEntities().first(),
            actual = emptyList()
        )
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