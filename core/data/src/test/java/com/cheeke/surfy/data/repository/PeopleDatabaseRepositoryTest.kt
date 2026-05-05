package com.cheeke.surfy.data.repository

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.testing.TestPager
import com.cheeke.surfy.data.testdouble.TestPeopleDao
import com.cheeke.surfy.database.model.PeopleEntity
import com.cheeke.surfy.testing.utils.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class PeopleDatabaseRepositoryTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private val peopleDao = TestPeopleDao()
    private val repository = PeopleDataBaseRepositoryImpl(peopleDao = peopleDao)
    private val people = PeopleEntity(id = 0, timestamp = 0L, name = "people_0", profilePath = "profilePath_0")

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
            expected = peopleDao.isFavoritePeople(id = 0).first(),
            actual = false
        )

        peopleDao.insertOrIgnorePeoples(people = people)

        assertEquals(
            expected = peopleDao.isFavoritePeople(id = 0).first(),
            actual = true
        )
    }

    @Test
    fun insert() = runTest {
        assertEquals(
            expected = peopleDao.getPeopleEntities().first(),
            actual = emptyList()
        )

        peopleDao.insertOrIgnorePeoples(people = people)

        assertEquals(
            expected = peopleDao.getPeopleEntities().first(),
            actual = listOf(people)
        )
    }

    @Test
    fun delete() = runTest {
        assertEquals(
            expected = peopleDao.getPeopleEntities().first(),
            actual = emptyList()
        )

        peopleDao.insertOrIgnorePeoples(people = people)

        assertEquals(
            expected = peopleDao.getPeopleEntities().first(),
            actual = listOf(people)
        )

        peopleDao.deletePeople(id = 0)

        assertEquals(
            expected = peopleDao.getPeopleEntities().first(),
            actual = emptyList()
        )
    }

    @Test
    fun upsert() = runTest {
        assertEquals(
            expected = peopleDao.getPeopleEntities().first(),
            actual = emptyList()
        )

        peopleDao.upsertPeoples(entities = listOf(people))

        assertEquals(
            expected = peopleDao.getPeopleEntities().first(),
            actual = listOf(people)
        )
    }
}