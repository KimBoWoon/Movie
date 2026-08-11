package com.cheeke.surfy.search.impl

import androidx.paging.PagingSource
import com.cheeke.surfy.model.Media
import com.cheeke.surfy.model.SearchKeyword
import com.cheeke.surfy.model.SearchType
import com.cheeke.surfy.network.api.SearchRemoteDataSource
import com.cheeke.surfy.search.impl.paging.RecommendKeywordPagingSource
import com.cheeke.surfy.search.impl.paging.SearchPagingSource
import javax.inject.Inject

interface SearchRepository {
    fun getSearchPagingSource(
        type: SearchType,
        query: String,
        language: String,
        region: String,
        isAdult: Boolean
    ): PagingSource<Int, Media>

    fun getRecommendKeywordPagingSource(
        query: String
    ): PagingSource<Int, SearchKeyword>
}

class SearchRepositoryImpl @Inject constructor(
    private val searchApis: SearchRemoteDataSource
) : SearchRepository {
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

    override fun getRecommendKeywordPagingSource(
        query: String
    ): PagingSource<Int, SearchKeyword> = RecommendKeywordPagingSource(
        apis = searchApis,
        query = query
    )
}