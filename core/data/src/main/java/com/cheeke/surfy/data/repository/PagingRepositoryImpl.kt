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
import com.cheeke.surfy.network.MovieNetworkDataSource
import javax.inject.Inject

class PagingRepositoryImpl @Inject constructor(
    private val apis: MovieNetworkDataSource,
    private val userDataRepository: UserDataRepository
) : PagingRepository {
    override fun getSearchPagingSource(
        type: SearchType,
        query: String,
        language: String,
        region: String,
        isAdult: Boolean
    ): PagingSource<Int, Media> = SearchPagingSource(
        apis = apis,
        type = type,
        query = query,
        language = language,
        region = region,
        isAdult = isAdult,
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

    override fun getTrendingMovie(timeWindow: String, language: String): PagingSource<Int, TrendingMovieResult> =
        TrendingMoviePagingSource(
            apis = apis,
            timeWindow = timeWindow,
            language = language
        )

    override fun getTrendingPeople(timeWindow: String, language: String): PagingSource<Int, TrendingPeopleResult> =
        TrendingPeoplePagingSource(
            apis = apis,
            timeWindow = timeWindow,
            language = language
        )

    override fun getTrendingTv(timeWindow: String, language: String): PagingSource<Int, TrendingTvResult> =
        TrendingTvPagingSource(
            apis = apis,
            timeWindow = timeWindow,
            language = language
        )
}