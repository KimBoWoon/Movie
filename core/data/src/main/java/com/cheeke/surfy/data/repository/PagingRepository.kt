package com.cheeke.surfy.data.repository

import androidx.paging.PagingSource
import com.cheeke.surfy.model.Media
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.model.Review
import com.cheeke.surfy.model.SearchKeyword
import com.cheeke.surfy.model.SearchType
import com.cheeke.surfy.model.TrendingMovieResult
import com.cheeke.surfy.model.TrendingPeopleResult
import com.cheeke.surfy.model.TrendingTvResult
import com.cheeke.surfy.model.Tv

interface PagingRepository {
    fun getSearchPagingSource(type: SearchType, query: String, language: String, region: String, isAdult: Boolean): PagingSource<Int, Media>
    fun getSimilarMoviePagingSource(id: Int): PagingSource<Int, Movie>
    fun getSimilarTvPagingSource(id: Int): PagingSource<Int, Tv>
    fun getRecommendKeywordPagingSource(query: String): PagingSource<Int, SearchKeyword>
    fun getMovieReviews(movieId: Int): PagingSource<Int, Review>
    fun getTvReviews(seriesId: Int): PagingSource<Int, Review>
    fun getTrendingMovie(timeWindow: String, language: String): PagingSource<Int, TrendingMovieResult>
    fun getTrendingPeople(timeWindow: String, language: String): PagingSource<Int, TrendingPeopleResult>
    fun getTrendingTv(timeWindow: String, language: String): PagingSource<Int, TrendingTvResult>
}