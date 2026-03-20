package com.cheeke.surfy.data.repository

import com.cheeke.surfy.data.testdouble.TestMovieDao
import com.cheeke.surfy.data.testdouble.TestPeopleDao
import com.cheeke.surfy.data.testdouble.TestTvDao
import com.cheeke.surfy.database.model.NowPlayingMovieEntity
import com.cheeke.surfy.database.model.asExternalModel
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.model.People
import com.cheeke.surfy.testing.utils.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import kotlin.test.assertEquals

class DatabaseRepositoryTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private val movieDao = TestMovieDao()
    private val peopleDao = TestPeopleDao()
    private val tvDao = TestTvDao()
    private val repository = DatabaseRepositoryImpl(
        movieDao = movieDao,
        peopleDao = peopleDao,
        tvDao = tvDao
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
            expected = repository.getMovies().first(),
            actual = emptyList()
        )

        repository.insertMovie(movie)

        assertEquals(
            expected = repository.getMovies().first(),
            actual = listOf(movie)
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
            expected = repository.getMovies().first(),
            actual = emptyList()
        )

        repository.insertMovie(movie)

        assertEquals(
            expected = repository.getMovies().first(),
            actual = listOf(movie)
        )

        repository.deleteMovie(movie)

        assertEquals(
            expected = repository.getMovies().first(),
            actual = emptyList()
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
            expected = repository.getMovies().first(),
            actual = emptyList()
        )

        movies.forEach {
            repository.insertMovie(it)
        }

        assertEquals(
            expected = repository.getMovies().first().sortedBy { it.id },
            actual = movies
        )

        repository.upsertMovies(newMovies)

        assertEquals(
            expected = repository.getMovies().first().sortedBy { it.id },
            actual = listOf(
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

    @Test
    fun getMoviesTest() = runTest {
        val movie = Movie(
            id = 4,
            posterPath = "/Movie_4.png",
            title = "movie_4",
            releaseDate = "2025-01-04"
        )

        assertEquals(
            expected = repository.getMovies().first(),
            actual = emptyList()
        )

        repository.insertMovie(movie = movie)

        assertEquals(
            expected = repository.getMovies().first(),
            actual = listOf(movie)
        )
    }

    @Test
    fun isFavoriteMovieTest() = runTest {
        val movie = Movie(
            id = 4,
            posterPath = "/Movie_4.png",
            title = "movie_4",
            releaseDate = "2025-01-04"
        )

        assertEquals(
            expected = repository.isFavoriteMovie(id = 4).first(),
            actual = false
        )

        repository.insertMovie(movie = movie)

        assertEquals(
            expected = repository.isFavoriteMovie(id = 4).first(),
            actual = true
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
            expected = repository.getPopularMovies().first(),
            actual = emptyList()
        )

        movieDao.upsertNowPlayingMovie(entities = listOf(movie))

        assertEquals(
            expected = repository.getPopularMovies().first(),
            actual = listOf(movie.asExternalModel())
        )
    }

    @Test
    fun getPeoplesTest() = runTest {
        val people = People(
            id = 4,
            posterPath = "/People_4.png",
            title = "people_4"
        )

        assertEquals(
            expected = repository.getPeople().first(),
            actual = emptyList()
        )

        repository.insertPeople(people = people)

        assertEquals(
            expected = repository.getPeople().first(),
            actual = listOf(people)
        )
    }

    @Test
    fun isFavoritePeopleTest() = runTest {
        val people = People(
            id = 4,
            posterPath = "/People_4.png",
            title = "people_4"
        )

        assertEquals(
            expected = repository.isFavoritePeople(id = 4).first(),
            actual = false
        )

        repository.insertPeople(people = people)

        assertEquals(
            expected = repository.isFavoritePeople(id = 4).first(),
            actual = true
        )
    }

    @Test
    fun getPeopleTest() = runTest {
        val people = People(
            id = 4,
            posterPath = "/People_4.png",
            title = "people_4"
        )

        assertEquals(
            expected = repository.getPeople().first(),
            actual = emptyList()
        )

        repository.insertPeople(people = people)

        assertEquals(
            expected = repository.getPeople().first(),
            actual = listOf(people)
        )
    }

    @Test
    fun deletePeopleTest() = runTest {
        val people = People(
            id = 4,
            posterPath = "/People_4.png",
            title = "people_4"
        )

        assertEquals(
            expected = repository.getPeople().first(),
            actual = emptyList()
        )

        repository.insertPeople(people = people)

        assertEquals(
            expected = repository.getPeople().first(),
            actual = listOf(people)
        )

        repository.deletePeople(people = people)

        assertEquals(
            expected = repository.getPeople().first(),
            actual = emptyList()
        )
    }

    @Test
    fun upsertPeopleTest() = runTest {
        val peoples = listOf(
            People(
                id = 1,
                posterPath = "/People_1.png",
                title = "people_1",
            ),
            People(
                id = 2,
                posterPath = "/People_2.png",
                title = "people_2",
            ),
            People(
                id = 3,
                posterPath = "/People_3.png",
                title = "people_3",
            )
        )
        val newPeoples = listOf(
            People(
                id = 2,
                posterPath = "/new_people_2.png",
                title = "new_people_2",
            ),
            People(
                id = 4,
                posterPath = "/People_4.png",
                title = "people_4",
            )
        )

        assertEquals(
            expected = repository.getPeople().first(),
            actual = emptyList()
        )

        peoples.forEach {
            repository.insertPeople(people = it)
        }

        assertEquals(
            expected = repository.getPeople().first().sortedBy { it.id },
            actual = peoples
        )

        repository.upsertPeoples(peoples = newPeoples)

        assertEquals(
            expected = repository.getPeople().first().sortedBy { it.id },
            actual = listOf(
                People(
                    id = 1,
                    posterPath = "/People_1.png",
                    title = "people_1",
                ),
                People(
                    id = 2,
                    posterPath = "/new_people_2.png",
                    title = "new_people_2",
                ),
                People(
                    id = 3,
                    posterPath = "/People_3.png",
                    title = "people_3",
                ),
                People(
                    id = 4,
                    posterPath = "/People_4.png",
                    title = "people_4",
                )
            )
        )
    }
}