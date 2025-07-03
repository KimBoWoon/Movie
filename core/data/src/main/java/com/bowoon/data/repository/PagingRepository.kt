package com.bowoon.data.repository

import androidx.paging.PagingSource
import com.bowoon.model.DisplayItem
import com.bowoon.model.SearchKeyword
import com.bowoon.model.SearchType

interface PagingRepository {
    fun getSearchPagingSource(
        type: SearchType,
        query: String,
        language: String,
        region: String,
        isAdult: Boolean
    ): PagingSource<Int, DisplayItem>
    fun getSimilarMoviePagingSource(id: Int, language: String): PagingSource<Int, DisplayItem>
    fun getRecommendKeywordPagingSource(query: String): PagingSource<Int, SearchKeyword>
}