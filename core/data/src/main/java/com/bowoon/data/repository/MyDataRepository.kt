package com.bowoon.data.repository

import androidx.compose.runtime.staticCompositionLocalOf
import com.bowoon.model.CertificationData
import com.bowoon.model.Configuration
import com.bowoon.model.ExternalData
import com.bowoon.model.MovieAppData
import com.bowoon.model.LanguageItem
import com.bowoon.model.MovieGenreList
import com.bowoon.model.RegionList
import kotlinx.coroutines.flow.Flow

interface MyDataRepository {
    val externalData: Flow<ExternalData>
    suspend fun syncWith(): Boolean
    fun getConfiguration(): Flow<Configuration>
    fun getCertification(): Flow<CertificationData>
    fun getGenres(): Flow<MovieGenreList>
    fun getAvailableLanguage(): Flow<List<LanguageItem>>
    fun getAvailableRegion(): Flow<RegionList>
}

val LocalMovieAppDataComposition = staticCompositionLocalOf {
    MovieAppData()
}