package com.bowoon.data.repository

import androidx.paging.PagingSource
import com.bowoon.model.Movie
import com.bowoon.model.SearchGroup
import com.bowoon.model.SearchKeyword
import com.bowoon.model.SearchType

interface PagingRepository {
    fun getUpComingMoviePagingSource(
        language: String,
        region: String,
        isAdult: Boolean,
        releaseDateGte: String,
        releaseDateLte: String
    ): PagingSource<Int, Movie>
    fun getNowPlayingMoviePagingSource(
        language: String,
        region: String,
        isAdult: Boolean,
        releaseDateGte: String,
        releaseDateLte: String
    ): PagingSource<Int, Movie>
    fun getSearchPagingSource(
        type: SearchType,
        query: String,
        language: String,
        region: String,
        isAdult: Boolean
    ): PagingSource<Int, SearchGroup>
    fun getSimilarMoviePagingSource(id: Int, language: String): PagingSource<Int, Movie>
    fun getRecommendKeywordPagingSource(query: String): PagingSource<Int, SearchKeyword>
}