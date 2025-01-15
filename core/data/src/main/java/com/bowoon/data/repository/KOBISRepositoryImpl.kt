package com.bowoon.data.repository

import com.bowoon.model.kobis.KOBISBoxOffice
import com.bowoon.network.ApiResponse
import com.bowoon.network.model.asExternalModel
import com.bowoon.network.retrofit.Apis
import javax.inject.Inject

class KOBISRepositoryImpl @Inject constructor(
    private val apis: Apis
) : KOBISRepository {
    override suspend fun getDailyBoxOffice(key: String, targetDt: String): KOBISBoxOffice =
        when (val response = apis.kobisApis.getDailyBoxOffice(key, targetDt)) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> response.data.asExternalModel()
        }
}