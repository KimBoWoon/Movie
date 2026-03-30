package com.cheeke.surfy.data.repository

import androidx.paging.PagingSource
import com.cheeke.surfy.model.Genre
import com.cheeke.surfy.model.SearchType
import com.cheeke.surfy.model.Tv
import com.cheeke.surfy.testing.model.movieSearchTestData
import com.cheeke.surfy.testing.model.peopleSearchTestData
import com.cheeke.surfy.testing.model.seriesSearchTestData
import com.cheeke.surfy.testing.model.similarTvTestData
import com.cheeke.surfy.testing.model.testMovieReviews
import com.cheeke.surfy.testing.model.testRecommendedKeyword
import com.cheeke.surfy.testing.model.testTrendingMovie
import com.cheeke.surfy.testing.model.testTrendingPeople
import com.cheeke.surfy.testing.model.testTrendingTv
import com.cheeke.surfy.testing.model.testTvReviews
import com.cheeke.surfy.testing.model.tvSearchTestData
import com.cheeke.surfy.testing.repository.TestPagingRepository
import com.cheeke.surfy.testing.utils.MainDispatcherRule
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class PagingRepositoryTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private val repository = TestPagingRepository()

    @Test
    fun moviePagingTest() = runTest {
        val pagingSource = repository.getSearchPagingSource(
            type = SearchType.MOVIE,
            query = "미션",
            language = "ko-KR",
            region = "KR",
            isAdult = true
        )

        val actual = pagingSource.load(
            params = PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 100,
                placeholdersEnabled = false
            )
        )

        assertTrue(actual is PagingSource.LoadResult.Page)

        val page = actual as PagingSource.LoadResult.Page

        assertEquals(expected = movieSearchTestData.results ?: emptyList(), actual = page.data)
        assertEquals(expected = null, actual = page.prevKey)
        assertEquals(expected = null, actual = page.nextKey)
    }

    @Test
    fun peoplePagingTest() = runTest {
        val pagingSource = repository.getSearchPagingSource(
            type = SearchType.PEOPLE,
            query = "톰 크루즈",
            language = "ko-KR",
            region = "KR",
            isAdult = true
        )

        val actual = pagingSource.load(
            params = PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 100,
                placeholdersEnabled = false
            )
        )

        assertTrue(actual is PagingSource.LoadResult.Page)

        val page = actual as PagingSource.LoadResult.Page

        assertEquals(expected = peopleSearchTestData.results ?: emptyList(), actual = page.data)
        assertEquals(expected = null, actual = page.prevKey)
        assertEquals(expected = null, actual = page.nextKey)
    }

    @Test
    fun tvPagingTest() = runTest {
        val pagingSource = repository.getSearchPagingSource(
            type = SearchType.TV,
            query = "톰 크루즈",
            language = "ko-KR",
            region = "KR",
            isAdult = true
        )

        val actual = pagingSource.load(
            params = PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 100,
                placeholdersEnabled = false
            )
        )

        assertTrue(actual is PagingSource.LoadResult.Page)

        val page = actual as PagingSource.LoadResult.Page

        assertEquals(expected = tvSearchTestData.results ?: emptyList(), actual = page.data)
        assertEquals(expected = null, actual = page.prevKey)
        assertEquals(expected = null, actual = page.nextKey)
    }

    @Test
    fun seriesPagingTest() = runTest {
        val pagingSource = repository.getSearchPagingSource(
            type = SearchType.SERIES,
            query = "톰 크루즈",
            language = "ko-KR",
            region = "KR",
            isAdult = true
        )

        val actual = pagingSource.load(
            params = PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 101,
                placeholdersEnabled = false
            )
        )

        assertTrue(actual is PagingSource.LoadResult.Page)

        val page = actual as PagingSource.LoadResult.Page

        assertEquals(expected = seriesSearchTestData.results ?: emptyList(), actual = page.data)
        assertEquals(expected = null, actual = page.prevKey)
        assertEquals(expected = null, actual = page.nextKey)
    }

    @Test
    fun multiPagingTest() = runTest {
        val pagingSource = repository.getSearchPagingSource(
            type = SearchType.MULTI,
            query = "톰 크루즈",
            language = "ko-KR",
            region = "KR",
            isAdult = true
        )

        val actual = pagingSource.load(
            params = PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 101,
                placeholdersEnabled = false
            )
        )

        assertTrue(actual is PagingSource.LoadResult.Page)

        val page = actual as PagingSource.LoadResult.Page

        assertEquals(expected = movieSearchTestData.results ?: emptyList(), actual = page.data)
        assertEquals(expected = null, actual = page.prevKey)
        assertEquals(expected = null, actual = page.nextKey)
    }

    @Test
    fun similarMoviePagingTest() = runTest {
        val pagingSource = repository.getSimilarMoviePagingSource(id = 0, language = "ko", region = "KR")

        val actual = pagingSource.load(
            params = PagingSource.LoadParams.Refresh(
                key = 1,
                loadSize = 100,
                placeholdersEnabled = false
            )
        )

        assertTrue(actual is PagingSource.LoadResult.Page)

        val page = actual as PagingSource.LoadResult.Page

        assertEquals(expected = movieSearchTestData.results ?: emptyList(), actual = page.data)
        assertEquals(expected = 0, actual = page.prevKey)
        assertEquals(expected = null, actual = page.nextKey)
    }

    @Test
    fun similarTvPagingTest() = runTest {
        val pagingSource = repository.getSimilarTvPagingSource(id = 0, language = "ko", region = "KR")

        val actual = pagingSource.load(
            params = PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 101,
                placeholdersEnabled = false
            )
        )

        assertTrue(actual is PagingSource.LoadResult.Page)

        val page = actual as PagingSource.LoadResult.Page

        assertEquals(
            expected = similarTvTestData.results?.mapIndexed { index, tv ->
                Tv(
                    genres = listOf(Genre(id = index)),
                    title = tv.name,
                    id = tv.id,
                    posterPath = tv.posterPath,
                    firstAirDate = "firstAirDate_$index",
                    adult = true
                )
            }?.firstOrNull() ?: emptyList<Tv>(),
            actual = page.data.firstOrNull() ?: emptyList<Tv>()
        )
        assertEquals(expected = null, actual = page.prevKey)
        assertEquals(expected = null, actual = page.nextKey)
    }

    @Test
    fun recommendKeywordPagingTest() = runTest {
        val pagingSource = repository.getRecommendKeywordPagingSource(query = "미션")

        val actual = pagingSource.load(
            params = PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 6,
                placeholdersEnabled = false
            )
        )

        assertTrue(actual is PagingSource.LoadResult.Page)

        val page = actual as PagingSource.LoadResult.Page

        assertEquals(expected = testRecommendedKeyword, actual = page.data)
        assertEquals(expected = null, actual = page.prevKey)
        assertEquals(expected = null, actual = page.nextKey)
    }

    @Test
    fun movieReviewPagingTest() = runTest {
        val pagingSource = repository.getMovieReviews(movieId = 0, language = "ko", region = "KR")

        val actual = pagingSource.load(
            params = PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 6,
                placeholdersEnabled = false
            )
        )

        assertTrue(actual is PagingSource.LoadResult.Page)

        val page = actual as PagingSource.LoadResult.Page

        assertEquals(expected = testMovieReviews, actual = page.data)
        assertEquals(expected = null, actual = page.prevKey)
        assertEquals(expected = null, actual = page.nextKey)
    }

    @Test
    fun tvReviewPagingTest() = runTest {
        val pagingSource = repository.getTvReviews(seriesId = 0, language = "ko", region = "KR")

        val actual = pagingSource.load(
            params = PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 101,
                placeholdersEnabled = false
            )
        )

        assertTrue(actual is PagingSource.LoadResult.Page)

        val page = actual as PagingSource.LoadResult.Page

        assertEquals(expected = testTvReviews, actual = page.data)
        assertEquals(expected = null, actual = page.prevKey)
        assertEquals(expected = null, actual = page.nextKey)
    }

    @Test
    fun trendingMoviePagingTest() = runTest {
        val pagingSource = repository.getTrendingMovie(timeWindow = "day", language = "ko-KR")

        val actual = pagingSource.load(
            params = PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 101,
                placeholdersEnabled = false
            )
        )

        assertTrue(actual is PagingSource.LoadResult.Page)

        val page = actual as PagingSource.LoadResult.Page

        assertEquals(expected = testTrendingMovie.results, actual = page.data)
        assertEquals(expected = null, actual = page.prevKey)
        assertEquals(expected = null, actual = page.nextKey)
    }

    @Test
    fun trendingPeoplePagingTest() = runTest {
        val pagingSource = repository.getTrendingPeople(timeWindow = "day", language = "ko-KR")

        val actual = pagingSource.load(
            params = PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 101,
                placeholdersEnabled = false
            )
        )

        assertTrue(actual is PagingSource.LoadResult.Page)

        val page = actual as PagingSource.LoadResult.Page

        assertEquals(expected = testTrendingPeople.results, actual = page.data)
        assertEquals(expected = null, actual = page.prevKey)
        assertEquals(expected = null, actual = page.nextKey)
    }

    @Test
    fun trendingTvPagingTest() = runTest {
        val pagingSource = repository.getTrendingTv(timeWindow = "day", language = "ko-KR")

        val actual = pagingSource.load(
            params = PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 101,
                placeholdersEnabled = false
            )
        )

        assertTrue(actual is PagingSource.LoadResult.Page)

        val page = actual as PagingSource.LoadResult.Page

        assertEquals(expected = testTrendingTv.results, actual = page.data)
        assertEquals(expected = null, actual = page.prevKey)
        assertEquals(expected = null, actual = page.nextKey)
    }
}