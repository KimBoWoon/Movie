package com.cheeke.surfy.database.impl

import androidx.paging.PagingSource
import com.cheeke.surfy.database.impl.model.MovieEntity
import com.cheeke.surfy.database.impl.model.NowPlayingMovieEntity
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
        val emptyResult = movieDao.getFavoriteMovie().load(
            params = PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        ) as PagingSource.LoadResult.Page

        assertEquals(expected = emptyResult.data.isEmpty(), actual = true)

        movieDao.upsertMovies(entities = favoriteMovies)

        val result = movieDao.getFavoriteMovie().load(
            params = PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        ) as PagingSource.LoadResult.Page

        assertEquals(expected = favoriteMovies, actual = result.data)
    }

    @Test
    fun deleteMovieTest() = runTest {
        movieDao.upsertMovies(entities = favoriteMovies)

        val favoritePagerBeforeDelete = movieDao.getFavoriteMovie().load(
            params = PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        ) as PagingSource.LoadResult.Page

        assertEquals(expected = favoriteMovies, actual = favoritePagerBeforeDelete.data)

        movieDao.deleteMovie(id = favoriteMovies.first().id)

        val favoritePagerAfterDelete = movieDao.getFavoriteMovie().load(
            params = PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        ) as PagingSource.LoadResult.Page

        assertEquals(expected = favoriteMovies.filter { it.id != favoriteMovies.first().id }, actual = favoritePagerAfterDelete.data)
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

        val favoritePagerBeforeInsert = movieDao.getFavoriteMovie().load(
            params = PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        ) as PagingSource.LoadResult.Page

        assertEquals(expected = favoriteMovies, actual = favoritePagerBeforeInsert.data)

        movieDao.insertOrIgnoreMovies(
            MovieEntity(
                id = 3,
                timestamp = Instant.now().epochSecond,
                posterPath = "/Movie_4.png",
                title = "movie_4",
                releaseDate = "2025-01-04"
            )
        )

        val favoritePagerAfterInsert = movieDao.getFavoriteMovie().load(
            params = PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        ) as PagingSource.LoadResult.Page

        assertEquals(expected = favoriteMovies, actual = favoritePagerAfterInsert.data)

        movieDao.insertOrIgnoreMovies(movie = movie)

        val favoritePagerAfterInsert2 = movieDao.getFavoriteMovie().load(
            params = PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        ) as PagingSource.LoadResult.Page

        assertEquals(expected = favoriteMovies + movie, actual = favoritePagerAfterInsert2.data)
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
        assertEquals(expected = movieDao.getPopularMovies(), actual = emptyList())
        movieDao.upsertNowPlayingMovie(entities = listOf(movie))
        assertEquals(expected = movieDao.getPopularMovies(), actual = listOf(movie))
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
    fun deleteAllMovies() = runTest {
        val favoritePagerBeforeInsert = movieDao.getFavoriteMovie().load(
            params = PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        ) as PagingSource.LoadResult.Page

        assertEquals(expected = true, actual = favoritePagerBeforeInsert.data.isEmpty())

        movieDao.upsertMovies(entities = favoriteMovies)

        val favoritePagerAfterInsert = movieDao.getFavoriteMovie().load(
            params = PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        ) as PagingSource.LoadResult.Page

        assertEquals(expected = false, actual = favoritePagerAfterInsert.data.isEmpty())
        assertEquals(expected = favoriteMovies, actual = favoritePagerAfterInsert.data)

        movieDao.deleteAllFavoriteMovies()

        val favoritePagerAfterDelete = movieDao.getFavoriteMovie().load(
            params = PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        ) as PagingSource.LoadResult.Page

        assertEquals(expected = true, actual = favoritePagerAfterDelete.data.isEmpty())
    }

    @Test
    fun getNextWeekReleaseMovies() = runTest {
        val movie = favoriteMovies[1].copy(releaseDate = LocalDate.now().toString())

        assertEquals(
            expected = movieDao.getNextWeekReleaseMovies(),
            actual = emptyList()
        )

        movieDao.insertOrIgnoreMovies(movie = movie)

        assertEquals(
            expected = movieDao.getNextWeekReleaseMovies(),
            actual = listOf(movie)
        )
    }
}