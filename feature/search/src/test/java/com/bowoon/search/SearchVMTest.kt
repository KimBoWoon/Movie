package com.bowoon.search

import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingSource
import com.bowoon.data.paging.TMDBSearchPagingSource
import com.bowoon.model.Movie
import com.bowoon.model.SearchType
import com.bowoon.testing.TestMovieDataSource
import com.bowoon.testing.model.movieSearchTestData
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

    @Before
    fun setup() {
        savedStateHandle = SavedStateHandle()
        testPagingRepository = TestPagingRepository()
        testUserDataRepository = TestUserDataRepository()
        viewModel = SearchVM(
            savedStateHandle = savedStateHandle,
            pagingRepository = testPagingRepository,
            userDataRepository = testUserDataRepository
        )
    }

    @Test
    fun updateKeywordTest() {
        assertEquals(viewModel.keyword, "")
        viewModel.updateKeyword("mission")
        assertEquals(viewModel.keyword, "mission")
        viewModel.updateKeyword("미션")
        assertEquals(viewModel.keyword, "미션")
    }

    @Test
    fun updateSearchTypeTest() {
        assertEquals(SearchType.entries[viewModel.searchType], SearchType.MOVIE)
        viewModel.updateSearchType(SearchType.PEOPLE)
        assertEquals(SearchType.entries[viewModel.searchType], SearchType.PEOPLE)
        viewModel.updateSearchType(SearchType.MOVIE)
        assertEquals(SearchType.entries[viewModel.searchType], SearchType.MOVIE)
    }

    @Test
    fun searchMovieStateTest() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.searchMovieState.collect() }
        viewModel.searchMovies("미션")

//        assertEquals(viewModel.searchMovieState.value, PagingData.empty<Movie>())

        val pagingSource = TMDBSearchPagingSource(
            apis = TestMovieDataSource(),
            type = SearchType.MOVIE.label,
            query = "미션",
            language = "ko-KR",
            region = "KR",
            isAdult = true
        )

        val a: PagingSource.LoadResult<Int, Movie> = PagingSource.LoadResult.Page(
            data = movieSearchTestData.results?.map {
                Movie(
                    id = it.id,
                    title = it.title,
                    posterPath = it.posterPath
                )
            } ?: emptyList(),
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
}