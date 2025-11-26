package com.bowoon.database

import com.bowoon.database.model.PeopleEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.threeten.bp.Instant
import kotlin.test.assertEquals

internal class PeopleDaoTest : DatabaseTest() {
    @Test
    fun getMovieTest() = runTest {
        val favoritePeoples = listOf(
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

        peopleDao.upsertPeoples(favoritePeoples)

        assertEquals(
            peopleDao.getPeopleEntities().first(),
            favoritePeoples
        )
    }

    @Test
    fun deleteMovieTest() = runTest {
        val favoritePeoples = listOf(
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

        peopleDao.upsertPeoples(favoritePeoples)

        assertEquals(
            peopleDao.getPeopleEntities().first(),
            favoritePeoples
        )

        peopleDao.deletePeople(2)

        assertEquals(
            peopleDao.getPeopleEntities().first(),
            favoritePeoples.filter { it.id != 2 }
        )
    }

    @Test
    fun insertOrIgnoreTest() = runTest {
        val favoritePeoples = listOf(
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
        val people = PeopleEntity(
            id = 4,
            timestamp = Instant.now().epochSecond,
            name = "people_4",
            profilePath = "/People_4.png"
        )

        peopleDao.upsertPeoples(favoritePeoples)

        assertEquals(
            peopleDao.getPeopleEntities().first(),
            favoritePeoples
        )

        peopleDao.insertOrIgnorePeoples(
            PeopleEntity(
                id = 3,
                timestamp = Instant.now().epochSecond,
                name = "people_4",
                profilePath = "/People_4.png"
            )
        )

        assertEquals(
            peopleDao.getPeopleEntities().first(),
            favoritePeoples
        )

        peopleDao.insertOrIgnorePeoples(people)

        assertEquals(
            peopleDao.getPeopleEntities().first(),
            favoritePeoples + people
        )
    }
}