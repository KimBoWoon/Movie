package com.cheeke.surfy.home.impl.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.cheeke.surfy.common.Log
import com.cheeke.surfy.model.TrendingMediaResult
import com.cheeke.surfy.network.TrendingRemoteDataSource

class TrendingPeoplePagingSource(
    private val apis: TrendingRemoteDataSource,
    private val timeWindow: String,
    private val language: String
) : PagingSource<Int, TrendingMediaResult>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TrendingMediaResult> =
        runCatching {
            val response = apis.getTrendingPeople(timeWindow = timeWindow, language = language, page = params.key ?: 1)

            LoadResult.Page(
                data = response.results.orEmpty(),
                prevKey = null,
                nextKey = if ((response.totalPages ?: 1) > (params.key ?: 1)) (params.key ?: 1) + 1 else null
            )
        }.getOrElse { e ->
            Log.printStackTrace(e)
            LoadResult.Error(e)
        }

    override fun getRefreshKey(state: PagingState<Int, TrendingMediaResult>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey ?: anchorPage?.nextKey
        }
}