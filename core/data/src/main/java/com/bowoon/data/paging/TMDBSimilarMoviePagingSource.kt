package com.bowoon.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bowoon.common.Log
import com.bowoon.model.SearchItem
import com.bowoon.model.tmdb.TMDBMovieDetailSimilarResult
import com.bowoon.network.ApiResponse
import com.bowoon.network.model.asExternalModel
import com.bowoon.network.retrofit.Apis
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class TMDBSimilarMoviePagingSource @Inject constructor(
    private val apis: Apis,
    private val id: Int,
    private val language: String,
    private val region: String,
    private val posterUrl: Flow<String>
) : PagingSource<Int, SearchItem>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchItem> =
        runCatching {
            val url = posterUrl.first()

            when (val response = apis.tmdbApis.getSimilarMovies(id = id, language = "$language-$region", page = params.key ?: 1)) {
                is ApiResponse.Failure -> LoadResult.Error(response.throwable)
                is ApiResponse.Success -> {
                    LoadResult.Page(
                        data = getSearchItem(response.data.results?.asExternalModel(), url),
                        prevKey = null,
                        nextKey = if ((response.data.totalPages ?: 1) > (params.key ?: 1)) (params.key ?: 1) + 1 else null
                    )
                }
            }
        }.getOrElse { e ->
            Log.printStackTrace(e)
            LoadResult.Error(e)
        }

    override fun getRefreshKey(state: PagingState<Int, SearchItem>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey ?: anchorPage?.nextKey
        }

    private fun getSearchItem(
        response: List<TMDBMovieDetailSimilarResult>?,
        url: String
    ): List<SearchItem> =
        response?.map {
            SearchItem(
                tmdbId = it.id,
                searchTitle = it.title,
                imagePath = "$url${it.posterPath}"
            )
        } ?: emptyList()
}