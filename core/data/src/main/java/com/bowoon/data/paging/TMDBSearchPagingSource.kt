package com.bowoon.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bowoon.common.Log
import com.bowoon.model.Movie
import com.bowoon.model.MovieSearchItem
import com.bowoon.model.PeopleSearchItem
import com.bowoon.model.SearchResult
import com.bowoon.model.SearchType
import com.bowoon.network.MovieNetworkDataSource
import javax.inject.Inject

class TMDBSearchPagingSource @Inject constructor(
    private val apis: MovieNetworkDataSource,
    private val type: SearchType,
    private val query: String,
    private val language: String,
    private val region: String,
    private val isAdult: Boolean
) : PagingSource<Int, Movie>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> =
        runCatching {
            when (type) {
                SearchType.MOVIE -> searchMovie(params, isAdult)
                SearchType.PEOPLE -> searchPeople(params, isAdult)
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

    private suspend fun searchMovie(params: LoadParams<Int>, isAdult: Boolean): LoadResult<Int, Movie> {
        val response = apis.searchMovies(query = query, includeAdult = isAdult, language = language, region = region, page = params.key ?: 1)

        return LoadResult.Page(
            data = getSearchItem(response.results),
            prevKey = null,
            nextKey = if ((response.totalPages ?: 1) > (params.key ?: 1)) (params.key ?: 1) + 1 else null
        )
    }

    private suspend fun searchPeople(params: LoadParams<Int>, isAdult: Boolean): LoadResult<Int, Movie> {
        val response = apis.searchPeople(query = query, includeAdult = isAdult, language = language, region = region, page = params.key ?: 1)

        return LoadResult.Page(
            data = getSearchItem(response.results),
            prevKey = null,
            nextKey = if ((response.totalPages ?: 1) > (params.key ?: 1)) (params.key ?: 1) + 1 else null
        )
    }

    private fun getSearchItem(
        response: List<SearchResult>?
    ): List<Movie> =
        response?.map {
            when (it) {
                is MovieSearchItem -> {
                    Movie(
                        id = it.tmdbId,
                        title = it.searchTitle,
                        posterPath = it.imagePath,
                        genreIds = it.genreIds
                    )
                }
                is PeopleSearchItem -> {
                    Movie(
                        id = it.tmdbId,
                        title = it.searchTitle,
                        posterPath = it.imagePath
                    )
                }
                else -> {
                    Movie(
                        id = it.tmdbId,
                        title = it.searchTitle,
                        posterPath = it.imagePath
                    )
                }
            }
        } ?: emptyList()
}