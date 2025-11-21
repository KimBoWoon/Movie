package com.bowoon.search

import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingSource
import com.bowoon.data.paging.RecommendKeywordPagingSource
import com.bowoon.data.paging.SearchPagingSource
import com.bowoon.model.Movie
import com.bowoon.model.SearchKeyword
import com.bowoon.model.SearchType
import com.bowoon.testing.TestMovieDataSource
import com.bowoon.testing.model.movieSearchTestData
import com.bowoon.testing.model.testRecommendedKeyword
import com.bowoon.testing.repository.TestPagingRepository
import com.bowoon.testing.repository.TestUserDataRepository
import com.bowoon.testing.utils.MainDispatcherRule
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class SearchVMTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var viewModel: SearchVM
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var testPagingRepository: TestPagingRepository
    private lateinit var testUserDataRepository: TestUserDataRepository
    private lateinit var testMovieAppDataRepository: TestMovieAppDataRepository
    private lateinit var apis: TestMovieDataSource

    @Before
    fun setup() {
        savedStateHandle = SavedStateHandle()
        testPagingRepository = TestPagingRepository()
        testUserDataRepository = TestUserDataRepository()
        testMovieAppDataRepository = TestMovieAppDataRepository()
        apis = TestMovieDataSource()
        viewModel = SearchVM(
            savedStateHandle = savedStateHandle,
            movieAppData = testMovieAppDataRepository,
            pagingRepository = testPagingRepository,
            userDataRepository = testUserDataRepository
        )
    }

    @Test
    fun updateKeywordTest() {
        assertEquals(viewModel.searchQuery, "")
        viewModel.updateKeyword("mission")
        assertEquals(viewModel.searchQuery, "mission")
        viewModel.updateKeyword("미션")
        assertEquals(viewModel.searchQuery, "미션")
    }

    @Test
    fun updateSearchTypeTest() {
        assertEquals(viewModel.searchType.value, SearchType.MOVIE)
        viewModel.updateSearchType(SearchType.PEOPLE)
        assertEquals(viewModel.searchType.value, SearchType.PEOPLE)
        viewModel.updateSearchType(SearchType.MOVIE)
        assertEquals(viewModel.searchType.value, SearchType.MOVIE)
    }

    @Test
    fun searchMovieStateTest() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.searchResult.collect() }
        viewModel.updateKeyword("미션")
        viewModel.searchMovies()

//        assertEquals(viewModel.searchMovieState.value, PagingData.empty<Movie>())

        val pagingSource = SearchPagingSource(
            apis = TestMovieDataSource(),
            type = SearchType.MOVIE,
            query = "미션",
            userDataRepository = testUserDataRepository
        )

        val a: PagingSource.LoadResult<Int, Movie> = PagingSource.LoadResult.Page(
            data = movieSearchTestData.results ?: emptyList(),
            prevKey = null,
            nextKey = 2
        )
        val b = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 2,
                placeholdersEnabled = false
            )
        )

        assertEquals(
            expected = a,
            actual = b,
        )
    }

    @Test
    fun recommendedKeywordTest() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.recommendKeywordPaging.collect() }

        viewModel.updateKeyword("mission")

        val pagingSource = RecommendKeywordPagingSource(
            apis = TestMovieDataSource(),
            query = "미션"
        )
        val a: PagingSource.LoadResult<Int, SearchKeyword> = PagingSource.LoadResult.Page(
            data = testRecommendedKeyword,
            prevKey = null,
            nextKey = null
        )
        val b = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 2,
                placeholdersEnabled = false
            )
        )

        assertEquals(
            a,
            b
        )
    }
}