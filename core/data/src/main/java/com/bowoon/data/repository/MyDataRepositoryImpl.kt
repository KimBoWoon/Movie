package com.bowoon.data.repository

import com.bowoon.datastore.InternalDataSource
import com.bowoon.model.Configuration
import com.bowoon.model.ExternalData
import com.bowoon.model.Language
import com.bowoon.model.Regions
import com.bowoon.network.MovieNetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MyDataRepositoryImpl @Inject constructor(
    private val apis: MovieNetworkDataSource,
    private val dataStore: InternalDataSource
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
    }

    override fun getConfiguration(): Flow<Configuration> = flow {
        emit(apis.getConfiguration())
    }

    override fun getAvailableLanguage(): Flow<List<Language>> = flow {
        emit(apis.getAvailableLanguage())
    }

    override fun getAvailableRegion(): Flow<Regions> = flow {
        emit(apis.getAvailableRegion())
    }
}