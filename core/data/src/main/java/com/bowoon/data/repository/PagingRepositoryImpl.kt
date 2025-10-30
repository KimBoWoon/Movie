package com.bowoon.data.repository

import androidx.paging.PagingSource
import com.bowoon.data.paging.MovieReviewPagingSource
import com.bowoon.data.paging.RecommendKeywordPagingSource
import com.bowoon.data.paging.SearchPagingSource
import com.bowoon.data.paging.SimilarMoviePagingSource
import com.bowoon.model.Movie
import com.bowoon.model.MovieReview
import com.bowoon.model.SearchKeyword
import com.bowoon.model.SearchType
import com.bowoon.network.MovieNetworkDataSource
import javax.inject.Inject

class PagingRepositoryImpl @Inject constructor(
    private val apis: MovieNetworkDataSource
) : PagingRepository {
    override fun getSearchPagingSource(
        type: SearchType,
        query: String,
        language: String,
        region: String,
        isAdult: Boolean
    ): PagingSource<Int, Movie> = SearchPagingSource(
        apis = apis,
        type = type,
        query = query,
        language = language,
        region = region,
        isAdult = isAdult
    )

    override fun getSimilarMoviePagingSource(
        id: Int,
        language: String
    ): PagingSource<Int, Movie> = SimilarMoviePagingSource(
        apis = apis,
        id = id,
        language = language
    )

    override fun getRecommendKeywordPagingSource(query: String): PagingSource<Int, SearchKeyword> =
        RecommendKeywordPagingSource(
            apis = apis,
            query = query
        )

    override fun getMovieReviews(
        movieId: Int,
        language: String
    ): PagingSource<Int, MovieReview> = MovieReviewPagingSource(
        apis = apis,
        id = movieId,
        language = language
    )
}