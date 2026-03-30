package com.cheeke.surfy.data.repository

import androidx.paging.PagingSource
import com.cheeke.surfy.data.paging.MovieReviewPagingSource
import com.cheeke.surfy.data.paging.RecommendKeywordPagingSource
import com.cheeke.surfy.data.paging.SearchPagingSource
import com.cheeke.surfy.data.paging.SimilarMoviePagingSource
import com.cheeke.surfy.data.paging.SimilarTvPagingSource
import com.cheeke.surfy.data.paging.TrendingMoviePagingSource
import com.cheeke.surfy.data.paging.TrendingPeoplePagingSource
import com.cheeke.surfy.data.paging.TrendingTvPagingSource
import com.cheeke.surfy.data.paging.TvReviewPagingSource
import com.cheeke.surfy.model.Media
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.model.Review
import com.cheeke.surfy.model.SearchKeyword
import com.cheeke.surfy.model.SearchType
import com.cheeke.surfy.model.TrendingMovieResult
import com.cheeke.surfy.model.TrendingPeopleResult
import com.cheeke.surfy.model.TrendingTvResult
import com.cheeke.surfy.model.Tv
import com.cheeke.surfy.network.MovieRemoteDataSource
import com.cheeke.surfy.network.SearchRemoteDataSource
import com.cheeke.surfy.network.TrendingRemoteDataSource
import com.cheeke.surfy.network.TvRemoteDataSource
import javax.inject.Inject

class PagingRepositoryImpl @Inject constructor(
    private val searchApis: SearchRemoteDataSource,
    private val movieApis: MovieRemoteDataSource,
    private val tvApis: TvRemoteDataSource,
    private val trendingApis: TrendingRemoteDataSource
) : PagingRepository {
    override fun getSearchPagingSource(
        type: SearchType,
        query: String,
        language: String,
        region: String,
        isAdult: Boolean
    ): PagingSource<Int, Media> = SearchPagingSource(
        apis = searchApis,
        type = type,
        query = query,
        language = language,
        region = region,
        isAdult = isAdult,
    )

    override fun getSimilarMoviePagingSource(
        id: Int,
        language: String,
        region: String
    ): PagingSource<Int, Movie> = SimilarMoviePagingSource(
        apis = movieApis,
        id = id,
        language = language,
        region = region
    )

    override fun getSimilarTvPagingSource(
        id: Int,
        language: String,
        region: String
    ): PagingSource<Int, Tv> = SimilarTvPagingSource(
        apis = tvApis,
        id = id,
        language = language,
        region = region
    )

    override fun getRecommendKeywordPagingSource(
        query: String
    ): PagingSource<Int, SearchKeyword> = RecommendKeywordPagingSource(
        apis = searchApis,
        query = query
    )

    override fun getMovieReviews(
        movieId: Int,
        language: String,
        region: String
    ): PagingSource<Int, Review> = MovieReviewPagingSource(
        apis = movieApis,
        id = movieId,
        language = language,
        region = region
    )

    override fun getTvReviews(
        seriesId: Int,
        language: String,
        region: String
    ): PagingSource<Int, Review> = TvReviewPagingSource(
        apis = tvApis,
        id = seriesId,
        language = language,
        region = region
    )

    override fun getTrendingMovie(
        timeWindow: String,
        language: String
    ): PagingSource<Int, TrendingMovieResult> = TrendingMoviePagingSource(
        apis = trendingApis,
        timeWindow = timeWindow,
        language = language
    )

    override fun getTrendingPeople(
        timeWindow: String,
        language: String
    ): PagingSource<Int, TrendingPeopleResult> = TrendingPeoplePagingSource(
        apis = trendingApis,
        timeWindow = timeWindow,
        language = language
    )

    override fun getTrendingTv(
        timeWindow: String,
        language: String
    ): PagingSource<Int, TrendingTvResult> = TrendingTvPagingSource(
        apis = trendingApis,
        timeWindow = timeWindow,
        language = language
    )
}