package com.bowoon.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bowoon.common.Log
import com.bowoon.model.SearchType
import com.bowoon.model.tmdb.SearchResult
import com.bowoon.model.tmdb.TMDBSearchPeopleResult
import com.bowoon.model.tmdb.TMDBSearchResult
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
    private val posterUrl: Flow<String>
) : PagingSource<Int, SearchResult>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchResult> =
        runCatching {
            val url = posterUrl.first()
            when (type) {
                SearchType.MOVIE.label -> searchMovie(params, url)
                SearchType.PEOPLE.label -> searchPeople(params, url)
                else -> searchMovie(params, url)
            }
        }.getOrElse { e ->
            Log.printStackTrace(e)
            LoadResult.Error(e)
        }

    override fun getRefreshKey(state: PagingState<Int, SearchResult>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey ?: anchorPage?.nextKey
        }

    private suspend fun searchMovie(params: LoadParams<Int>, url: String): LoadResult<Int, SearchResult> =
        when (val response = apis.tmdbApis.searchMovies(query = query, language = language, region = region, page = params.key ?: 1)) {
            is ApiResponse.Failure -> LoadResult.Error(response.throwable)
            is ApiResponse.Success -> {
                LoadResult.Page(
                    data = response.data.results?.asExternalModel()?.map {
                        TMDBSearchResult(
                            adult = it.adult,
                            backdropPath = it.backdropPath,
                            genreIds = it.genreIds,
                            id = it.id,
                            originalLanguage = it.originalLanguage,
                            originalTitle = it.originalTitle,
                            overview = it.overview,
                            popularity = it.popularity,
                            posterPath = "$url${it.posterPath}",
                            releaseDate = it.releaseDate,
                            title = it.title,
                            video = it.video,
                            voteAverage = it.voteAverage,
                            voteCount = it.voteCount
                        )
                    } ?: emptyList(),
                    prevKey = null,
                    nextKey = if ((response.data.totalPages ?: 1) > (params.key ?: 1)) (params.key ?: 1) + 1 else null
                )
            }
        }

    private suspend fun searchPeople(params: LoadParams<Int>, url: String): LoadResult<Int, SearchResult> =
        when (val response = apis.tmdbApis.searchPeople(query = query, language = language, region = region, page = params.key ?: 1)) {
            is ApiResponse.Failure -> LoadResult.Error(response.throwable)
            is ApiResponse.Success -> {
                LoadResult.Page(
                    data = response.data.results?.asExternalModel()?.map {
                        TMDBSearchPeopleResult(
                            adult = it.adult,
                            gender = it.gender,
                            id = it.id,
                            knownFor = it.knownFor,
                            knownForDepartment = it.knownForDepartment,
                            name = it.name,
                            originalName = it.originalName,
                            popularity = it.popularity,
                            profilePath = "$url${it.profilePath}"
                        )
                    } ?: emptyList(),
                    prevKey = null,
                    nextKey = if ((response.data.totalPages ?: 1) > (params.key ?: 1)) (params.key ?: 1) + 1 else null
                )
            }
        }
}