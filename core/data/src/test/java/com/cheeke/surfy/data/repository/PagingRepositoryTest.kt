package com.cheeke.surfy.data.repository

import androidx.paging.PagingSource
import com.cheeke.surfy.data.paging.SearchPagingSource
import com.cheeke.surfy.data.paging.SimilarMoviePagingSource
import com.cheeke.surfy.model.Media
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.model.SearchType
import com.cheeke.surfy.testing.TestMovieDataSource
import com.cheeke.surfy.testing.model.movieSearchTestData
import com.cheeke.surfy.testing.model.peopleSearchTestData
import com.cheeke.surfy.testing.model.similarMoviesTestData
import com.cheeke.surfy.testing.repository.TestUserDataRepository
import com.cheeke.surfy.testing.utils.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class PagingRepositoryTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private val movieApis = TestMovieDataSource()
    private val testUserDataRepository = TestUserDataRepository()

    @Test
    fun moviePagingTest() = runTest {
        val pagingSource = SearchPagingSource(
            apis = movieApis,
            type = SearchType.MOVIE,
            query = "미션",
            language = "ko-KR",
            region = "KR",
            isAdult = true,
        )

        val a: PagingSource.LoadResult<Int, Media> = PagingSource.LoadResult.Page(
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
    fun peoplePagingTest() = runTest {
        val pagingSource = SearchPagingSource(
            apis = movieApis,
            type = SearchType.PEOPLE,
            query = "톰 크루즈",
            language = "ko-KR",
            region = "KR",
            isAdult = true
        )

        val a: PagingSource.LoadResult<Int, Media> = PagingSource.LoadResult.Page(
            data = peopleSearchTestData.results ?: emptyList(),
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
            expected = a,
            actual = b,
        )
    }

    @Test
    fun similarMoviePagingTest() = runTest {
        val pagingSource = SimilarMoviePagingSource(
            apis = movieApis,
            id = 0,
            userDataRepository = testUserDataRepository
        )

        val a: PagingSource.LoadResult<Int, Movie> = PagingSource.LoadResult.Page(
            data = similarMoviesTestData.results?.map {
                Movie(
                    id = it.id,
                    title = it.title,
                    posterPath = it.posterPath
                )
            } ?: emptyList(),
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
            expected = a,
            actual = b,
        )
    }
}