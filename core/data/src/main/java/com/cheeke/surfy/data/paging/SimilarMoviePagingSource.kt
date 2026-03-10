package com.cheeke.surfy.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.cheeke.surfy.common.Log
import com.cheeke.surfy.data.repository.UserDataRepository
import com.cheeke.surfy.model.InternalData
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.model.SimilarMovie
import com.cheeke.surfy.network.MovieNetworkDataSource
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class SimilarMoviePagingSource @Inject constructor(
    private val apis: MovieNetworkDataSource,
    private val id: Int,
    private val userDataRepository: UserDataRepository
) : PagingSource<Int, Movie>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> =
        runCatching {
            val internalData = userDataRepository.internalData.firstOrNull() ?: InternalData()
            val language = "${internalData.language}-${internalData.region}"
            val response = apis.getSimilarMovies(id = id, language = language, page = params.key ?: 1)

            LoadResult.Page(
                data = getSearchItem(response.results),
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

    private fun getSearchItem(
        response: List<SimilarMovie>?
    ): List<Movie> =
        response?.map {
            Movie(
                id = it.id,
                title = it.title,
                posterPath = it.posterPath
            )
        } ?: emptyList()
}