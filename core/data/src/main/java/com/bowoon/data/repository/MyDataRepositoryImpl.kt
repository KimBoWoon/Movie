package com.bowoon.data.repository

import com.bowoon.common.Log
import com.bowoon.common.di.ApplicationScope
import com.bowoon.data.util.suspendRunCatching
import com.bowoon.datastore.InternalDataSource
import com.bowoon.model.CertificationData
import com.bowoon.model.Configuration
import com.bowoon.model.LanguageItem
import com.bowoon.model.MovieGenreList
import com.bowoon.model.RegionList
import com.bowoon.model.TMDBConfiguration
import com.bowoon.network.ApiResponse
import com.bowoon.network.model.asExternalModel
import com.bowoon.network.retrofit.Apis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyDataRepositoryImpl @Inject constructor(
    @ApplicationScope private val scope: CoroutineScope,
    private val apis: Apis,
    private val datastore: InternalDataSource
) : MyDataRepository {
    override val tmdbConfiguration: Flow<TMDBConfiguration> = combine(
        getConfiguration(),
        getCertification(),
        getGenres(),
        getAvailableRegion(),
        getAvailableLanguage()
    ) { configuration, certification, genres, region, language ->
        datastore.updateSecureBaseUrl(configuration.images?.secureBaseUrl ?: "")

        TMDBConfiguration(
            secureBaseUrl = configuration.images?.secureBaseUrl,
            configuration = configuration,
            certification = certification.certifications?.certifications,
            genres = genres,
            region = region,
            language = language,
            posterSize = configuration.images
        )
    }.catch { e ->
        Log.printStackTrace(e)
    }.stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = TMDBConfiguration()
    )
    override val posterUrl: Flow<String> = datastore.userData.map { "${it.secureBaseUrl}${it.imageQuality}" }

    override suspend fun syncWith(): Boolean = suspendRunCatching {
//        myData.first()
    }.isSuccess

    override fun getConfiguration(): Flow<Configuration> = flow {
        when (val response = apis.tmdbApis.getConfiguration()) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> emit(response.data.asExternalModel())
        }
    }

    override fun getCertification(): Flow<CertificationData> = flow {
        val language = datastore.getUserData().language
        val region = datastore.getUserData().region

        when (val response = apis.tmdbApis.getCertification(language = "$language-$region")) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> emit(response.data.asExternalModel())
        }
    }

    override fun getGenres(): Flow<MovieGenreList> = flow {
        val language = datastore.getUserData().language
        val region = datastore.getUserData().region

        when (val response = apis.tmdbApis.getGenres(language = "$language-$region")) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> emit(response.data.asExternalModel())
        }
    }

    override fun getAvailableLanguage(): Flow<List<LanguageItem>> = flow {
        when (val response = apis.tmdbApis.getAvailableLanguage()) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> emit(response.data.asExternalModel())
        }
    }

    override fun getAvailableRegion(): Flow<RegionList> = flow {
        when (val response = apis.tmdbApis.getAvailableRegion()) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> emit(response.data.asExternalModel())
        }
    }
}