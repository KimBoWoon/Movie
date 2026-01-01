package com.bowoon.data.util

import com.bowoon.common.Dispatcher
import com.bowoon.common.Dispatchers
import com.bowoon.common.Log
import com.bowoon.common.di.ApplicationScope
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.datastore.InternalDataSource
import com.bowoon.model.Configuration
import com.bowoon.model.Genres
import com.bowoon.model.Language
import com.bowoon.model.MovieAppData
import com.bowoon.model.PosterSize
import com.bowoon.model.Region
import com.bowoon.model.Regions
import com.bowoon.network.MovieNetworkDataSource
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MovieDataManager @Inject constructor(
    @param:Dispatcher(dispatcher = Dispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    @ApplicationScope appScope: CoroutineScope,
    private val apis: MovieNetworkDataSource,
    datastore: InternalDataSource,
    userDataRepository: UserDataRepository
) : DataManager {
    var language = ""
    var genres = Genres()

    override val movieAppData = combine(
        datastore.userData,
        getConfiguration(),
        getAvailableLanguage(),
        getAvailableRegion(),
        datastore.userData.map { internalData ->
            if (language != internalData.language) {
                language = internalData.language
                genres = runCatching {
                    apis.getGenres(language = "${internalData.language}-${internalData.region}")
                }.getOrElse { e ->
                    Log.e(e.message ?: "something wrong...")
                    Genres()
                }
            }
            genres
        }
    ) { internalData, configuration, language, region, genres ->
        Log.d("${configuration.images?.secureBaseUrl}${internalData.imageQuality}")
        Log.d("movieAppDataGenres -> $genres")

        userDataRepository.updateSecureBaseUrl(value = "${configuration.images?.secureBaseUrl}${internalData.imageQuality}")

        MovieAppData(
            isAdult = internalData.isAdult,
            autoPlayTrailer = internalData.isAutoPlayTrailer,
            isDarkMode = internalData.isDarkMode,
            updateDate = internalData.updateDate,
            imageQuality = internalData.imageQuality,
            secureBaseUrl = configuration.images?.secureBaseUrl ?: "",
            genres = genres.genres ?: emptyList(),
            region = region.results?.map {
                Region(
                    englishName = it.englishName,
                    iso31661 = it.iso31661,
                    nativeName = it.nativeName,
                    isSelected = internalData.region == it.iso31661
                )
            } ?: emptyList(),
            language = language.map {
                Language(
                    englishName = it.englishName,
                    iso6391 = it.iso6391,
                    name = it.name,
                    isSelected = internalData.language == it.iso6391
                )
            },
            posterSize = configuration.images?.posterSizes?.map {
                PosterSize(
                    size = it,
                    isSelected = internalData.imageQuality == it
                )
            } ?: emptyList()
        )
    }.catch { e ->
        Log.printStackTrace(tr = e)
    }.flowOn(context = ioDispatcher)
        .stateIn(
            scope = appScope,
            started = SharingStarted.Lazily,
            initialValue = MovieAppData()
        )

    private fun getConfiguration(): Flow<Configuration> = flow {
        runCatching {
            apis.getConfiguration()
        }.onSuccess {
            emit(value = it)
        }.onFailure { e ->
            Log.e(e.message ?: "something wrong...")
        }
    }
    private fun getAvailableLanguage(): Flow<List<Language>> = flow {
        runCatching {
            apis.getAvailableLanguage()
        }.onSuccess {
            emit(value = it)
        }.onFailure { e ->
            Log.e(e.message ?: "something wrong...")
        }
    }
    private fun getAvailableRegion(): Flow<Regions> = flow {
        runCatching {
            apis.getAvailableRegion()
        }.onSuccess {
            emit(value = it)
        }.onFailure { e ->
            Log.e(e.message ?: "something wrong...")
        }
    }
}