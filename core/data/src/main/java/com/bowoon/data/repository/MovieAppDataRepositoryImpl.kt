package com.bowoon.data.repository

import com.bowoon.common.di.ApplicationScope
import com.bowoon.datastore.InternalDataSource
import com.bowoon.model.Configuration
import com.bowoon.model.Genres
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class MovieAppDataRepositoryImpl @Inject constructor(
    @ApplicationScope private val appScope: CoroutineScope,
    private val apis: MovieNetworkDataSource,
    datastore: InternalDataSource
): MovieAppDataRepository {
    override val movieAppData: StateFlow<MovieAppData> = combine(
        getConfiguration(),
        getAvailableLanguage(),
        getAvailableRegion(),
        datastore.userData
    ) { configuration, languages, regions, internalData ->
        val genres = getGenres("${internalData.language}-${internalData.region}").firstOrNull()

        MovieAppData(
            isAdult = internalData.isAdult,
            autoPlayTrailer = internalData.autoPlayTrailer,
            isDarkMode = internalData.isDarkMode,
            updateDate = internalData.updateDate,
            mainMenu = internalData.mainMenu,
            imageQuality = internalData.imageQuality,
            secureBaseUrl = configuration.images?.secureBaseUrl,
            genres = genres?.genres ?: emptyList(),
            region = regions.results?.map {
                Region(
                    englishName = it.englishName,
                    iso31661 = it.iso31661,
                    nativeName = it.nativeName,
                    isSelected = internalData.region == it.iso31661
                )
            },
            language = languages.map {
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
    }.stateIn(
        scope = appScope,
        started = SharingStarted.Eagerly,
        initialValue = MovieAppData()
    )

    override fun getConfiguration(): Flow<Configuration> = flow {
        emit(apis.getConfiguration())
    }

    override fun getAvailableLanguage(): Flow<List<Language>> = flow {
        emit(apis.getAvailableLanguage())
    }

    override fun getAvailableRegion(): Flow<Regions> = flow {
        emit(apis.getAvailableRegion())
    }

    override fun getGenres(language: String): Flow<Genres> = flow {
        emit(apis.getGenres(language = language))
    }
}