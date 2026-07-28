package com.cheeke.surfy.database.impl

import androidx.paging.PagingSource
import com.cheeke.surfy.database.model.PeopleEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.Instant
import kotlin.test.assertEquals

internal class PeopleDaoTest : DatabaseTest() {
    private val favoritePeoples = listOf(
        PeopleEntity(
            id = 1,
            timestamp = Instant.now().epochSecond,
            name = "people_1",
            profilePath = "/People_1.png"
        ),
        PeopleEntity(
            id = 2,
            timestamp = Instant.now().epochSecond,
            name = "people_2",
            profilePath = "/People_2.png"
        ),
        PeopleEntity(
            id = 3,
            timestamp = Instant.now().epochSecond,
            name = "people_3",
            profilePath = "/People_3.png"
        )
    )

    @Test
    fun getPeopleTest() = runTest {
        val emptyResult = peopleDao.getFavoritePeople().load(
            params = PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        ) as PagingSource.LoadResult.Page

        assertEquals(expected = emptyResult.data.isEmpty(), actual = true)

        peopleDao.upsertPeoples(entities = favoritePeoples)

        val result = peopleDao.getFavoritePeople().load(
            params = PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        ) as PagingSource.LoadResult.Page

        assertEquals(expected = favoritePeoples, actual = result.data)
    }

    @Test
    fun deletePeopleTest() = runTest {
        peopleDao.upsertPeoples(entities = favoritePeoples)

        val favoritePagerBeforeDelete = peopleDao.getFavoritePeople().load(
            params = PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        ) as PagingSource.LoadResult.Page

        assertEquals(expected = favoritePeoples, actual = favoritePagerBeforeDelete.data)

        peopleDao.deletePeople(id = favoritePeoples.first().id)

        val favoritePagerAfterDelete = peopleDao.getFavoritePeople().load(
            params = PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        ) as PagingSource.LoadResult.Page

        assertEquals(expected = favoritePeoples.filter { it.id != favoritePeoples.first().id }, actual = favoritePagerAfterDelete.data)
    }

    @Test
    fun insertOrIgnoreTest() = runTest {
        val people = PeopleEntity(
            id = 4,
            timestamp = Instant.now().epochSecond,
            name = "people_4",
            profilePath = "/People_4.png"
        )

        peopleDao.upsertPeoples(entities = favoritePeoples)

        val favoritePagerBeforeInsert = peopleDao.getFavoritePeople().load(
            params = PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        ) as PagingSource.LoadResult.Page

        assertEquals(expected = favoritePeoples, actual = favoritePagerBeforeInsert.data)

        peopleDao.insertOrIgnorePeoples(
            people = PeopleEntity(
                id = 3,
                timestamp = Instant.now().epochSecond,
                name = "people_3",
                profilePath = "/People_3.png"
            )
        )

        val favoritePagerAfterInsert = peopleDao.getFavoritePeople().load(
            params = PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        ) as PagingSource.LoadResult.Page

        assertEquals(expected = favoritePeoples, actual = favoritePagerAfterInsert.data)

        peopleDao.insertOrIgnorePeoples(people = people)

        val favoritePagerAfterInsert2 = peopleDao.getFavoritePeople().load(
            params = PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        ) as PagingSource.LoadResult.Page

        assertEquals(expected = favoritePeoples + people, actual = favoritePagerAfterInsert2.data)
    }

    @Test
    fun isFavoriteTest() = runTest {
        val people = PeopleEntity(
            id = 3,
            timestamp = Instant.now().epochSecond,
            profilePath = "/Movie_4.png",
            name = "movie_4"
        )

        assertEquals(
            expected = peopleDao.isFavoritePeople(id = 3).first(),
            actual = false
        )

        peopleDao.insertOrIgnorePeoples(people = people)

        assertEquals(
            expected = peopleDao.isFavoritePeople(id = 3).first(),
            actual = true
        )
    }
}