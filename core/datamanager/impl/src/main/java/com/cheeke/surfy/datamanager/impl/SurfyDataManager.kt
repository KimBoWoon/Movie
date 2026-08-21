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
import com.cheeke.surfy.model.InternalData
import com.cheeke.surfy.model.Language
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
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
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

    @OptIn(ExperimentalCoroutinesApi::class)
    private val remoteDataFlow: Flow<RemoteAppData> = localeFlow
        .flatMapLatest { locale ->
            var cachedForLocale: RemoteAppData? = null

            networkMonitor.isOnline
                .distinctUntilChanged()
                .filter { it }
                .flatMapLatest {
                    val current = cachedForLocale
                    if (current != null) {
                        flowOf(value = current)
                    } else {
                        loadRemoteData(locale = locale).onEach { cachedForLocale = it }
                    }
                }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val surfyAppData: StateFlow<SurfyAppDataState> = combine(
        userDataFlow,
        remoteDataFlow
    ) { internalData, remote ->
        buildSurfyAppData(internalData = internalData, remote = remote)
    }.asResult()
        .map { result ->
            when (result) {
                is Result.Loading -> SurfyAppDataState.Loading
                is Result.Success -> SurfyAppDataState.Success(data = result.data)
                is Result.Error -> SurfyAppDataState.Error(throwable = result.throwable)
            }
        }.flowOn(context = ioDispatcher)
        .stateIn(
            scope = appScope,
            started = SharingStarted.Lazily,
            initialValue = SurfyAppDataState.Loading
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

    private fun loadRemoteData(locale: Locale): Flow<RemoteAppData> = combine(
        flow<Configuration?> { emit(value = apis.getConfiguration()) }.catch { e -> Log.printStackTrace(tr = e); emit(value = null) },
        flow<List<Language>?> { emit(value = apis.getAvailableLanguage()) }.catch { e -> Log.printStackTrace(tr = e); emit(value = null) },
        flow<Regions?> { emit(value = apis.getAvailableRegion()) }.catch { e -> Log.printStackTrace(tr = e); emit(value = null) },
        flow {
            val language = "${locale.language}-${locale.region}"
            val movieDeferred = appScope.async { apis.getMovieGenres(language = language) }
            val tvDeferred = appScope.async { apis.getTvGenres(language = language) }
            emit(value = GenreData(movie = movieDeferred.await().genres.orEmpty(), tv = tvDeferred.await().genres.orEmpty()))
        }.catch { e -> Log.printStackTrace(tr = e); emit(value = GenreData(movie = emptyList(), tv = emptyList())) }
    ) { configuration, language, region, genres ->
        RemoteAppData(configuration = configuration, language = language, region = region, genres = genres)
    }.flowOn(context = ioDispatcher)

    private fun buildSurfyAppData(internalData: InternalData, remote: RemoteAppData): SurfyAppData =
        SurfyAppData(
            isAdult = internalData.isAdult,
            autoPlayTrailer = internalData.isAutoPlayTrailer,
            isDarkMode = internalData.isDarkMode,
            updateDate = internalData.updateDate,
            imageQuality = internalData.imageQuality,
            secureBaseUrl = remote.configuration?.images?.secureBaseUrl.orEmpty(),
            movieGenres = remote.genres.movie,
            tvGenres = remote.genres.tv,
            region = remote.region?.results?.map {
                LocaleOption(
                    code = it.iso31661 ?: "",
                    label = it.nativeName ?: "",
                    isSelected = internalData.region == it.iso31661
                )
            }.orEmpty(),
            language = remote.language?.map {
                LocaleOption(
                    code = it.iso6391 ?: "",
                    label = it.englishName ?: "",
                    isSelected = internalData.language == it.iso6391
                )
            } ?: emptyList(),
            posterSize = remote.configuration?.images?.posterSizes?.map {
                PosterSize(size = it, isSelected = internalData.imageQuality == it)
            }.orEmpty()
        )

    private data class RemoteAppData(
        val configuration: Configuration?,
        val language: List<Language>?,
        val region: Regions?,
        val genres: GenreData
    )
}