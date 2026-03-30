package com.cheeke.surfy.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.cheeke.surfy.common.Log
import com.cheeke.surfy.model.TrendingTvResult
import com.cheeke.surfy.network.TrendingRemoteDataSource
import javax.inject.Inject

class TrendingTvPagingSource @Inject constructor(
    private val apis: TrendingRemoteDataSource,
    private val timeWindow: String,
    private val language: String
) : PagingSource<Int, TrendingTvResult>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TrendingTvResult> =
        runCatching {
            val response = apis.getTrendingTv(timeWindow = timeWindow, language = language, page = params.key ?: 1)

            LoadResult.Page(
                data = response.results ?: emptyList(),
                prevKey = null,
                nextKey = if ((response.totalPages ?: 1) > (params.key ?: 1)) (params.key ?: 1) + 1 else null
            )
        }.getOrElse { e ->
            Log.printStackTrace(e)
            LoadResult.Error(e)
        }

    override fun getRefreshKey(state: PagingState<Int, TrendingTvResult>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey ?: anchorPage?.nextKey
        }
}