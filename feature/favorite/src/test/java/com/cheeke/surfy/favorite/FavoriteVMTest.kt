package com.cheeke.surfy.favorite

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.testing.TestPager
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
    fun favoriteMoviePagingTest() = runTest {
        val favoriteMovieSource = testDatabaseRepository.getFavoriteMovie()
        val favoriteMoviePager = TestPager(
            config = PagingConfig(pageSize = 0, initialLoadSize = 20, prefetchDistance = 5),
            pagingSource = testDatabaseRepository.getFavoriteMovie()
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
    fun favoritePeoplePagingTest() = runTest {
        val favoritePeopleSource = testDatabaseRepository.getFavoritePeople()
        val favoritePeoplePager = TestPager(
            config = PagingConfig(pageSize = 0, initialLoadSize = 20, prefetchDistance = 5),
            pagingSource = testDatabaseRepository.getFavoritePeople()
        )

        assertEquals(
            expected = favoritePeoplePager.refresh(initialKey = 0),
            actual = favoritePeopleSource.load(
                params = PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = 20,
                    placeholdersEnabled = false
                )
            )
        )
    }

    @Test
    fun favoriteTvPagingTest() = runTest {
        val favoriteTvSource = testDatabaseRepository.getFavoriteTv()
        val favoriteTvPager = TestPager(
            config = PagingConfig(pageSize = 0, initialLoadSize = 20, prefetchDistance = 5),
            pagingSource = testDatabaseRepository.getFavoriteTv()
        )

        assertEquals(
            expected = favoriteTvPager.refresh(initialKey = 0),
            actual = favoriteTvSource.load(
                params = PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = 20,
                    placeholdersEnabled = false
                )
            )
        )
    }

//    @Test
//    fun deleteMovieTest() = runTest {
//        backgroundScope.launch(context = UnconfinedTestDispatcher()) { viewModel.favoriteMovies.collect() }
//
//        testDatabaseRepository.insertMovie(movie = movie1)
//        testDatabaseRepository.insertMovie(movie = movie2)
//        viewModel.deleteFavorite(favoriteTab = FavoriteTab.MOVIE, media = movie1)
//
//        assertEquals(
//            expected = viewModel.favoriteMovies.value,
//            actual = listOf(movie2)
//        )
//    }
//
//    @Test
//    fun deletePeopleTest() = runTest {
//        backgroundScope.launch(context = UnconfinedTestDispatcher()) { viewModel.favoritePeoples.collect() }
//
//        testDatabaseRepository.insertPeople(people = people1)
//        testDatabaseRepository.insertPeople(people = people2)
//        viewModel.deletePeople(people = people1)
//
//        assertEquals(
//            expected = viewModel.favoritePeoples.value,
//            actual = listOf(people2)
//        )
//    }
//
//    @Test
//    fun deleteTvTest() = runTest {
//        backgroundScope.launch(context = UnconfinedTestDispatcher()) { viewModel.favoriteTvs.collect() }
//
//        testDatabaseRepository.insertTv(tv = tv1)
//        testDatabaseRepository.insertTv(tv = tv2)
//        viewModel.deleteTv(tv = tv1)
//
//        assertEquals(
//            expected = viewModel.favoriteTvs.value,
//            actual = listOf(tv2)
//        )
//    }
}