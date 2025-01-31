package com.bowoon.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bowoon.common.Log
import com.bowoon.model.Movie
import com.bowoon.model.SearchType
import com.bowoon.model.SearchResult
import com.bowoon.network.ApiResponse
import com.bowoon.network.model.asExternalModel
import com.bowoon.network.retrofit.Apis
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class TMDBSearchPagingSource @Inject constructor(
    private val apis: Apis,
    private val type: String,
    private val query: String,
    private val language: String,
    private val region: String,
    private val isAdult: Boolean,
    private val posterUrl: Flow<String>
) : PagingSource<Int, Movie>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> =
        runCatching {
            val url = posterUrl.first()

            when (type) {
                SearchType.MOVIE.label -> searchMovie(params, isAdult, url)
                SearchType.PEOPLE.label -> searchPeople(params, isAdult, url)
                else -> searchMovie(params, isAdult, url)
            }
        }.getOrElse { e ->
            Log.printStackTrace(e)
            LoadResult.Error(e)
        }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey ?: anchorPage?.nextKey
        }

    private suspend fun searchMovie(params: LoadParams<Int>, isAdult: Boolean, url: String): LoadResult<Int, Movie> =
        when (val response = apis.tmdbApis.searchMovies(query = query, includeAdult = isAdult, language = language, region = region, page = params.key ?: 1)) {
            is ApiResponse.Failure -> LoadResult.Error(response.throwable)
            is ApiResponse.Success -> {
                LoadResult.Page(
                    data = getSearchItem(response.data.results?.asExternalModel(), url),
                    prevKey = null,
                    nextKey = if ((response.data.totalPages ?: 1) > (params.key ?: 1)) (params.key ?: 1) + 1 else null
                )
            }
        }

    private suspend fun searchPeople(params: LoadParams<Int>, isAdult: Boolean, url: String): LoadResult<Int, Movie> =
        when (val response = apis.tmdbApis.searchPeople(query = query, includeAdult = isAdult, language = language, region = region, page = params.key ?: 1)) {
            is ApiResponse.Failure -> LoadResult.Error(response.throwable)
            is ApiResponse.Success -> {
                LoadResult.Page(
                    data = getSearchItem(response.data.results?.asExternalModel(), url),
                    prevKey = null,
                    nextKey = if ((response.data.totalPages ?: 1) > (params.key ?: 1)) (params.key ?: 1) + 1 else null
                )
            }
        }

    private fun getSearchItem(
        response: List<SearchResult>?,
        url: String
    ): List<Movie> =
        response?.map {
            Movie(
                id = it.tmdbId,
                title = it.searchTitle,
                posterPath = "$url${it.imagePath}"
            )
        } ?: emptyList()
}