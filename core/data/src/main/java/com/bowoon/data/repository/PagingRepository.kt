package com.bowoon.data.repository

import androidx.paging.PagingSource
import com.bowoon.model.Media
import com.bowoon.model.Movie
import com.bowoon.model.Review
import com.bowoon.model.SearchKeyword
import com.bowoon.model.SearchType
import com.bowoon.model.TrendingMovieResult
import com.bowoon.model.TrendingPeopleResult
import com.bowoon.model.TrendingTvResult
import com.bowoon.model.Tv

interface PagingRepository {
    fun getSearchPagingSource(type: SearchType, query: String): PagingSource<Int, Media>
    fun getSimilarMoviePagingSource(id: Int): PagingSource<Int, Movie>
    fun getSimilarTvPagingSource(id: Int): PagingSource<Int, Tv>
    fun getRecommendKeywordPagingSource(query: String): PagingSource<Int, SearchKeyword>
    fun getMovieReviews(movieId: Int): PagingSource<Int, Review>
    fun getTvReviews(seriesId: Int): PagingSource<Int, Review>
    fun getTrendingMovie(timeWindow: String, language: String): PagingSource<Int, TrendingMovieResult>
    fun getTrendingPeople(timeWindow: String, language: String): PagingSource<Int, TrendingPeopleResult>
    fun getTrendingTv(timeWindow: String, language: String): PagingSource<Int, TrendingTvResult>
}