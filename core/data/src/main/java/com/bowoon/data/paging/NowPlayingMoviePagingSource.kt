package com.bowoon.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bowoon.common.Log
import com.bowoon.model.DisplayItem
import com.bowoon.model.asExternalMovie
import com.bowoon.network.MovieNetworkDataSource
import javax.inject.Inject

class NowPlayingMoviePagingSource @Inject constructor(
    private val apis: MovieNetworkDataSource,
    private val language: String,
    private val region: String
) : PagingSource<Int, DisplayItem>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DisplayItem> =
        runCatching {
            val response = apis.getNowPlayingMovie(language = language, region = region, page = params.key ?: 1)

            LoadResult.Page(
                data = response.results?.map { it.asExternalMovie() } ?: emptyList(),
                prevKey = null,
                nextKey = if ((response.totalPages ?: 1) > (response.page ?: 1)) (params.key ?: 1) + 1 else null
            )
        }.getOrElse { e ->
            Log.printStackTrace(e)
            LoadResult.Error(e)
        }

    override fun getRefreshKey(state: PagingState<Int, DisplayItem>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey ?: anchorPage?.nextKey
        }
}