package com.cheeke.surfy.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.cheeke.surfy.common.Log
import com.cheeke.surfy.data.repository.UserDataRepository
import com.cheeke.surfy.model.Review
import com.cheeke.surfy.network.MovieNetworkDataSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class TvReviewPagingSource(
    private val apis: MovieNetworkDataSource,
    private val id: Int,
    private val userDataRepository: UserDataRepository
) : PagingSource<Int, Review>() {
    override fun getRefreshKey(state: PagingState<Int, Review>): Int? = 1

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Review> =
        runCatching {
            val language = userDataRepository.internalData.map { "${it.language}-${it.region}" }.first()
            val page = params.key ?: 1
            val response = apis.getTvReviews(seriesId = id, language = language, page = page)

            LoadResult.Page(
                data = response.results ?: emptyList(),
                prevKey = null,
                nextKey = if ((response.totalPages ?: 1) > page) page + 1 else null
            )
        }.getOrElse { e ->
            Log.printStackTrace(e)
            LoadResult.Error(e)
        }
}