package com.bowoon.data.repository

import com.bowoon.model.Configuration
import com.bowoon.model.Genres
import com.bowoon.model.Language
import com.bowoon.model.MovieAppData
import com.bowoon.model.Regions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface MovieAppDataRepository {
    val movieAppData: StateFlow<MovieAppData>

    fun getConfiguration(): Flow<Configuration>
    fun getAvailableLanguage(): Flow<List<Language>>
    fun getAvailableRegion(): Flow<Regions>
    fun getGenres(language: String): Flow<Genres>
}