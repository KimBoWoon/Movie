package com.bowoon.data.repository

import androidx.compose.runtime.staticCompositionLocalOf
import com.bowoon.model.Configuration
import com.bowoon.model.ExternalData
import com.bowoon.model.Language
import com.bowoon.model.MovieAppData
import com.bowoon.model.Regions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface MyDataRepository {
    val externalData: Flow<ExternalData>
//    val externalData: StateFlow<ExternalData?>
    fun getConfiguration(): Flow<Configuration>
    fun getAvailableLanguage(): Flow<List<Language>>
    fun getAvailableRegion(): Flow<Regions>
    suspend fun syncWith(): Boolean
}

val LocalMovieAppDataComposition = staticCompositionLocalOf {
    MovieAppData()
}