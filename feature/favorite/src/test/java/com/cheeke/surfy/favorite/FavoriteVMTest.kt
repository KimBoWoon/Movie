package com.cheeke.surfy.favorite

import com.cheeke.surfy.favorite.FavoriteVM
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.model.People
import com.cheeke.surfy.model.Tv
import com.cheeke.surfy.testing.repository.TestDatabaseRepository
import com.cheeke.surfy.testing.utils.MainDispatcherRule
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class FavoriteVMTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private val testDatabaseRepository = TestDatabaseRepository()
    private lateinit var viewModel: FavoriteVM
    private val movie1 = Movie(id = 0, title = "movie_1", posterPath = "/movieImagePath_0.png")
    private val movie2 = Movie(id = 1, title = "movie_2", posterPath = "/movieImagePath_1.png")
    private val people1 = People(id = 0, title = "people_1", posterPath = "/peopleImagePath_0.png")
    private val people2 = People(id = 1, title = "people_2", posterPath = "/peopleImagePath_1.png")
    private val tv1 = Tv(id = 0, title = "tv_1", posterPath = "/tvImagePath_0.png")
    private val tv2 = Tv(id = 1, title = "tv_2", posterPath = "/tvImagePath_1.png")

    @Before
    fun setup() {
        viewModel = FavoriteVM(
            initialTabIndex = 0,
            databaseRepository = testDatabaseRepository
        )
    }

    @Test
    fun changeTabIndexTest() = runTest {
        backgroundScope.launch(context = UnconfinedTestDispatcher()) { viewModel.tabIndex.collect() }

        assertEquals(
            expected = viewModel.tabIndex.value,
            actual = 0
        )

        viewModel.updateTabIndex(index = 1)

        assertEquals(
            expected = viewModel.tabIndex.value,
            actual = 1
        )
    }

    @Test
    fun favoriteMovieLoadingTest() = runTest {
        backgroundScope.launch(context = UnconfinedTestDispatcher()) { viewModel.favoriteMovies.collect() }

        assertEquals(
            expected = viewModel.favoriteMovies.value,
            actual = emptyList()
        )

        testDatabaseRepository.insertMovie(movie = movie1)
        testDatabaseRepository.insertMovie(movie = movie2)

        assertEquals(
            expected = viewModel.favoriteMovies.value,
            actual = listOf(movie1, movie2)
        )
    }

    @Test
    fun favoritePeopleLoadingTest() = runTest {
        backgroundScope.launch(context = UnconfinedTestDispatcher()) { viewModel.favoritePeoples.collect() }

        assertEquals(
            expected = viewModel.favoritePeoples.value,
            actual = emptyList()
        )

        testDatabaseRepository.insertPeople(people = people1)
        testDatabaseRepository.insertPeople(people = people2)

        assertEquals(
            expected = viewModel.favoritePeoples.value,
            actual = listOf(people1, people2)
        )
    }

    @Test
    fun deleteMovieTest() = runTest {
        backgroundScope.launch(context = UnconfinedTestDispatcher()) { viewModel.favoriteMovies.collect() }

        testDatabaseRepository.insertMovie(movie = movie1)
        testDatabaseRepository.insertMovie(movie = movie2)
        viewModel.deleteMovie(movie = movie1)

        assertEquals(
            expected = viewModel.favoriteMovies.value,
            actual = listOf(movie2)
        )
    }

    @Test
    fun deletePeopleTest() = runTest {
        backgroundScope.launch(context = UnconfinedTestDispatcher()) { viewModel.favoritePeoples.collect() }

        testDatabaseRepository.insertPeople(people = people1)
        testDatabaseRepository.insertPeople(people = people2)
        viewModel.deletePeople(people = people1)

        assertEquals(
            expected = viewModel.favoritePeoples.value,
            actual = listOf(people2)
        )
    }

    @Test
    fun deleteTvTest() = runTest {
        backgroundScope.launch(context = UnconfinedTestDispatcher()) { viewModel.favoriteTvs.collect() }

        testDatabaseRepository.insertTv(tv = tv1)
        testDatabaseRepository.insertTv(tv = tv2)
        viewModel.deleteTv(tv = tv1)

        assertEquals(
            expected = viewModel.favoriteTvs.value,
            actual = listOf(tv2)
        )
    }
}