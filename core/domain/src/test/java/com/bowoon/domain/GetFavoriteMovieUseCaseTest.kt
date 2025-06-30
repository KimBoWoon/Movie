package com.bowoon.domain

import com.bowoon.model.Favorite
import com.bowoon.model.MovieAppData
import com.bowoon.testing.repository.TestDatabaseRepository
import com.bowoon.testing.repository.TestMovieAppDataRepository
import com.bowoon.testing.utils.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class GetFavoriteMovieUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var getMovieFavoriteMovieListUseCase: GetFavoriteMovieListUseCase
    private lateinit var testDatabaseRepository: TestDatabaseRepository
    private lateinit var testMovieAppDataRepository: TestMovieAppDataRepository
    private val testMovie = Favorite(id = 0, title = "movie_0", imagePath = "/asdf.png")

    @Before
    fun setup() {
        testDatabaseRepository = TestDatabaseRepository()
        testMovieAppDataRepository = TestMovieAppDataRepository()
        getMovieFavoriteMovieListUseCase = GetFavoriteMovieListUseCase(
            databaseRepository = testDatabaseRepository,
            movieAppDataRepository = testMovieAppDataRepository
        )

        runBlocking {
            testMovieAppDataRepository.setMovieAppData(MovieAppData())
        }
    }

    @Test
    fun getFavoriteMovieList() = runTest {
        testDatabaseRepository.movieDatabase.emit(emptyList())

        assertEquals(
            getMovieFavoriteMovieListUseCase().first(),
            emptyList()
        )

        testDatabaseRepository.insertMovie(testMovie)

        assertEquals(
            getMovieFavoriteMovieListUseCase().first(),
            listOf(
                testMovie.copy(imagePath = "${testMovieAppDataRepository.movieAppData.value.getImageUrl()}${testMovie.imagePath}")
            )
        )
    }
}