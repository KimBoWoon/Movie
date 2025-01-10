package com.bowoon.data.repository

import com.bowoon.model.KOBISBoxOffice
import com.bowoon.network.ApiResponse
import com.bowoon.network.model.asExternalModel
import com.bowoon.network.retrofit.Apis
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class KobisRepositoryImpl @Inject constructor(
    private val apis: Apis
) : KobisRepository {
    override fun getDailyBoxOffice(key: String, targetDt: String): Flow<KOBISBoxOffice> = flow {
        when (val response = apis.kobisApis.getDailyBoxOffice(key, targetDt)) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> emit(response.data.asExternalModel())
        }
    }
}