package com.bowoon.data.repository

import com.bowoon.common.Log
import com.bowoon.datastore.InternalDataSource
import com.bowoon.model.Configuration
import com.bowoon.model.ExternalData
import com.bowoon.model.LanguageItem
import com.bowoon.model.MovieGenreList
import com.bowoon.model.RegionList
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

    override fun getGenres(): Flow<MovieGenreList> = datastore.userData.map {
        apis.getGenres(language = "${it.language}-${it.region}")
    }

    override fun getAvailableLanguage(): Flow<List<LanguageItem>> = flow {
        emit(apis.getAvailableLanguage())
    }

    override fun getAvailableRegion(): Flow<RegionList> = flow {
        emit(apis.getAvailableRegion())
    }
}