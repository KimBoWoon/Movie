package com.bowoon.data.repository

import com.bowoon.datastore.InternalDataSource
import com.bowoon.model.CombineCredits
import com.bowoon.model.ExternalIds
import com.bowoon.model.MovieDetail
import com.bowoon.model.MovieSearchData
import com.bowoon.model.PeopleDetailData
import com.bowoon.network.ApiResponse
import com.bowoon.network.model.asExternalModel
import com.bowoon.network.retrofit.Apis
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DetailRepositoryImpl @Inject constructor(
    private val apis: Apis,
    private val datastore: InternalDataSource
) : DetailRepository {
    override fun getMovieDetail(id: Int): Flow<MovieDetail> = flow {
        val language = datastore.getLanguage()
        val region = datastore.getRegion()

        when (val response = apis.tmdbApis.getMovieDetail(id = id, language = "$language-$region", region = region, includeImageLanguage = "$language,null")) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> emit(response.data.asExternalModel())
        }
    }

    override fun discoverMovie(
        releaseDateGte: String,
        releaseDateLte: String
    ): Flow<MovieSearchData> = flow {
        val language = "${datastore.getLanguage()}-${datastore.getRegion()}"
        val region = datastore.getRegion()
        val isAdult = datastore.isAdult()

        when (val response = apis.tmdbApis.discoverMovie(releaseDateGte = releaseDateGte, releaseDateLte = releaseDateLte, includeAdult = isAdult, language = language, region = region)) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> emit(response.data.asExternalModel())
        }
    }

    override fun getPeople(personId: Int): Flow<PeopleDetailData> = flow {
        val language = datastore.getLanguage()
        val region = datastore.getRegion()

        when (val response = apis.tmdbApis.getPeopleDetail(personId = personId, language = "$language-$region", includeImageLanguage = "$language,null")) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> emit(response.data.asExternalModel())
        }
    }

    override fun getCombineCredits(personId: Int): Flow<CombineCredits> = flow {
        val language = "${datastore.getLanguage()}-${datastore.getRegion()}"

        when (val response = apis.tmdbApis.getCombineCredits(personId = personId, language = language)) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> emit(response.data.asExternalModel())
        }
    }

    override fun getExternalIds(personId: Int): Flow<ExternalIds> = flow {
        when (val response = apis.tmdbApis.getExternalIds(personId = personId)) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> emit(response.data.asExternalModel())
        }
    }
}