package com.cheeke.surfy.datamanager.impl

import com.cheeke.surfy.common.Dispatcher
import com.cheeke.surfy.common.Dispatchers
import com.cheeke.surfy.common.Log
import com.cheeke.surfy.common.Result
import com.cheeke.surfy.common.asResult
import com.cheeke.surfy.common.di.ApplicationScope
import com.cheeke.surfy.datamanager.api.DataManager
import com.cheeke.surfy.datamanager.api.GenreData
import com.cheeke.surfy.datamanager.api.Locale
import com.cheeke.surfy.datamanager.api.SurfyAppDataState
import com.cheeke.surfy.model.Configuration
import com.cheeke.surfy.model.LocaleOption
import com.cheeke.surfy.model.PosterSize
import com.cheeke.surfy.model.Regions
import com.cheeke.surfy.model.SurfyAppData
import com.cheeke.surfy.network.api.NetworkMonitor
import com.cheeke.surfy.network.api.SettingRemoteDataSource
import com.cheeke.surfy.userdata.api.UserDataRepository
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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

class SurfyDataManager @Inject constructor(
    @param:Dispatcher(dispatcher = Dispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    @param:ApplicationScope private val appScope: CoroutineScope,
    private val apis: SettingRemoteDataSource,
    private val userDataRepository: UserDataRepository,
    networkMonitor: NetworkMonitor
) : DataManager {
    private val userDataFlow = userDataRepository.internalData.distinctUntilChanged()
    override val localeFlow: Flow<Locale> =
        userDataFlow
            .map { Locale(it.language, it.region) }
            .distinctUntilChanged()
    private val cached = MutableStateFlow<SurfyAppDataState?>(value = null)

    @OptIn(ExperimentalCoroutinesApi::class)
    override val surfyAppData: StateFlow<SurfyAppDataState> = networkMonitor.isOnline
        .distinctUntilChanged()
        .filter { it }
        .flatMapLatest {
            val current = cached.value
            if (current is SurfyAppDataState.Success) {
                flowOf(value = current)
            } else {
                loadData()
            }
        }.onEach { state ->
            if (state is SurfyAppDataState.Success) {
                cached.value = state
            }
        }.stateIn(
            scope = appScope,
            started = SharingStarted.Lazily,
            initialValue = SurfyAppDataState.Success(data = SurfyAppData())
        )

    init {
        combine(
            userDataFlow.map { it.imageQuality }.distinctUntilChanged(),
            flow { emit(value = apis.getConfiguration()) }
                .catch { e -> Log.printStackTrace(e); emit(value = Configuration()) }
                .map { it.images?.secureBaseUrl.orEmpty() }
                .distinctUntilChanged()
        ) { quality, base -> "$base$quality" }
            .distinctUntilChanged()
            .onEach { userDataRepository.updateSecureBaseUrl(value = it) }
            .flowOn(context = ioDispatcher)
            .launchIn(appScope)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val genresFlow: Flow<GenreData> =
        localeFlow
            .flatMapLatest { key ->
                flow {
                    val language = "${key.language}-${key.region}"
                    val movie = apis.getMovieGenres(language = language)
                    val tv = apis.getTvGenres(language = language)
                    emit(value = GenreData(movie = movie.genres.orEmpty(), tv = tv.genres.orEmpty()))
                }.catch { e -> Log.printStackTrace(e) }
            }
            .distinctUntilChanged()

    // private으로 변경
    private fun loadData(): Flow<SurfyAppDataState> = combine(
        userDataFlow,
        flow { emit(apis.getConfiguration()) }.catch { e -> Log.printStackTrace(e); emit(value = Configuration()) },
        flow { emit(apis.getAvailableLanguage()) }.catch { e -> Log.printStackTrace(e); emit(value = emptyList()) },
        flow { emit(apis.getAvailableRegion()) }.catch { e -> Log.printStackTrace(e); emit(value = Regions()) },
        genresFlow
    ) { internalData, configuration, language, region, genresPair ->
        SurfyAppData(
            isAdult = internalData.isAdult,
            autoPlayTrailer = internalData.isAutoPlayTrailer,
            isDarkMode = internalData.isDarkMode,
            updateDate = internalData.updateDate,
            imageQuality = internalData.imageQuality,
            secureBaseUrl = configuration.images?.secureBaseUrl.orEmpty(),
            movieGenres = genresPair.movie,
            tvGenres = genresPair.tv,
            region = region.results?.map {
                LocaleOption(
                    code = it.iso31661 ?: "",
                    label = it.nativeName ?: "",
                    isSelected = internalData.region == it.iso31661
                )
            }.orEmpty(),
            language = language.map {
                LocaleOption(
                    code = it.iso6391 ?: "",
                    label = it.englishName ?: "",
                    isSelected = internalData.language == it.iso6391
                )
            },
            posterSize = configuration.images?.posterSizes?.map {
                PosterSize(size = it, isSelected = internalData.imageQuality == it)
            }.orEmpty()
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