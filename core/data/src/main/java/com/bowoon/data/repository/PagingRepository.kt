package com.bowoon.data.repository

import androidx.paging.PagingSource
import com.bowoon.model.Movie
import com.bowoon.model.MovieReview
import com.bowoon.model.SearchKeyword
import com.bowoon.model.SearchType

interface PagingRepository {
    fun getSearchPagingSource(
        type: SearchType,
        query: String
    ): PagingSource<Int, Movie>
    fun getSimilarMoviePagingSource(id: Int): PagingSource<Int, Movie>
    fun getRecommendKeywordPagingSource(query: String): PagingSource<Int, SearchKeyword>
    fun getMovieReviews(movieId: Int): PagingSource<Int, MovieReview>
}