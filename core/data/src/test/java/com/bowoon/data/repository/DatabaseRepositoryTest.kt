package com.bowoon.data.repository

import com.bowoon.data.testdouble.TestMovieDao
import com.bowoon.data.testdouble.TestPeopleDao
import com.bowoon.model.Movie
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
        val movie = Movie(
            id = 1,
            posterPath = "/Movie_1.png",
            title = "movie_1",
            releaseDate = "2025-01-01"
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
        val movie = Movie(
            id = 1,
            posterPath = "/Movie_1.png",
            title = "movie_1",
            releaseDate = "2025-01-01"
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
            Movie(
                id = 1,
                posterPath = "/Movie_1.png",
                title = "movie_1",
                releaseDate = "2025-01-01"
            ),
            Movie(
                id = 2,
                posterPath = "/Movie_2.png",
                title = "movie_2",
                releaseDate = "2025-01-02"
            ),
            Movie(
                id = 3,
                posterPath = "/Movie_3.png",
                title = "movie_3",
                releaseDate = "2025-01-03"
            )
        )
        val newMovies = listOf(
            Movie(
                id = 2,
                posterPath = "/new_movie_2.png",
                title = "new_movie_2",
                releaseDate = "2025-01-02"
            ),
            Movie(
                id = 4,
                posterPath = "/Movie_4.png",
                title = "movie_4",
                releaseDate = "2025-01-04"
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
                Movie(
                    id = 1,
                    posterPath = "/Movie_1.png",
                    title = "movie_1",
                    releaseDate = "2025-01-01"
                ),
                Movie(
                    id = 2,
                    posterPath = "/new_movie_2.png",
                    title = "new_movie_2",
                    releaseDate = "2025-01-02"
                ),
                Movie(
                    id = 3,
                    posterPath = "/Movie_3.png",
                    title = "movie_3",
                    releaseDate = "2025-01-03"
                ),
                Movie(
                    id = 4,
                    posterPath = "/Movie_4.png",
                    title = "movie_4",
                    releaseDate = "2025-01-04"
                )
            )
        )
    }
}