package com.bowoon.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bowoon.common.Log
import com.bowoon.model.Movie
import com.bowoon.model.asExternalMovie
import com.bowoon.network.MovieNetworkDataSource
import javax.inject.Inject

class TMDBNowPlayingPagingSource @Inject constructor(
    private val apis: MovieNetworkDataSource,
    private val language: String,
    private val region: String,
    private val isAdult: Boolean,
    private val releaseDateGte: String,
    private val releaseDateLte: String
) : PagingSource<Int, Movie>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> =
        runCatching {
            val response = apis.getNowPlayingMovie(language = language, region = region, page = params.key ?: 1)
//            val response = apis.discoverMovie(
//                releaseDateGte = releaseDateGte,
//                releaseDateLte = releaseDateLte,
//                includeAdult = isAdult,
//                language = language,
//                region = region,
//                page = params.key ?: 1,
//                sortBy = "primary_release_date.asc",
//                withReleaseType = "2|3"
//            )

            LoadResult.Page(
                data = response.results?.map { it.asExternalMovie() } ?: emptyList(),
                prevKey = null,
                nextKey = if ((response.totalPages ?: 1) > (response.page ?: 1)) (params.key ?: 1) + 1 else null
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