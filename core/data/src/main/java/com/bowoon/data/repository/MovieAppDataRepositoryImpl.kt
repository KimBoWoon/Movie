package com.bowoon.data.repository

import com.bowoon.common.Log
import com.bowoon.common.di.ApplicationScope
import com.bowoon.datastore.InternalDataSource
import com.bowoon.model.Configuration
import com.bowoon.model.Genres
import com.bowoon.model.InternalData
import com.bowoon.model.Language
import com.bowoon.model.MovieAppData
import com.bowoon.model.PosterSize
import com.bowoon.model.Region
import com.bowoon.model.Regions
import com.bowoon.network.MovieNetworkDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class MovieAppDataRepositoryImpl @Inject constructor(
    @param:ApplicationScope private val appScope: CoroutineScope,
    private val apis: MovieNetworkDataSource,
    private val datastore: InternalDataSource
): MovieAppDataRepository {
    private var currentLanguage = ""
    private var currentGenres = Genres()

    init {
        appScope.launch {
            currentLanguage = "${datastore.getUserData().language}-${datastore.getUserData().region}"
        }
    }

    override val movieAppData: StateFlow<MovieAppData> = combine(
        getInitData(),
        datastore.userData.map {
            if ("${it.language}-${it.region}" != currentLanguage) {
                currentLanguage = "${it.language}-${it.region}"
                currentGenres = apis.getGenres(language = "${it.language}-${it.region}")
            }

            currentGenres
        }
    ) { initData, genres ->
        currentGenres = genres

        MovieAppData(
            isAdult = initData.internalData.isAdult,
            autoPlayTrailer = initData.internalData.autoPlayTrailer,
            isDarkMode = initData.internalData.isDarkMode,
            updateDate = initData.internalData.updateDate,
            mainMenu = initData.internalData.mainMenu,
            imageQuality = initData.internalData.imageQuality,
            secureBaseUrl = initData.configuration.images?.secureBaseUrl ?: "",
            genres = genres.genres ?: emptyList(),
            region = initData.regions.results?.map {
                Region(
                    englishName = it.englishName,
                    iso31661 = it.iso31661,
                    nativeName = it.nativeName,
                    isSelected =  initData.internalData.region == it.iso31661
                )
            } ?: emptyList(),
            language =initData.languages.map {
                Language(
                    englishName = it.englishName,
                    iso6391 = it.iso6391,
                    name = it.name,
                    isSelected = initData.internalData.language == it.iso6391
                )
            },
            posterSize = initData.configuration.images?.posterSizes?.map {
                PosterSize(
                    size = it,
                    isSelected = initData.internalData.imageQuality == it
                )
            } ?: emptyList()
        )
    }.catch {e ->
        Log.printStackTrace(tr = e)
    }.stateIn(
        scope = appScope,
        started = SharingStarted.Eagerly,
        initialValue = MovieAppData()
    )

    private fun getInitData(): Flow<InitData> = combine(
        getConfiguration(),
        getAvailableLanguage(),
        getAvailableRegion(),
        datastore.userData
    ) { configuration, languages, regions, internalData ->
        InitData(
            configuration = configuration,
            languages = languages,
            regions = regions,
            internalData = internalData,
        )
    }

    fun getConfiguration(): Flow<Configuration> = flow {
        emit(apis.getConfiguration())
    }

    fun getAvailableLanguage(): Flow<List<Language>> = flow {
        emit(apis.getAvailableLanguage())
    }

    fun getAvailableRegion(): Flow<Regions> = flow {
        emit(apis.getAvailableRegion())
    }

    fun getGenres(language: String): Flow<Genres> = flow {
        emit(apis.getGenres(language = language))
    }
}

private data class InitData(
    val configuration: Configuration,
    val languages: List<Language>,
    val regions: Regions,
    val internalData: InternalData
)