package com.bowoon.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bowoon.common.Log
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.Movie
import com.bowoon.model.SimilarMovie
import com.bowoon.network.MovieNetworkDataSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SimilarMoviePagingSource @Inject constructor(
    private val apis: MovieNetworkDataSource,
    private val id: Int,
    private val userDataRepository: UserDataRepository
) : PagingSource<Int, Movie>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> =
        runCatching {
            val language = userDataRepository.internalData.map { "${it.language}-${it.region}" }.first()
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