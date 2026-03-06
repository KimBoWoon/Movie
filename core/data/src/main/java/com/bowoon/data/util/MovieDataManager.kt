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
import com.bowoon.model.Genre
import com.bowoon.model.Language
import com.bowoon.model.LocaleOption
import com.bowoon.model.MovieAppData
import com.bowoon.model.PosterSize
import com.bowoon.model.Regions
import com.bowoon.network.MovieNetworkDataSource
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

class MovieDataManager @Inject constructor(
    @param:Dispatcher(dispatcher = Dispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    @ApplicationScope appScope: CoroutineScope,
    private val apis: MovieNetworkDataSource,
    private val userDataRepository: UserDataRepository,
    private val datastore: InternalDataSource,
    networkMonitor: NetworkMonitor
) : DataManager {
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
    private val userDataFlow = datastore.userData.distinctUntilChanged()
    override val localeFlow: Flow<Locale> =
        userDataFlow
            .map { Locale(it.language, it.region) }
            .distinctUntilChanged()
    @OptIn(ExperimentalCoroutinesApi::class)
    private val genresFlow: Flow<GenreData> =
        localeFlow
            .flatMapLatest { key ->
                flow {
                    val language = "${key.language}-${key.region}"
                    val movie = apis.getMovieGenres(language = language)
                    val tv = apis.getTvGenres(language = language)
                    emit(value = GenreData(movie = movie.genres.orEmpty(), tv = tv.genres.orEmpty()))
                }
            }.distinctUntilChanged()
    private val configurationFlow: Flow<Configuration> =
        flow { emit(value = apis.getConfiguration()) }
            .stateIn(
                scope = appScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = Configuration()
            )
    private val availableLanguageFlow: Flow<List<Language>> =
        flow { emit(value = apis.getAvailableLanguage()) }
            .stateIn(
                scope = appScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = emptyList()
            )
    private val availableRegionFlow: Flow<Regions> =
        flow { emit(value = apis.getAvailableRegion()) }
            .stateIn(
                scope = appScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = Regions()
            )
    private val secureBaseUrlFlow: Flow<String> =
        combine(
            userDataFlow.map { it.imageQuality }.distinctUntilChanged(),
            configurationFlow.map { it.images?.secureBaseUrl.orEmpty() }.distinctUntilChanged()
        ) { quality, base ->
            "$base$quality"
        }.distinctUntilChanged()

    init {
        secureBaseUrlFlow
            .onEach { userDataRepository.updateSecureBaseUrl(value = it) }
            .flowOn(context = ioDispatcher)
            .launchIn(scope = appScope)
    }

    fun loadData(): Flow<MovieAppDataState> = combine(
        userDataFlow,
        configurationFlow,
        availableLanguageFlow,
        availableRegionFlow,
        genresFlow
    ) { internalData, configuration, language, region, genresPair ->
        Log.d("${configuration.images?.secureBaseUrl}${internalData.imageQuality}")
        Log.d("movieAppDataGenres -> $genresPair")

        MovieAppData(
            isAdult = internalData.isAdult,
            autoPlayTrailer = internalData.isAutoPlayTrailer,
            isDarkMode = internalData.isDarkMode,
            updateDate = internalData.updateDate,
            imageQuality = internalData.imageQuality,
            secureBaseUrl = configuration.images?.secureBaseUrl ?: "",
            movieGenres = genresPair.movie,
            tvGenres = genresPair.tv,
            region = region.results?.map {
                LocaleOption(code = it.iso31661 ?: "", label = it.nativeName ?: "", isSelected = internalData.region == it.iso31661)
            } ?: emptyList(),
            language = language.map {
                LocaleOption(code = it.iso6391 ?: "", label = it.englishName ?: "", isSelected = internalData.language == it.iso6391)
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
}

data class Locale(val language: String, val region: String)
data class GenreData(val movie: List<Genre>, val tv: List<Genre>)