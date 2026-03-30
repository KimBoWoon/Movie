package com.cheeke.surfy.data.util

import com.cheeke.surfy.common.Dispatcher
import com.cheeke.surfy.common.Dispatchers
import com.cheeke.surfy.common.Log
import com.cheeke.surfy.common.Result
import com.cheeke.surfy.common.asResult
import com.cheeke.surfy.common.di.ApplicationScope
import com.cheeke.surfy.data.repository.UserDataRepository
import com.cheeke.surfy.datastore.InternalDataSource
import com.cheeke.surfy.model.Configuration
import com.cheeke.surfy.model.Genre
import com.cheeke.surfy.model.Language
import com.cheeke.surfy.model.LocaleOption
import com.cheeke.surfy.model.PosterSize
import com.cheeke.surfy.model.Regions
import com.cheeke.surfy.model.SurfyAppData
import com.cheeke.surfy.network.SettingRemoteDataSource
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
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
    private val apis: SettingRemoteDataSource,
    private val userDataRepository: UserDataRepository,
    datastore: InternalDataSource,
    networkMonitor: NetworkMonitor
) : DataManager {
    private val cached = MutableStateFlow<SurfyAppDataState?>(value = null)
    @OptIn(ExperimentalCoroutinesApi::class)
    override val surfyAppData = networkMonitor.isOnline
        .distinctUntilChanged()
        .filter { it }
        .flatMapLatest { _ ->
            cached.value?.let { flowOf(value = it) } ?: loadData()
        }.onEach {
            if (it is SurfyAppDataState.Success) {
                cached.value = it
            }
        }.stateIn(
            scope = appScope,
            started = SharingStarted.Lazily,
            initialValue = SurfyAppDataState.Success(data = SurfyAppData())
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
                }.catch { e -> Log.printStackTrace(tr = e) }
            }.distinctUntilChanged()
    private val configurationFlow: Flow<Configuration> =
        flow { emit(value = apis.getConfiguration()) }
            .catch { e -> Log.printStackTrace(tr = e) }
            .stateIn(
                scope = appScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = Configuration()
            )
    private val availableLanguageFlow: Flow<List<Language>> =
        flow { emit(value = apis.getAvailableLanguage()) }
            .catch { e -> Log.printStackTrace(tr = e) }
            .stateIn(
                scope = appScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = emptyList()
            )
    private val availableRegionFlow: Flow<Regions> =
        flow { emit(value = apis.getAvailableRegion()) }
            .catch { e -> Log.printStackTrace(tr = e) }
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

    fun loadData(): Flow<SurfyAppDataState> = combine(
        userDataFlow,
        configurationFlow,
        availableLanguageFlow,
        availableRegionFlow,
        genresFlow
    ) { internalData, configuration, language, region, genresPair ->
        Log.d("${configuration.images?.secureBaseUrl}${internalData.imageQuality}")
        Log.d("movieAppDataGenres -> $genresPair")

        SurfyAppData(
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
                is Result.Loading -> SurfyAppDataState.Loading
                is Result.Success -> SurfyAppDataState.Success(data = result.data)
                is Result.Error -> SurfyAppDataState.Error(throwable = result.throwable)
            }
        }.flowOn(context = ioDispatcher)
}

data class Locale(val language: String, val region: String)
data class GenreData(val movie: List<Genre>, val tv: List<Genre>)