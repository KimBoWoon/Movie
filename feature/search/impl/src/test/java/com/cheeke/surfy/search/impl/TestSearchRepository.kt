package com.cheeke.surfy.search.impl

import androidx.paging.PagingSource
import androidx.paging.testing.asPagingSourceFactory
import com.cheeke.surfy.model.Media
import com.cheeke.surfy.model.SearchKeyword
import com.cheeke.surfy.model.SearchType
import com.cheeke.surfy.testing.model.movieSearchTestData
import com.cheeke.surfy.testing.model.testRecommendedKeyword

class TestSearchRepository : SearchRepository {
    override fun getSearchPagingSource(
        type: SearchType,
        query: String,
        language: String,
        region: String,
        isAdult: Boolean
    ): PagingSource<Int, Media> = (movieSearchTestData.results ?: emptyList()).asPagingSourceFactory().invoke()

    override fun getRecommendKeywordPagingSource(query: String): PagingSource<Int, SearchKeyword> =
        testRecommendedKeyword.asPagingSourceFactory().invoke()
}