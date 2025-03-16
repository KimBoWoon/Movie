package com.bowoon.data.repository

import com.bowoon.common.Log
import com.bowoon.data.util.suspendRunCatching
import com.bowoon.datastore.InternalDataSource
import com.bowoon.model.CertificationData
import com.bowoon.model.Configuration
import com.bowoon.model.ExternalData
import com.bowoon.model.LanguageItem
import com.bowoon.model.MovieGenreList
import com.bowoon.model.RegionList
import com.bowoon.network.MovieNetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MyDataRepositoryImpl @Inject constructor(
    private val apis: MovieNetworkDataSource,
    private val datastore: InternalDataSource
) : MyDataRepository {
    override val externalData: Flow<ExternalData> = combine(
        getConfiguration(),
        getCertification(),
        getGenres(),
        getAvailableRegion(),
        getAvailableLanguage()
    ) { configuration, certification, genres, region, language ->
        ExternalData(
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
    }

    override suspend fun syncWith(): Boolean = suspendRunCatching {
        externalData.first()
    }.isSuccess

    override fun getConfiguration(): Flow<Configuration> = flow {
        emit(apis.getConfiguration())
    }

    override fun getCertification(): Flow<CertificationData> = flow {
        emit(apis.getCertification())
    }

    override fun getGenres(): Flow<MovieGenreList> = flow {
        val language = datastore.getUserData().language
        val region = datastore.getUserData().region

        emit(apis.getGenres(language = "$language-$region"))
    }

    override fun getAvailableLanguage(): Flow<List<LanguageItem>> = flow {
        emit(apis.getAvailableLanguage())
    }

    override fun getAvailableRegion(): Flow<RegionList> = flow {
        emit(apis.getAvailableRegion())
    }
}