package com.bowoon.data.repository

import com.bowoon.model.MyData
import com.bowoon.model.tmdb.TMDBCertificationData
import com.bowoon.model.tmdb.TMDBConfiguration
import com.bowoon.model.tmdb.TMDBLanguageItem
import com.bowoon.model.tmdb.TMDBMovieGenres
import com.bowoon.model.tmdb.TMDBRegion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface MyDataRepository {
    val myData: MutableStateFlow<MyData?>
    val posterUrl: Flow<String>
    fun combineMyData(): Flow<MyData>
    suspend fun syncWith(): Boolean

    fun getConfiguration(): Flow<TMDBConfiguration>
    fun getCertification(): Flow<TMDBCertificationData>
    fun getGenres(): Flow<TMDBMovieGenres>
    fun getAvailableLanguage(): Flow<List<TMDBLanguageItem>>
    fun getAvailableRegion(): Flow<TMDBRegion>
}