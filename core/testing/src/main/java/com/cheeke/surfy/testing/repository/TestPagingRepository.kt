package com.cheeke.surfy.testing.repository

import android.annotation.SuppressLint
import androidx.paging.PagingSource
import androidx.paging.testing.asPagingSourceFactory
import com.cheeke.surfy.data.repository.PagingRepository
import com.cheeke.surfy.model.Genre
import com.cheeke.surfy.model.Media
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.model.Review
import com.cheeke.surfy.model.SearchKeyword
import com.cheeke.surfy.model.SearchType
import com.cheeke.surfy.model.TrendingMovieResult
import com.cheeke.surfy.model.TrendingPeopleResult
import com.cheeke.surfy.model.TrendingTvResult
import com.cheeke.surfy.model.Tv
import com.cheeke.surfy.testing.model.movieSearchTestData
import com.cheeke.surfy.testing.model.peopleSearchTestData
import com.cheeke.surfy.testing.model.seriesSearchTestData
import com.cheeke.surfy.testing.model.testMovieReviews
import com.cheeke.surfy.testing.model.testRecommendedKeyword
import com.cheeke.surfy.testing.model.testTrendingMovie
import com.cheeke.surfy.testing.model.testTrendingPeople
import com.cheeke.surfy.testing.model.testTrendingTv
import com.cheeke.surfy.testing.model.testTvReviews
import com.cheeke.surfy.testing.model.tvSearchTestData

class TestPagingRepository : PagingRepository {
    @SuppressLint("VisibleForTests")
    private val testPagingSource = (0..100).map {
        Movie(
            genres = listOf(Genre(id = it)),
            releaseDate = "releaseDate_$it",
            title = "title_$it",
            adult = true,
            id = it,
            posterPath = "/imagePath_$it.png"
        )
    }.asPagingSourceFactory().invoke()

    @SuppressLint("VisibleForTests")
    override fun getSearchPagingSource(
        type: SearchType,
        query: String,
        language: String,
        region: String,
        isAdult: Boolean
    ): PagingSource<Int, Media> {
        return (when (type) {
            SearchType.MOVIE -> movieSearchTestData.results
            SearchType.MULTI -> movieSearchTestData.results
            SearchType.TV -> tvSearchTestData.results
            SearchType.PEOPLE -> peopleSearchTestData.results
            SearchType.SERIES -> seriesSearchTestData.results
        } ?: emptyList()).asPagingSourceFactory().invoke()
    }

    override fun getSimilarMoviePagingSource(id: Int): PagingSource<Int, Movie> = testPagingSource

    @SuppressLint("VisibleForTests")
    override fun getRecommendKeywordPagingSource(query: String): PagingSource<Int, SearchKeyword> = testRecommendedKeyword.asPagingSourceFactory().invoke()

    @SuppressLint("VisibleForTests")
    override fun getMovieReviews(
        movieId: Int
    ): PagingSource<Int, Review> = testMovieReviews.asPagingSourceFactory().invoke()

    @SuppressLint("VisibleForTests")
    override fun getSimilarTvPagingSource(id: Int): PagingSource<Int, Tv> = (0..100).map {
        Tv(
            genres = listOf(Genre(id = it)),
            firstAirDate = "firstAirDate_$it",
            title = "title_$it",
            adult = true,
            id = it,
            posterPath = "/imagePath_$it.png"
        )
    }.asPagingSourceFactory().invoke()

    @SuppressLint("VisibleForTests")
    override fun getTvReviews(seriesId: Int): PagingSource<Int, Review> = testTvReviews.asPagingSourceFactory().invoke()

    @SuppressLint("VisibleForTests")
    override fun getTrendingMovie(
        timeWindow: String,
        language: String
    ): PagingSource<Int, TrendingMovieResult> = testTrendingMovie.asPagingSourceFactory().invoke()

    @SuppressLint("VisibleForTests")
    override fun getTrendingPeople(
        timeWindow: String,
        language: String
    ): PagingSource<Int, TrendingPeopleResult> = testTrendingPeople.asPagingSourceFactory().invoke()

    @SuppressLint("VisibleForTests")
    override fun getTrendingTv(
        timeWindow: String,
        language: String
    ): PagingSource<Int, TrendingTvResult> = testTrendingTv.asPagingSourceFactory().invoke()
}