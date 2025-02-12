package com.bowoon.data.repository

import com.bowoon.model.CertificationData
import com.bowoon.model.Configuration
import com.bowoon.model.LanguageItem
import com.bowoon.model.MovieGenreList
import com.bowoon.model.RegionList
import com.bowoon.model.TMDBConfiguration
import kotlinx.coroutines.flow.Flow

interface MyDataRepository {
    val tmdbConfiguration: Flow<TMDBConfiguration>
    val posterUrl: Flow<String>
    suspend fun syncWith(): Boolean
    fun getConfiguration(): Flow<Configuration>
    fun getCertification(): Flow<CertificationData>
    fun getGenres(): Flow<MovieGenreList>
    fun getAvailableLanguage(): Flow<List<LanguageItem>>
    fun getAvailableRegion(): Flow<RegionList>
}