package com.bowoon.data.repository

import com.bowoon.common.Log
import com.bowoon.data.util.suspendRunCatching
import com.bowoon.datastore.InternalDataSource
import com.bowoon.model.Configuration
import com.bowoon.model.ExternalData
import com.bowoon.model.Language
import com.bowoon.model.Regions
import com.bowoon.network.MovieNetworkDataSource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class MyDataRepositoryImpl @Inject constructor(
    private val apis: MovieNetworkDataSource,
    private val datastore: InternalDataSource
) : MyDataRepository {
//    override val externalData = MutableStateFlow<ExternalData?>(null)
    override val externalData = callbackFlow<ExternalData> {
        val language = "${datastore.getUserData().language}-${datastore.getUserData().region}"
        val configuration = apis.getConfiguration()
        val regions = apis.getAvailableRegion()
        val languages = apis.getAvailableLanguage()
        val genres = apis.getGenres(language)

        send(
            ExternalData(
                configuration = configuration,
                region = regions,
                language = languages,
                genres = genres
            )
        )

        awaitClose {
            Log.d("externalData coroutine close!")
        }
    }

    override suspend fun syncWith(): Boolean = suspendRunCatching {
//        val language = "${datastore.getUserData().language}-${datastore.getUserData().region}"
//        val configuration = apis.getConfiguration()
//        val regions = apis.getAvailableRegion()
//        val languages = apis.getAvailableLanguage()
//        val genres = apis.getGenres(language)
//
//        externalData.emit(
//            ExternalData(
//                configuration = configuration,
//                region = regions,
//                language = languages,
//                genres = genres
//            )
//        )
    }.isSuccess

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