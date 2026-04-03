package com.cheeke.surfy.data.repository

import androidx.paging.PagingSource
import com.cheeke.surfy.model.Media
import com.cheeke.surfy.model.Review
import com.cheeke.surfy.model.SearchKeyword
import com.cheeke.surfy.model.SearchType
import com.cheeke.surfy.model.SimilarMedia
import com.cheeke.surfy.model.TrendingMediaResult

interface PagingRepository {
    fun getSearchPagingSource(type: SearchType, query: String, language: String, region: String, isAdult: Boolean): PagingSource<Int, Media>
    fun getSimilarMoviePagingSource(id: Int, language: String, region: String): PagingSource<Int, SimilarMedia>
    fun getSimilarTvPagingSource(id: Int, language: String, region: String): PagingSource<Int, SimilarMedia>
    fun getRecommendKeywordPagingSource(query: String): PagingSource<Int, SearchKeyword>
    fun getMovieReviews(movieId: Int, language: String, region: String): PagingSource<Int, Review>
    fun getTvReviews(seriesId: Int, language: String, region: String): PagingSource<Int, Review>
    fun getTrendingMovie(timeWindow: String, language: String): PagingSource<Int, TrendingMediaResult>
    fun getTrendingPeople(timeWindow: String, language: String): PagingSource<Int, TrendingMediaResult>
    fun getTrendingTv(timeWindow: String, language: String): PagingSource<Int, TrendingMediaResult>
}