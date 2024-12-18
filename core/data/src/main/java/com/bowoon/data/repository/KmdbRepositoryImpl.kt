package com.bowoon.data.repository

import com.bowoon.model.KMDBMovieDetail
import com.bowoon.network.ApiResponse
import com.bowoon.network.model.asExternalModel
import com.bowoon.network.retrofit.Apis
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class KmdbRepositoryImpl @Inject constructor(
    private val apis: Apis
) : KmdbRepository {
    override fun getMovieInfo(url: String): Flow<KMDBMovieDetail> = flow {
        when (val response = apis.kmdbApis.getMovieInfo(url)) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> emit(response.data.asExternalModel())
        }
    }
}