package com.bowoon.testing.repository

import android.annotation.SuppressLint
import androidx.paging.PagingSource
import androidx.paging.testing.asPagingSourceFactory
import com.bowoon.data.repository.PagingRepository
import com.bowoon.model.Genre
import com.bowoon.model.Media
import com.bowoon.model.Movie
import com.bowoon.model.Review
import com.bowoon.model.ReviewAuthorDetails
import com.bowoon.model.SearchKeyword
import com.bowoon.model.SearchType
import com.bowoon.model.TrendingMovieResult
import com.bowoon.model.TrendingPeopleResult
import com.bowoon.model.TrendingTvResult
import com.bowoon.model.Tv
import com.bowoon.testing.model.testMovieReviews
import com.bowoon.testing.model.testRecommendedKeyword

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
        query: String
    ): PagingSource<Int, Media> {
        return (0..100).map {
            Movie(
                genres = listOf(Genre(id = it)),
                releaseDate = "releaseDate_$it",
                title = "title_$it",
                adult = true,
                id = it,
                posterPath = "/imagePath_$it.png"
            ) as Media
        }.asPagingSourceFactory().invoke()
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
    override fun getTvReviews(seriesId: Int): PagingSource<Int, Review> = (0..100).map {
        Review(
            author = "author_$it",
            content = "content_$it",
            id = "id_$it",
            url = "url_$it",
            authorDetails = ReviewAuthorDetails(
                name = "name_$it",
                username = "username_$it",
                avatarPath = "avatarPath_$it",
                rating = it.toFloat()
            ),
            createdAt = "createdAt_$it",
            updatedAt = "updatedAt_$it",
        )
    }.asPagingSourceFactory().invoke()

    @SuppressLint("VisibleForTests")
    override fun getTrendingMovie(
        timeWindow: String,
        language: String
    ): PagingSource<Int, TrendingMovieResult> = (0..100).map {
        TrendingMovieResult(
            adult = true,
            backdropPath = "backdropPath_$it",
            genreIds = emptyList(),
            id = it,
            originalLanguage = "originalLanguage_$it",
            originalTitle = "originalTitle_$it"
        )
    }.asPagingSourceFactory().invoke()

    @SuppressLint("VisibleForTests")
    override fun getTrendingPeople(
        timeWindow: String,
        language: String
    ): PagingSource<Int, TrendingPeopleResult> = (0..100).map {
        TrendingPeopleResult(
            adult = true,
            posterPath = "posterPath_$it",
            id = it,
            originalTitle = "originalTitle_$it"
        )
    }.asPagingSourceFactory().invoke()

    @SuppressLint("VisibleForTests")
    override fun getTrendingTv(
        timeWindow: String,
        language: String
    ): PagingSource<Int, TrendingTvResult> = (0..100).map {
        TrendingTvResult(
            adult = true,
            backdropPath = "backdropPath_$it",
            genreIds = emptyList(),
            id = it,
            originalLanguage = "originalLanguage_$it",
            originalTitle = "originalTitle_$it"
        )
    }.asPagingSourceFactory().invoke()
}