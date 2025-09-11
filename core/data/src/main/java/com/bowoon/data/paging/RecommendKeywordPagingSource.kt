package com.bowoon.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bowoon.common.Log
import com.bowoon.model.SearchKeyword
import com.bowoon.network.MovieNetworkDataSource

class RecommendKeywordPagingSource(
    private val apis: MovieNetworkDataSource,
    private val query: String
): PagingSource<Int, SearchKeyword>() {
    override fun getRefreshKey(state: PagingState<Int, SearchKeyword>): Int? = 1

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchKeyword> =
        runCatching {
            if (query.isNotEmpty()) {
                val page = params.key ?: 1
                val response = apis.getSearchKeyword(query = query, page = page)

                LoadResult.Page(
                    data = response.results ?: emptyList(),
                    prevKey = null,
                    nextKey = if (response.totalPages == page) null else page + 1
                )
            } else {
                LoadResult.Page<Int, SearchKeyword>(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            }
        }.getOrElse { e ->
            Log.printStackTrace(e)
            LoadResult.Error(e)
        }
}