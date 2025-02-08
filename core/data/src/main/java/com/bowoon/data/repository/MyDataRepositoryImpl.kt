package com.bowoon.data.repository

import com.bowoon.common.Log
import com.bowoon.common.di.ApplicationScope
import com.bowoon.data.util.suspendRunCatching
import com.bowoon.datastore.InternalDataSource
import com.bowoon.model.CertificationData
import com.bowoon.model.Configuration
import com.bowoon.model.LanguageItem
import com.bowoon.model.MovieGenre
import com.bowoon.model.MovieGenreList
import com.bowoon.model.MyData
import com.bowoon.model.PosterSize
import com.bowoon.model.Region
import com.bowoon.model.RegionList
import com.bowoon.model.RequestMyData
import com.bowoon.network.ApiResponse
import com.bowoon.network.model.asExternalModel
import com.bowoon.network.retrofit.Apis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyDataRepositoryImpl @Inject constructor(
    @ApplicationScope private val appScope: CoroutineScope,
    private val apis: Apis,
    private val datastore: InternalDataSource
) : MyDataRepository {
    override val myData: Flow<MyData> = combine(
        datastore.userData,
        requestMyData()
    ) { userdata, requestData ->
        MyData(
            isAdult = userdata.isAdult,
            isDarkMode = userdata.isDarkMode,
            isAutoPlayTrailer = userdata.autoPlayTrailer,
            mainUpdateLatestDate = userdata.updateDate,
            secureBaseUrl = userdata.secureBaseUrl,
            configuration = requestData.configuration,
            certification = requestData.certification,
            genres = requestData.genres?.genres?.map {
                MovieGenre(
                    id = it.id,
                    name = it.name
                )
            },
            region = requestData.region?.results?.map {
                Region(
                    englishName = it.englishName,
                    iso31661 = it.iso31661,
                    nativeName = it.nativeName,
                    isSelected = userdata.region == it.iso31661
                )
            },
            language = requestData.language?.map {
                LanguageItem(
                    englishName = it.englishName,
                    iso6391 = it.iso6391,
                    name = it.name,
                    isSelected = userdata.language == it.iso6391
                )
            },
            posterSize = requestData.posterSize?.posterSizes?.map {
                PosterSize(
                    size = it,
                    isSelected = userdata.imageQuality == it
                )
            } ?: emptyList()
        )
    }.catch { e ->
        Log.printStackTrace(e)
    }.stateIn(
        scope = appScope,
        started = SharingStarted.Eagerly,
        initialValue = MyData()
    )
    override val posterUrl: Flow<String> = datastore.userData.map { "${it.secureBaseUrl}${it.imageQuality}" }

    override fun requestMyData(): Flow<RequestMyData> = combine(
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
            posterSize = configuration.images
        )
    }.catch { e ->
        Log.printStackTrace(e)
    }

    override suspend fun syncWith(): Boolean = suspendRunCatching {
        myData.first()
    }.isSuccess

    override fun getConfiguration(): Flow<Configuration> = flow {
        when (val response = apis.tmdbApis.getConfiguration()) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> emit(response.data.asExternalModel())
        }
    }

    override fun getCertification(): Flow<CertificationData> = flow {
        val language = datastore.getLanguage()
        val region = datastore.getRegion()

        when (val response = apis.tmdbApis.getCertification(language = "$language-$region")) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> emit(response.data.asExternalModel())
        }
    }

    override fun getGenres(): Flow<MovieGenreList> = flow {
        val language = datastore.getLanguage()
        val region = datastore.getRegion()

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