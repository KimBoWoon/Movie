package com.bowoon.data.repository

import com.bowoon.model.KOBISBoxOffice
import com.bowoon.model.KOBISMovieData
import com.bowoon.network.ApiResponse
import com.bowoon.network.model.asExternalModel
import com.bowoon.network.retrofit.Apis
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ViewModelScoped
class KobisRepositoryImpl @Inject constructor(
    private val apis: Apis
) : KobisRepository {
    override fun getDailyBoxOffice(key: String, targetDt: String): Flow<KOBISBoxOffice> = flow {
        when (val response = apis.kobisApis.getDailyBoxOffice(key, targetDt)) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> emit(response.data.asExternalModel())
        }
    }

    override fun getMovieInfo(key: String, movieCd: String): Flow<KOBISMovieData> = flow {
        when (val response = apis.kobisApis.getMovieInfo(key, movieCd)) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> emit(response.data.asExternalModel())
        }
    }
}