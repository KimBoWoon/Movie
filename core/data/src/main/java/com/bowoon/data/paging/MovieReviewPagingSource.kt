package com.bowoon.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bowoon.common.Log
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.MovieReview
import com.bowoon.network.MovieNetworkDataSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class MovieReviewPagingSource(
    private val apis: MovieNetworkDataSource,
    private val id: Int,
    private val userDataRepository: UserDataRepository
) : PagingSource<Int, MovieReview>() {
    override fun getRefreshKey(state: PagingState<Int, MovieReview>): Int? = 1

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieReview> =
        runCatching {
            val language = userDataRepository.internalData.map { "${it.language}-${it.region}" }.first()
            val page = params.key ?: 1
            val response = apis.getMovieReviews(movieId = id, language = language, page = page)

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