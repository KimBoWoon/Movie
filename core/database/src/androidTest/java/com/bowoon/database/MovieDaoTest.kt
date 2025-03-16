package com.bowoon.database

import com.bowoon.database.model.MovieEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.threeten.bp.Instant
import kotlin.test.assertEquals

internal class MovieDaoTest : DatabaseTest() {
    @Test
    fun getMovieTest() = runTest {
        val favoriteMovies = listOf(
            MovieEntity(
                id = 1,
                timestamp = Instant.now().epochSecond,
                posterPath = "/Movie_1.png"
            ),
            MovieEntity(
                id = 2,
                timestamp = Instant.now().epochSecond,
                posterPath = "/Movie_2.png"
            ),
            MovieEntity(
                id = 3,
                timestamp = Instant.now().epochSecond,
                posterPath = "/Movie_3.png"
            )
        )

        movieDao.upsertMovies(favoriteMovies)

        assertEquals(
            movieDao.getMovieEntities().first(),
            favoriteMovies
        )
    }

    @Test
    fun deleteMovieTest() = runTest {
        val favoriteMovies = listOf(
            MovieEntity(
                id = 1,
                timestamp = Instant.now().epochSecond,
                posterPath = "/Movie_1.png"
            ),
            MovieEntity(
                id = 2,
                timestamp = Instant.now().epochSecond,
                posterPath = "/Movie_2.png"
            ),
            MovieEntity(
                id = 3,
                timestamp = Instant.now().epochSecond,
                posterPath = "/Movie_3.png"
            )
        )

        movieDao.upsertMovies(favoriteMovies)

        assertEquals(
            movieDao.getMovieEntities().first(),
            favoriteMovies
        )

        movieDao.deleteMovie(2)

        assertEquals(
            movieDao.getMovieEntities().first(),
            favoriteMovies.filter { it.id != 2 }
        )

        movieDao.deleteMovies(listOf(1, 3))

        assertEquals(
            movieDao.getMovieEntities().first(),
            emptyList()
        )
    }

    @Test
    fun insertOrIgnoreTest() = runTest {
        val favoriteMovies = listOf(
            MovieEntity(
                id = 1,
                timestamp = Instant.now().epochSecond,
                posterPath = "/Movie_1.png"
            ),
            MovieEntity(
                id = 2,
                timestamp = Instant.now().epochSecond,
                posterPath = "/Movie_2.png"
            ),
            MovieEntity(
                id = 3,
                timestamp = Instant.now().epochSecond,
                posterPath = "/Movie_3.png"
            )
        )
        val movie = MovieEntity(
            id = 4,
            timestamp = Instant.now().epochSecond,
            posterPath = "/Movie_4.png"
        )

        movieDao.upsertMovies(favoriteMovies)

        assertEquals(
            movieDao.getMovieEntities().first(),
            favoriteMovies
        )

        movieDao.insertOrIgnoreMovies(
            MovieEntity(
                id = 3,
                timestamp = Instant.now().epochSecond,
                posterPath = "/Movie_4.png"
            )
        )

        assertEquals(
            movieDao.getMovieEntities().first(),
            favoriteMovies
        )

        movieDao.insertOrIgnoreMovies(movie)

        assertEquals(
            movieDao.getMovieEntities().first(),
            favoriteMovies + movie
        )
    }

    @Test
    fun getMovieEntityTest() = runTest {
        val favoriteMovies = listOf(
            MovieEntity(
                id = 1,
                timestamp = Instant.now().epochSecond,
                posterPath = "/Movie_1.png"
            ),
            MovieEntity(
                id = 2,
                timestamp = Instant.now().epochSecond,
                posterPath = "/Movie_2.png"
            ),
            MovieEntity(
                id = 3,
                timestamp = Instant.now().epochSecond,
                posterPath = "/Movie_3.png"
            )
        )

        assertEquals(
            movieDao.getMovieEntities().first(),
            emptyList()
        )

        movieDao.insertOrIgnoreMovies(favoriteMovies[0])

        assertEquals(
            movieDao.getMovieEntities(setOf(1)).first(),
            listOf(favoriteMovies[0])
        )
    }
}