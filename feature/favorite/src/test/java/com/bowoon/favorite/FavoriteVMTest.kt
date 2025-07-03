package com.bowoon.favorite

import com.bowoon.model.Favorite
import com.bowoon.testing.repository.TestDatabaseRepository
import com.bowoon.testing.repository.TestMovieAppDataRepository
import com.bowoon.testing.utils.MainDispatcherRule
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FavoriteVMTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private val testDatabaseRepository = TestDatabaseRepository()
    private val testMovieAppDataRepository = TestMovieAppDataRepository()
    private lateinit var viewModel: FavoriteVM
    private val movie1 = Favorite(id = 0, title = "movie_1", imagePath = "/movieImagePath_0.png")
    private val movie2 = Favorite(id = 1, title = "movie_2", imagePath = "/movieImagePath_1.png")
    private val people1 = Favorite(id = 0, title = "people_1", imagePath = "/peopleImagePath_0.png")
    private val people2 = Favorite(id = 1, title = "people_2", imagePath = "/peopleImagePath_1.png")

    @Before
    fun setup() {
        viewModel = FavoriteVM(
            databaseRepository = testDatabaseRepository
        )
        runBlocking {
            testDatabaseRepository.insertMovie(movie1)
            testDatabaseRepository.insertMovie(movie2)
            testDatabaseRepository.insertPeople(people1)
            testDatabaseRepository.insertPeople(people2)
        }
    }

    @Test
    fun favoriteMovieLoadingTest() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.favoriteMovies.collect() }
        assertEquals(
            viewModel.favoriteMovies.value,
            listOf(movie1, movie2)
        )
    }

    @Test
    fun favoritePeopleLoadingTest() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.favoritePeoples.collect() }

        assertEquals(
            viewModel.favoritePeoples.value,
            listOf(people1, people2)
        )
    }

    @Test
    fun deleteMovieTest() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.favoriteMovies.collect() }

        viewModel.deleteMovie(movie1)

        assertEquals(
            viewModel.favoriteMovies.value,
            listOf(movie2)
        )
    }

    @Test
    fun deletePeopleTest() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.favoritePeoples.collect() }

        viewModel.deletePeople(people1)

        assertEquals(
            viewModel.favoritePeoples.value,
            listOf(people2)
        )
    }
}