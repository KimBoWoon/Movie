package com.bowoon.data.repository

import com.bowoon.common.di.ApplicationScope
import com.bowoon.data.util.suspendRunCatching
import com.bowoon.datastore.InternalDataSource
import com.bowoon.model.MyData
import com.bowoon.model.PosterSize
import com.bowoon.model.RequestMyData
import com.bowoon.model.tmdb.TMDBCertificationData
import com.bowoon.model.tmdb.TMDBConfiguration
import com.bowoon.model.tmdb.TMDBLanguageItem
import com.bowoon.model.tmdb.TMDBMovieGenre
import com.bowoon.model.tmdb.TMDBMovieGenres
import com.bowoon.model.tmdb.TMDBRegion
import com.bowoon.model.tmdb.TMDBRegionResult
import com.bowoon.network.ApiResponse
import com.bowoon.network.model.asExternalModel
import com.bowoon.network.retrofit.Apis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyDataRepositoryImpl @Inject constructor(
    private val apis: Apis,
    private val datastore: InternalDataSource,
    @ApplicationScope scope: CoroutineScope
) : MyDataRepository {
    override val myData: Flow<MyData?> = combine(
        datastore.userData,
        requestMyData()
    ) { userdata, requestData ->
        MyData(
            isAdult = userdata.isAdult,
            mainUpdateLatestDate = userdata.updateDate,
            secureBaseUrl = userdata.secureBaseUrl,
            configuration = requestData.configuration,
            certification = requestData.certification,
            genres = requestData.genres?.genres?.map {
                TMDBMovieGenre(
                    id = it.id,
                    name = it.name
                )
            },
            region = requestData.region?.results?.map {
                TMDBRegionResult(
                    englishName = it.englishName,
                    iso31661 = it.iso31661,
                    nativeName = it.nativeName,
                    isSelected = datastore.getRegion() == it.iso31661
                )
            },
            language = requestData.language?.map {
                TMDBLanguageItem(
                    englishName = it.englishName,
                    iso6391 = it.iso6391,
                    name = it.name,
                    isSelected = datastore.getLanguage() == it.iso6391
                )
            },
            posterSize = requestData.posterSize
        )
    }.stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )
    override val posterUrl: Flow<String> = datastore.userData.map { "${it.secureBaseUrl}${it.imageQuality}" }

    private fun requestMyData(): Flow<RequestMyData> = combine(
        getConfiguration(),
        getCertification(),
        getGenres(),
        getAvailableRegion(),
        getAvailableLanguage()
    ) { configuration, certification, genres, region, language ->
        datastore.updateSecureBaseUrl(configuration.images?.secureBaseUrl ?: "")

        RequestMyData(
            configuration = configuration,
            certification = certification.certifications?.certifications,
            genres = genres,
            region = region,
            language = language,
            posterSize = configuration.images?.posterSizes?.map {
                PosterSize(
                    size = it,
                    isSelected = datastore.getImageQuality() == it
                )
            } ?: emptyList()
        )
    }

    override suspend fun syncWith(): Boolean = suspendRunCatching {
        myData.first()
    }.isSuccess

    override fun getConfiguration(): Flow<TMDBConfiguration> = flow {
        when (val response = apis.tmdbApis.getConfiguration()) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> emit(response.data.asExternalModel())
        }
    }

    override fun getCertification(): Flow<TMDBCertificationData> = flow {
        val language = datastore.getLanguage()
        val region = datastore.getRegion()

        when (val response = apis.tmdbApis.getCertification(language = "$language-$region")) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> emit(response.data.asExternalModel())
        }
    }

    override fun getGenres(): Flow<TMDBMovieGenres> = flow {
        val language = datastore.getLanguage()
        val region = datastore.getRegion()

        when (val response = apis.tmdbApis.getGenres(language = "$language-$region")) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> emit(response.data.asExternalModel())
        }
    }

    override fun getAvailableLanguage(): Flow<List<TMDBLanguageItem>> = flow {
        when (val response = apis.tmdbApis.getAvailableLanguage()) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> emit(response.data.asExternalModel())
        }
    }

    override fun getAvailableRegion(): Flow<TMDBRegion> = flow {
        when (val response = apis.tmdbApis.getAvailableRegion()) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> emit(response.data.asExternalModel())
        }
    }
}