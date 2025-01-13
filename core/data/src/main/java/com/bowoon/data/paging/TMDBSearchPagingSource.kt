package com.bowoon.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bowoon.common.Log
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.TMDBSearchResult
import com.bowoon.network.ApiResponse
import com.bowoon.network.model.asExternalModel
import com.bowoon.network.retrofit.Apis
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TMDBSearchPagingSource @Inject constructor(
    private val apis: Apis,
    private val query: String,
    private val userDataRepository: UserDataRepository
) : PagingSource<Int, TMDBSearchResult>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TMDBSearchResult> =
        runCatching {
            val language = "${userDataRepository.getLanguage()}-${userDataRepository.getRegion()}"
            val region = userDataRepository.getRegion()
            val imageQuality = userDataRepository.getImageQuality()
            val posterUrl = userDataRepository.userData.map { it.myData?.secureBaseUrl }.firstOrNull()

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
                                posterPath = "$posterUrl$imageQuality${it.posterPath}",
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
        }.getOrElse { e ->
            Log.printStackTrace(e)
            LoadResult.Error(e)
        }

    override fun getRefreshKey(state: PagingState<Int, TMDBSearchResult>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey ?: anchorPage?.nextKey
        }
}