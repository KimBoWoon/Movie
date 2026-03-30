package com.cheeke.surfy.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.cheeke.surfy.common.Log
import com.cheeke.surfy.model.SimilarTv
import com.cheeke.surfy.model.Tv
import com.cheeke.surfy.network.TvRemoteDataSource
import javax.inject.Inject

class SimilarTvPagingSource @Inject constructor(
    private val apis: TvRemoteDataSource,
    private val id: Int,
    private val language: String,
    private val region: String
) : PagingSource<Int, Tv>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Tv> =
        runCatching {
            val response = apis.getSimilarTv(id = id, language = "$language-$region", page = params.key ?: 1)

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