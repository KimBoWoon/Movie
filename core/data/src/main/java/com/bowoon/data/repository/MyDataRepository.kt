package com.bowoon.data.repository

import androidx.compose.runtime.staticCompositionLocalOf
import com.bowoon.model.Configuration
import com.bowoon.model.ExternalData
import com.bowoon.model.Language
import com.bowoon.model.MovieAppData
import com.bowoon.model.Genres
import com.bowoon.model.Regions
import kotlinx.coroutines.flow.Flow

interface MyDataRepository {
    val externalData: Flow<ExternalData>
    fun getConfiguration(): Flow<Configuration>
    fun getGenres(): Flow<Genres>
    fun getAvailableLanguage(): Flow<List<Language>>
    fun getAvailableRegion(): Flow<Regions>
}

val LocalMovieAppDataComposition = staticCompositionLocalOf {
    MovieAppData()
}