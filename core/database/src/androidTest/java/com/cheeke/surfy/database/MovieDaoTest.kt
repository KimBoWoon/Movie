package com.cheeke.surfy.database

import com.cheeke.surfy.database.model.MovieEntity
import com.cheeke.surfy.database.model.NowPlayingMovieEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import kotlin.test.assertEquals

internal class MovieDaoTest : DatabaseTest() {
    val favoriteMovies = listOf(
        MovieEntity(
            id = 1,
            timestamp = Instant.now().epochSecond,
            posterPath = "/Movie_1.png",
            title = "movie_1",
            releaseDate = "2025-01-01"
        ),
        MovieEntity(
            id = 2,
            timestamp = Instant.now().epochSecond,
            posterPath = "/Movie_2.png",
            title = "movie_2",
            releaseDate = "2025-01-02"
        ),
        MovieEntity(
            id = 3,
            timestamp = Instant.now().epochSecond,
            posterPath = "/Movie_3.png",
            title = "movie_3",
            releaseDate = "2025-01-03"
        )
    )

    @Test
    fun getMovieTest() = runTest {
        assertEquals(
            expected = movieDao.getMovieEntities().first(),
            actual = emptyList()
        )

        movieDao.upsertMovies(entities = favoriteMovies)

        assertEquals(
            expected = movieDao.getMovieEntities().first(),
            actual = favoriteMovies
        )
    }

    @Test
    fun deleteMovieTest() = runTest {
        movieDao.upsertMovies(entities = favoriteMovies)

        assertEquals(
            expected = movieDao.getMovieEntities().first(),
            actual = favoriteMovies
        )

        movieDao.deleteMovie(id = 2)

        assertEquals(
            expected = movieDao.getMovieEntities().first(),
            actual = favoriteMovies.filter { it.id != 2 }
        )
    }

    @Test
    fun insertOrIgnoreTest() = runTest {
        val movie = MovieEntity(
            id = 4,
            timestamp = Instant.now().epochSecond,
            posterPath = "/Movie_4.png",
            title = "movie_4",
            releaseDate = "2025-01-04"
        )

        movieDao.upsertMovies(entities = favoriteMovies)

        assertEquals(
            expected = movieDao.getMovieEntities().first(),
            actual = favoriteMovies
        )

        movieDao.insertOrIgnoreMovies(
            MovieEntity(
                id = 3,
                timestamp = Instant.now().epochSecond,
                posterPath = "/Movie_4.png",
                title = "movie_4",
                releaseDate = "2025-01-04"
            )
        )

        assertEquals(
            expected = movieDao.getMovieEntities().first(),
            actual = favoriteMovies
        )

        movieDao.insertOrIgnoreMovies(movie = movie)

        assertEquals(
            expected = movieDao.getMovieEntities().first(),
            actual = favoriteMovies + movie
        )
    }

    @Test
    fun getPopularMoviesTest() = runTest {
        val movie = NowPlayingMovieEntity(
            id = 3,
            posterPath = "/Movie_4.png",
            title = "movie_4",
            releaseDate = "2025-01-04",
            voteAverage = 8.0f,
            voteCount = 864
        )
        assertEquals(expected = movieDao.getPopularMovies().first(), actual = emptyList())
        movieDao.upsertNowPlayingMovie(entities = listOf(movie))
        assertEquals(expected = movieDao.getPopularMovies().first(), actual = listOf(movie))
    }

    @Test
    fun isFavoriteTest() = runTest {
        val movie = MovieEntity(
            id = 3,
            timestamp = Instant.now().epochSecond,
            posterPath = "/Movie_4.png",
            title = "movie_4",
            releaseDate = "2025-04-27"
        )

        assertEquals(
            expected = movieDao.isFavoriteMovie(id = 3).first(),
            actual = false
        )

        movieDao.insertOrIgnoreMovies(movie = movie)

        assertEquals(
            expected = movieDao.isFavoriteMovie(id = 3).first(),
            actual = true
        )
    }

    @Test
    fun deleteAllTvs() = runTest {
        assertEquals(
            expected = movieDao.getMovieEntities().first(),
            actual = emptyList()
        )

        movieDao.upsertMovies(entities = favoriteMovies)

        assertEquals(
            expected = movieDao.getMovieEntities().first(),
            actual = favoriteMovies
        )

        movieDao.deleteAllFavoriteMovies()

        assertEquals(
            expected = movieDao.getMovieEntities().first(),
            actual = emptyList()
        )
    }

    @Test
    fun getNextWeekReleaseTvs() = runTest {
        val movie = favoriteMovies[1].copy(releaseDate = LocalDate.now().toString())

        assertEquals(
            expected = movieDao.getNextWeekReleaseMovies().first(),
            actual = emptyList()
        )

        movieDao.insertOrIgnoreMovies(movie = movie)

        assertEquals(
            expected = movieDao.getNextWeekReleaseMovies().first(),
            actual = listOf(movie)
        )
    }
}