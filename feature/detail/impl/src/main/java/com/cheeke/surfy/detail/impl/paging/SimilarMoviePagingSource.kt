package com.cheeke.surfy.detail.impl.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.cheeke.surfy.common.Log
import com.cheeke.surfy.model.SimilarMedia
import com.cheeke.surfy.network.MovieRemoteDataSource

class SimilarMoviePagingSource(
    private val apis: MovieRemoteDataSource,
    private val id: Int,
    private val language: String,
    private val region: String
) : PagingSource<Int, SimilarMedia>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SimilarMedia> =
        runCatching {
            val response = apis.getSimilarMovies(id = id, language = "$language-$region", page = params.key ?: 1)

            LoadResult.Page(
                data = response.results.orEmpty(),
                prevKey = null,
                nextKey = if ((response.totalPages ?: 1) > (params.key ?: 1)) (params.key ?: 1) + 1 else null
            )
        }.getOrElse { e ->
            Log.printStackTrace(e)
            LoadResult.Error(e)
        }

    override fun getRefreshKey(state: PagingState<Int, SimilarMedia>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey ?: anchorPage?.nextKey
        }
}