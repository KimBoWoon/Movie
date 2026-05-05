package com.cheeke.surfy.data.repository

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.testing.TestPager
import com.cheeke.surfy.data.testdouble.TestMovieDao
import com.cheeke.surfy.database.model.MovieEntity
import com.cheeke.surfy.database.model.NowPlayingMovieEntity
import com.cheeke.surfy.database.model.asExternalModel
import com.cheeke.surfy.testing.utils.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import kotlin.test.assertEquals

class MovieDatabaseRepositoryTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private val movieDao = TestMovieDao()
    private val repository = MovieDataBaseRepositoryImpl(movieDao = movieDao)
    private val movie = MovieEntity(id = 0, title = "title_0", posterPath = "posterPath_0", releaseDate = "releaseDate", timestamp = 0)

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
            expected = movieDao.isFavoriteMovie(id = 0).first(),
            actual = false
        )

        movieDao.insertOrIgnoreMovies(movie = movie)

        assertEquals(
            expected = movieDao.isFavoriteMovie(id = 0).first(),
            actual = true
        )
    }

    @Test
    fun insert() = runTest {
        assertEquals(
            expected = movieDao.getMovieEntities().first(),
            actual = emptyList()
        )

        movieDao.insertOrIgnoreMovies(movie = movie)

        assertEquals(
            expected = movieDao.getMovieEntities().first(),
            actual = listOf(movie)
        )
    }

    @Test
    fun delete() = runTest {
        assertEquals(
            expected = movieDao.getMovieEntities().first(),
            actual = emptyList()
        )

        movieDao.insertOrIgnoreMovies(movie = movie)

        assertEquals(
            expected = movieDao.getMovieEntities().first(),
            actual = listOf(movie)
        )

        movieDao.deleteMovie(id = 0)

        assertEquals(
            expected = movieDao.getMovieEntities().first(),
            actual = emptyList()
        )
    }

    @Test
    fun upsert() = runTest {
        assertEquals(
            expected = movieDao.getMovieEntities().first(),
            actual = emptyList()
        )

        movieDao.upsertMovies(entities = listOf(movie))

        assertEquals(
            expected = movieDao.getMovieEntities().first(),
            actual = listOf(movie)
        )
    }

    @Test
    fun getUpComingMovies() = runTest {
        val favoriteMovieSource = repository.getUpComingMovies()
        val favoriteMoviePager = TestPager(
            config = PagingConfig(pageSize = 0, initialLoadSize = 20, prefetchDistance = 5),
            pagingSource = repository.getUpComingMovies()
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
    fun getNowPlayingMovies() = runTest {
        val favoriteMovieSource = repository.getNowPlayingMovies()
        val favoriteMoviePager = TestPager(
            config = PagingConfig(pageSize = 0, initialLoadSize = 20, prefetchDistance = 5),
            pagingSource = repository.getNowPlayingMovies()
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
    fun getPopularMoviesTest() = runTest {
        val movie = NowPlayingMovieEntity(
            id = 4,
            posterPath = "/Movie_4.png",
            title = "movie_4",
            releaseDate = LocalDate.now().toString(),
            voteAverage = 8.6f,
            voteCount = 673
        )

        assertEquals(
            expected = repository.getPopularMovies(),
            actual = emptyList()
        )

        movieDao.upsertNowPlayingMovie(entities = listOf(movie))

        assertEquals(
            expected = repository.getPopularMovies(),
            actual = listOf(movie.asExternalModel())
        )
    }

    @Test
    fun getNextWeekReleaseMovies() = runTest {
        val movie1 = MovieEntity(id = 0, releaseDate = LocalDate.now().plusDays(1).toString(), posterPath = "", timestamp = 0L, title = "")
        val movie2 = MovieEntity(id = 1, releaseDate = LocalDate.now().minusDays(1).toString(), posterPath = "", timestamp = 0L, title = "")
        val movie3 = MovieEntity(id = 2, releaseDate = LocalDate.now().plusDays(2).toString(), posterPath = "", timestamp = 0L, title = "")

        movieDao.insertOrIgnoreMovies(movie = movie1)
        movieDao.insertOrIgnoreMovies(movie = movie2)
        movieDao.insertOrIgnoreMovies(movie = movie3)

        assertEquals(
            expected = movieDao.getNextWeekReleaseMovies(),
            actual = listOf(movie1, movie3)
        )
    }
}