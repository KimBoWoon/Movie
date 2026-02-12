package com.bowoon.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bowoon.common.Log
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.InternalData
import com.bowoon.model.SimilarTv
import com.bowoon.model.Tv
import com.bowoon.network.MovieNetworkDataSource
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class SimilarTvPagingSource @Inject constructor(
    private val apis: MovieNetworkDataSource,
    private val id: Int,
    private val userDataRepository: UserDataRepository
) : PagingSource<Int, Tv>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Tv> =
        runCatching {
            val internalData = userDataRepository.internalData.firstOrNull() ?: InternalData()
            val language = "${internalData.language}-${internalData.region}"
            val response = apis.getSimilarTv(id = id, language = language, page = params.key ?: 1)

            LoadResult.Page(
                data = getSearchItem(response.results),
                prevKey = null,
                nextKey = if ((response.totalPages ?: 1) > (params.key ?: 1)) (params.key ?: 1) + 1 else null
            )
        }.getOrElse { e ->
            Log.printStackTrace(e)
            LoadResult.Error(e)
        }

    override fun getRefreshKey(state: PagingState<Int, Tv>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey ?: anchorPage?.nextKey
        }

    private fun getSearchItem(
        response: List<SimilarTv>?
    ): List<Tv> =
        response?.map {
            Tv(
                id = it.id,
                title = it.name,
                posterPath = it.posterPath
            )
        } ?: emptyList()
}