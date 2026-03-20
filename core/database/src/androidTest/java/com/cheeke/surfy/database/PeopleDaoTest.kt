package com.cheeke.surfy.database

import com.cheeke.surfy.database.model.PeopleEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.Instant
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