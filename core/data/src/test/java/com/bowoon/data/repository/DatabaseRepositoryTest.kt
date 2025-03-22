package com.bowoon.data.repository

import com.bowoon.data.testdouble.TestMovieDao
import com.bowoon.data.testdouble.TestPeopleDao
import com.bowoon.model.Favorite
import com.bowoon.testing.utils.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class DatabaseRepositoryTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private val repository = DatabaseRepositoryImpl(
        movieDao = TestMovieDao(),
        peopleDao = TestPeopleDao()
    )

    @Test
    fun insertMovieTest() = runTest {
        val movie = Favorite(
            id = 1,
            imagePath = "/Movie_1.png"
        )

        assertEquals(
            repository.getMovies().first(),
            emptyList()
        )

        repository.insertMovie(movie)

        assertEquals(
            repository.getMovies().first(),
            listOf(movie)
        )
    }

    @Test
    fun deleteMovieTest() = runTest {
        val movie = Favorite(
            id = 1,
            imagePath = "/Movie_1.png"
        )

        assertEquals(
            repository.getMovies().first(),
            emptyList()
        )

        repository.insertMovie(movie)

        assertEquals(
            repository.getMovies().first(),
            listOf(movie)
        )

        repository.deleteMovie(movie)

        assertEquals(
            repository.getMovies().first(),
            emptyList()
        )
    }

    @Test
    fun upsertMoviesTest() = runTest {
        val movies = listOf(
            Favorite(
                id = 1,
                imagePath = "/Movie_1.png"
            ),
            Favorite(
                id = 2,
                imagePath = "/Movie_2.png"
            ),
            Favorite(
                id = 3,
                imagePath = "/Movie_3.png"
            )
        )
        val newMovies = listOf(
            Favorite(
                id = 2,
                imagePath = "/new_movie_2.png"
            ),
            Favorite(
                id = 4,
                imagePath = "/Movie_4.png"
            )
        )

        assertEquals(
            repository.getMovies().first(),
            emptyList()
        )

        movies.forEach {
            repository.insertMovie(it)
        }

        assertEquals(
            repository.getMovies().first().sortedBy { it.id },
            movies
        )

        repository.upsertMovies(newMovies)

        assertEquals(
            repository.getMovies().first().sortedBy { it.id },
            listOf(
                Favorite(
                    id = 1,
                    imagePath = "/Movie_1.png"
                ),
                Favorite(
                    id = 2,
                    imagePath = "/new_movie_2.png"
                ),
                Favorite(
                    id = 3,
                    imagePath = "/Movie_3.png"
                ),
                Favorite(
                    id = 4,
                    imagePath = "/Movie_4.png"
                )
            )
        )
    }
}