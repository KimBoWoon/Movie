package com.bowoon.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bowoon.common.Log
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.InternalData
import com.bowoon.model.Movie
import com.bowoon.model.SearchType
import com.bowoon.network.MovieNetworkDataSource
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class SearchPagingSource @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val apis: MovieNetworkDataSource,
    private val type: SearchType,
    private val query: String
) : PagingSource<Int, Movie>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> =
        runCatching {
            val internalData = userDataRepository.internalData.firstOrNull() ?: InternalData()
            val language = "${internalData.language}-${internalData.region}"
            val region = internalData.region
            val isAdult = internalData.isAdult

            val response = when (type) {
                SearchType.MOVIE -> apis.searchMovies(query = query, includeAdult = isAdult, language = language, region = region, page = params.key ?: 1)
                SearchType.PEOPLE -> apis.searchPeople(query = query, includeAdult = isAdult, language = language, region = region, page = params.key ?: 1)
                SearchType.SERIES -> apis.searchSeries(query = query, includeAdult = isAdult, language = language, region = region, page = params.key ?: 1)
            }

            LoadResult.Page(
                data = response.results ?: emptyList(),
                prevKey = null,
                nextKey = if ((response.totalPages ?: 1) > (params.key ?: 1)) (params.key ?: 1) + 1 else null
            )
        }.getOrElse { e ->
            Log.printStackTrace(e)
            LoadResult.Error(e)
        }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey ?: anchorPage?.nextKey
        }
}