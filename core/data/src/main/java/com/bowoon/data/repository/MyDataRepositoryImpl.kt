package com.bowoon.data.repository

import com.bowoon.common.Log
import com.bowoon.datastore.InternalDataSource
import com.bowoon.model.Configuration
import com.bowoon.model.ExternalData
import com.bowoon.model.Language
import com.bowoon.model.Genres
import com.bowoon.model.Regions
import com.bowoon.network.MovieNetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MyDataRepositoryImpl @Inject constructor(
    private val apis: MovieNetworkDataSource,
    private val datastore: InternalDataSource
) : MyDataRepository {
    override val externalData: Flow<ExternalData> = combine(
        getConfiguration(),
        getAvailableRegion(),
        getAvailableLanguage()
    ) { configuration, region, language ->
        ExternalData(
            configuration = configuration,
            region = region,
            language = language
        )
    }.catch { e ->
        Log.printStackTrace(e)
    }

    override fun getConfiguration(): Flow<Configuration> = flow {
        emit(apis.getConfiguration())
    }

    override fun getGenres(): Flow<Genres> = datastore.userData.map {
        apis.getGenres(language = "${it.language}-${it.region}")
    }

    override fun getAvailableLanguage(): Flow<List<Language>> = flow {
        emit(apis.getAvailableLanguage())
    }

    override fun getAvailableRegion(): Flow<Regions> = flow {
        emit(apis.getAvailableRegion())
    }
}