package com.bowoon.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bowoon.common.Log
import com.bowoon.model.SearchGroup
import com.bowoon.model.SearchType
import com.bowoon.network.MovieNetworkDataSource
import javax.inject.Inject

class TMDBSearchPagingSource @Inject constructor(
    private val apis: MovieNetworkDataSource,
    private val type: SearchType,
    private val query: String,
    private val language: String,
    private val region: String,
    private val isAdult: Boolean
) : PagingSource<Int, SearchGroup>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchGroup> =
        runCatching {
            when (type) {
                SearchType.MOVIE -> searchMovie(params, isAdult)
                SearchType.PEOPLE -> searchPeople(params, isAdult)
            }
        }.getOrElse { e ->
            Log.printStackTrace(e)
            LoadResult.Error(e)
        }

    override fun getRefreshKey(state: PagingState<Int, SearchGroup>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey ?: anchorPage?.nextKey
        }

    private suspend fun searchMovie(params: LoadParams<Int>, isAdult: Boolean): LoadResult<Int, SearchGroup> {
        val response = apis.searchMovies(query = query, includeAdult = isAdult, language = language, region = region, page = params.key ?: 1)

        return LoadResult.Page(
            data = response.results ?: emptyList(),
            prevKey = null,
            nextKey = if ((response.totalPages ?: 1) > (params.key ?: 1)) (params.key ?: 1) + 1 else null
        )
    }

    private suspend fun searchPeople(params: LoadParams<Int>, isAdult: Boolean): LoadResult<Int, SearchGroup> {
        val response = apis.searchPeople(query = query, includeAdult = isAdult, language = language, region = region, page = params.key ?: 1)

        return LoadResult.Page(
            data = response.results ?: emptyList(),
            prevKey = null,
            nextKey = if ((response.totalPages ?: 1) > (params.key ?: 1)) (params.key ?: 1) + 1 else null
        )
    }
}