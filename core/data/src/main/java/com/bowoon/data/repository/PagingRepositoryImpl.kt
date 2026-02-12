package com.bowoon.data.repository

import androidx.paging.PagingSource
import com.bowoon.data.paging.MovieReviewPagingSource
import com.bowoon.data.paging.RecommendKeywordPagingSource
import com.bowoon.data.paging.SearchPagingSource
import com.bowoon.data.paging.SimilarMoviePagingSource
import com.bowoon.data.paging.SimilarTvPagingSource
import com.bowoon.data.paging.TrendingMoviePagingSource
import com.bowoon.data.paging.TrendingPeoplePagingSource
import com.bowoon.data.paging.TrendingTvPagingSource
import com.bowoon.data.paging.TvReviewPagingSource
import com.bowoon.model.Media
import com.bowoon.model.Movie
import com.bowoon.model.Review
import com.bowoon.model.SearchKeyword
import com.bowoon.model.SearchType
import com.bowoon.model.TrendingMovieResult
import com.bowoon.model.TrendingPeopleResult
import com.bowoon.model.TrendingTvResult
import com.bowoon.model.Tv
import com.bowoon.network.MovieNetworkDataSource
import javax.inject.Inject

class PagingRepositoryImpl @Inject constructor(
    private val apis: MovieNetworkDataSource,
    private val userDataRepository: UserDataRepository
) : PagingRepository {
    override fun getSearchPagingSource(
        type: SearchType,
        query: String
    ): PagingSource<Int, Media> = SearchPagingSource(
        apis = apis,
        type = type,
        query = query,
        userDataRepository = userDataRepository
    )

    override fun getSimilarMoviePagingSource(
        id: Int
    ): PagingSource<Int, Movie> = SimilarMoviePagingSource(
        apis = apis,
        id = id,
        userDataRepository = userDataRepository
    )

    override fun getSimilarTvPagingSource(id: Int): PagingSource<Int, Tv> = SimilarTvPagingSource(
        apis = apis,
        id = id,
        userDataRepository = userDataRepository
    )

    override fun getRecommendKeywordPagingSource(query: String): PagingSource<Int, SearchKeyword> =
        RecommendKeywordPagingSource(
            apis = apis,
            query = query
        )

    override fun getMovieReviews(
        movieId: Int
    ): PagingSource<Int, Review> = MovieReviewPagingSource(
        apis = apis,
        id = movieId,
        userDataRepository = userDataRepository
    )

    override fun getTvReviews(
        seriesId: Int
    ): PagingSource<Int, Review> = TvReviewPagingSource(
        apis = apis,
        id = seriesId,
        userDataRepository = userDataRepository
    )

    override fun getTrendingMovie(timeWindow: String, language: String): PagingSource<Int, TrendingMovieResult> = TrendingMoviePagingSource(
        apis = apis,
        timeWindow = timeWindow,
        language = language
    )

    override fun getTrendingPeople(timeWindow: String, language: String): PagingSource<Int, TrendingPeopleResult> = TrendingPeoplePagingSource(
        apis = apis,
        timeWindow = timeWindow,
        language = language
    )

    override fun getTrendingTv(timeWindow: String, language: String): PagingSource<Int, TrendingTvResult> = TrendingTvPagingSource(
        apis = apis,
        timeWindow = timeWindow,
        language = language
    )
}