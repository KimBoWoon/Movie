package com.bowoon.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.bowoon.data.paging.TMDBSearchPagingSource
import com.bowoon.data.paging.TMDBUpcomingPagingSource
import com.bowoon.model.TMDBConfiguration
import com.bowoon.model.TMDBLanguageItem
import com.bowoon.model.TMDBMovieDetail
import com.bowoon.model.TMDBRegion
import com.bowoon.model.TMDBSearch
import com.bowoon.model.TMDBSearchResult
import com.bowoon.model.UpComingResult
import com.bowoon.network.ApiResponse
import com.bowoon.network.model.asExternalModel
import com.bowoon.network.retrofit.Apis
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TMDBRepositoryImpl @Inject constructor(
    private val apis: Apis,
    private val userDataRepository: UserDataRepository
) : TMDBRepository {
    override val posterUrl: Flow<String> = userDataRepository.userData.map { "https://image.tmdb.org/t/p/${it.imageQuality}" }

    override fun getConfiguration(): Flow<TMDBConfiguration> = flow {
        when (val response = apis.tmdbApis.getConfiguration()) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> emit(response.data.asExternalModel())
        }
    }

    override suspend fun searchMovies(
        query: String
    ): Flow<PagingData<TMDBSearchResult>> = Pager(
        config = PagingConfig(pageSize = 20, initialLoadSize = 20, prefetchDistance = 5),
        pagingSourceFactory = {
            TMDBSearchPagingSource(
                apis = apis,
                query = query,
                userDataRepository = userDataRepository
            )
        }
    ).flow

    override suspend fun getUpcomingMovies(): Flow<PagingData<UpComingResult>> = Pager(
        config = PagingConfig(pageSize = 20, initialLoadSize = 20, prefetchDistance = 5),
        pagingSourceFactory = {
            TMDBUpcomingPagingSource(
                apis = apis,
                userDataRepository = userDataRepository
            )
        }
    ).flow

    override fun getMovieDetail(id: Int): Flow<TMDBMovieDetail> = flow {
        val language = "${userDataRepository.getLanguage()}-${userDataRepository.getRegion()}"
        val region = userDataRepository.getRegion()

        when (val response = apis.tmdbApis.getMovieDetail(id = id, language = language, region = region)) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> emit(response.data.asExternalModel())
        }
    }

    override fun discoverMovie(
        releaseDateGte: String,
        releaseDateLte: String
    ): Flow<TMDBSearch> = flow {
        when (val response = apis.tmdbApis.discoverMovie(releaseDateGte = releaseDateGte, releaseDateLte = releaseDateLte)) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> emit(response.data.asExternalModel())
        }
    }

    override fun availableLanguage(): Flow<List<TMDBLanguageItem>> = flow {
        when (val response = apis.tmdbApis.getAvailableLanguage()) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> emit(response.data.asExternalModel())
        }
    }

    override fun availableRegion(): Flow<TMDBRegion> = flow {
        when (val response = apis.tmdbApis.getAvailableRegion()) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> emit(response.data.asExternalModel())
        }
    }
}