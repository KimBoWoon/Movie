package com.cheeke.surfy.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.cheeke.surfy.common.Log
import com.cheeke.surfy.model.Media
import com.cheeke.surfy.model.SearchType
import com.cheeke.surfy.network.SearchRemoteDataSource

class SearchPagingSource(
    private val apis: SearchRemoteDataSource,
    private val type: SearchType,
    private val query: String,
    private val language: String,
    private val region: String,
    private val isAdult: Boolean
) : PagingSource<Int, Media>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Media> =
        runCatching {
            val response = when (type) {
                SearchType.MULTI -> apis.searchMulti(query = query, includeAdult = isAdult, language = language, page = params.key ?: 1)
                SearchType.MOVIE -> apis.searchMovies(query = query, includeAdult = isAdult, language = language, region = region, page = params.key ?: 1)
                SearchType.TV -> apis.searchTv(query = query, includeAdult = isAdult, language = language, region = region, page = params.key ?: 1)
                SearchType.PEOPLE -> apis.searchPeople(query = query, includeAdult = isAdult, language = language, region = region, page = params.key ?: 1)
                SearchType.SERIES -> apis.searchSeries(query = query, includeAdult = isAdult, language = language, region = region, page = params.key ?: 1)
            }

            LoadResult.Page(
                data = response.results.orEmpty(),
                prevKey = null,
                nextKey = if ((response.totalPages ?: 1) > (params.key ?: 1)) (params.key ?: 1) + 1 else null
            )
        }.getOrElse { e ->
            Log.printStackTrace(e)
            LoadResult.Error(e)
        }

    override fun getRefreshKey(state: PagingState<Int, Media>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey ?: anchorPage?.nextKey
        }
}