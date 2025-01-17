package com.bowoon.data.repository

import com.bowoon.data.util.suspendRunCatching
import com.bowoon.datastore.InternalDataSource
import com.bowoon.model.MyData
import com.bowoon.model.PosterSize
import com.bowoon.model.tmdb.TMDBCertificationData
import com.bowoon.model.tmdb.TMDBConfiguration
import com.bowoon.model.tmdb.TMDBLanguageItem
import com.bowoon.model.tmdb.TMDBMovieGenres
import com.bowoon.model.tmdb.TMDBRegion
import com.bowoon.model.tmdb.TMDBRegionResult
import com.bowoon.network.ApiResponse
import com.bowoon.network.model.asExternalModel
import com.bowoon.network.retrofit.Apis
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyDataRepositoryImpl @Inject constructor(
    private val apis: Apis,
    private val datastore: InternalDataSource
) : MyDataRepository {
    override val myData: StateFlow<MyData> = MutableStateFlow(MyData())
    override val posterUrl: Flow<String> = combine(
        myData.map { it.secureBaseUrl },
        datastore.userData.map { it.imageQuality }
    ) { secureBaseUrl, imageQuality ->
        "$secureBaseUrl$imageQuality"
    }

    override suspend fun syncWith(): Boolean = suspendRunCatching {
        val configuration = getConfiguration()
        val certification = getCertification()
        val genres = getGenres()
        val region = getAvailableRegion()
        val language = getAvailableLanguage()

        MyData(
            isAdult = datastore.isAdult(),
            mainUpdateLatestDate = datastore.getMainOfDate(),
            secureBaseUrl = configuration.images?.secureBaseUrl,
            certification = certification.certifications?.certifications,
            genres = genres.genres,
            region = region.results?.map {
                TMDBRegionResult(
                    englishName = it.englishName,
                    iso31661 = it.iso31661,
                    nativeName = it.nativeName,
                    isSelected = datastore.getRegion() == it.iso31661
                )
            },
            language = language.map {
                TMDBLanguageItem(
                    englishName = it.englishName,
                    iso6391 = it.iso6391,
                    name = it.name,
                    isSelected = datastore.getLanguage() == it.iso6391
                )
            },
            posterSize = configuration.images?.posterSizes?.map {
                PosterSize(
                    size = it,
                    isSelected = datastore.getImageQuality() == it
                )
            } ?: emptyList(),
        ).also {
            (myData as? MutableStateFlow)?.emit(it)
        }
    }.isSuccess

    private suspend fun getConfiguration(): TMDBConfiguration {
        return when (val response = apis.tmdbApis.getConfiguration()) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> response.data.asExternalModel()
        }
    }

    private suspend fun getCertification(): TMDBCertificationData {
        val language = datastore.getLanguage()
        val region = datastore.getRegion()

        return when (val response =
            apis.tmdbApis.getCertification(language = "$language-$region")) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> response.data.asExternalModel()
        }
    }

    private suspend fun getGenres(): TMDBMovieGenres {
        val language = datastore.getLanguage()
        val region = datastore.getRegion()

        return when (val response = apis.tmdbApis.getGenres(language = "$language-$region")) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> response.data.asExternalModel()
        }
    }

    private suspend fun getAvailableLanguage(): List<TMDBLanguageItem> {
        return when (val response = apis.tmdbApis.getAvailableLanguage()) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> response.data.asExternalModel()
        }
    }

    private suspend fun getAvailableRegion(): TMDBRegion {
        return when (val response = apis.tmdbApis.getAvailableRegion()) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> response.data.asExternalModel()
        }
    }
}