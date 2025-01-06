package com.bowoon.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bowoon.common.Log
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.UpComingResult
import com.bowoon.network.ApiResponse
import com.bowoon.network.model.asExternalModel
import com.bowoon.network.retrofit.Apis
import javax.inject.Inject

class TMDBUpcomingPagingSource @Inject constructor(
    private val apis: Apis,
    private val userDataRepository: UserDataRepository
) : PagingSource<Int, UpComingResult>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UpComingResult> =
        runCatching {
            val language = "${userDataRepository.getLanguage()}-${userDataRepository.getRegion()}"
            val region = userDataRepository.getRegion()
            val imageQuality = userDataRepository.getImageQuality()

            when (val response = apis.tmdbApis.getUpcomingMovie(language = language, region = region, page = params.key ?: 1)) {
                is ApiResponse.Failure -> LoadResult.Error(response.throwable)
                is ApiResponse.Success -> {
                    LoadResult.Page(
                        data = response.data.results?.asExternalModel()?.map {
                            UpComingResult(
                                adult = it.adult,
                                backdropPath = it.backdropPath,
                                genreIds = it.genreIds,
                                id = it.id,
                                originalLanguage = it.originalLanguage,
                                originalTitle = it.originalTitle,
                                overview = it.overview,
                                popularity = it.popularity,
                                posterPath = "https://image.tmdb.org/t/p/$imageQuality${it.posterPath}",
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

    override fun getRefreshKey(state: PagingState<Int, UpComingResult>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey ?: anchorPage?.nextKey
        }
}