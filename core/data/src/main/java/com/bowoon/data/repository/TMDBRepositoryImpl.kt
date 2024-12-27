package com.bowoon.data.repository

import com.bowoon.model.TMDBConfiguration
import com.bowoon.model.TMDBMovieDetail
import com.bowoon.model.TMDBSearch
import com.bowoon.model.Upcoming
import com.bowoon.network.ApiResponse
import com.bowoon.network.model.asExternalModel
import com.bowoon.network.retrofit.Apis
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TMDBRepositoryImpl @Inject constructor(
    private val apis: Apis,
    private val userDataRepository: UserDataRepository
) : TMDBRepository {
    override fun getConfiguration(): Flow<TMDBConfiguration> = flow {
        when (val response = apis.tmdbApis.getConfiguration()) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> emit(response.data.asExternalModel())
        }
    }

    override fun getUpcomingMovies(): Flow<Upcoming> = flow {
        val language = userDataRepository.getLanguage()
        val region = userDataRepository.getRegion()

        when (val response = apis.tmdbApis.getUpcomingMovie(language = language ?: "ko-KR", region = region ?: "KR")) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> emit(response.data.asExternalModel())
        }
    }

    override fun searchMovies(
        query: String
    ): Flow<TMDBSearch> = flow {
        val language = userDataRepository.getLanguage()
        val region = userDataRepository.getRegion()

        when (val response = apis.tmdbApis.searchMovies(query, language ?: "ko-KR", region ?: "KR")) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> emit(response.data.asExternalModel())
        }
    }

    override fun getMovieDetail(id: Int): Flow<TMDBMovieDetail> = flow {
        val language = userDataRepository.getLanguage()
        val region = userDataRepository.getRegion()

        when (val response = apis.tmdbApis.getMovieDetail(id = id, language = language ?: "ko-KR", region = region ?: "KR")) {
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
}