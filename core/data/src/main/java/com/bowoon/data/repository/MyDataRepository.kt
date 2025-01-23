package com.bowoon.data.repository

import com.bowoon.model.MyData
import com.bowoon.model.tmdb.TMDBCertificationData
import com.bowoon.model.tmdb.TMDBConfiguration
import com.bowoon.model.tmdb.TMDBLanguageItem
import com.bowoon.model.tmdb.TMDBMovieGenres
import com.bowoon.model.tmdb.TMDBRegion
import kotlinx.coroutines.flow.Flow

interface MyDataRepository {
    val myData: Flow<MyData?>
    val posterUrl: Flow<String>
    suspend fun syncWith(): Boolean

    fun getConfiguration(): Flow<TMDBConfiguration>
    fun getCertification(): Flow<TMDBCertificationData>
    fun getGenres(): Flow<TMDBMovieGenres>
    fun getAvailableLanguage(): Flow<List<TMDBLanguageItem>>
    fun getAvailableRegion(): Flow<TMDBRegion>
}