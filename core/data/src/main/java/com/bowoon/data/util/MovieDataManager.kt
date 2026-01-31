package com.bowoon.data.util

import com.bowoon.common.Dispatcher
import com.bowoon.common.Dispatchers
import com.bowoon.common.Log
import com.bowoon.common.Result
import com.bowoon.common.asResult
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
import com.bowoon.network.di.CoroutineNetwork
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

class MovieDataManager @Inject constructor(
    @param:Dispatcher(dispatcher = Dispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    @ApplicationScope appScope: CoroutineScope,
    @param:CoroutineNetwork private val apis: MovieNetworkDataSource,
    private val userDataRepository: UserDataRepository,
    private val datastore: InternalDataSource,
    networkMonitor: NetworkMonitor
) : DataManager {
    var language = ""
    var genres = Genres()

    private val cached = MutableStateFlow<MovieAppDataState?>(value = null)
    @OptIn(ExperimentalCoroutinesApi::class)
    override val movieAppData = networkMonitor.isOnline
        .distinctUntilChanged()
        .filter { it }
        .flatMapLatest { _ ->
            cached.value?.let { flowOf(value = it) } ?: loadData()
        }.onEach {
            if (it is MovieAppDataState.Success) {
                cached.value = it
            }
        }.stateIn(
            scope = appScope,
            started = SharingStarted.Lazily,
            initialValue = MovieAppDataState.Success(data = MovieAppData())
        )

    fun loadData(): Flow<MovieAppDataState> = combine(
        datastore.userData,
        getConfiguration(),
        getAvailableLanguage(),
        getAvailableRegion(),
        datastore.userData.map { internalData ->
            if (language != internalData.language) {
                language = internalData.language
                genres = apis.getGenres(language = "${internalData.language}-${internalData.region}")
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
    }.asResult()
        .map { result ->
            when (result) {
                is Result.Loading -> MovieAppDataState.Loading
                is Result.Success -> MovieAppDataState.Success(data = result.data)
                is Result.Error -> MovieAppDataState.Error(throwable = result.throwable)
            }
        }.flowOn(context = ioDispatcher)

    private fun getConfiguration(): Flow<Configuration> = flow {
        emit(value = apis.getConfiguration())
    }
    private fun getAvailableLanguage(): Flow<List<Language>> = flow {
        emit(value = apis.getAvailableLanguage())
    }
    private fun getAvailableRegion(): Flow<Regions> = flow {
        emit(value = apis.getAvailableRegion())
    }
}