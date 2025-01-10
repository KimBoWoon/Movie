package com.bowoon.data.repository

import androidx.paging.PagingData
import com.bowoon.model.MyData
import com.bowoon.model.TMDBConfiguration
import com.bowoon.model.TMDBLanguageItem
import com.bowoon.model.TMDBMovieDetail
import com.bowoon.model.TMDBPeopleDetail
import com.bowoon.model.TMDBRegion
import com.bowoon.model.TMDBSearch
import com.bowoon.model.TMDBSearchResult
import com.bowoon.model.UpComingResult
import kotlinx.coroutines.flow.Flow

interface TMDBRepository {
    val posterUrl: Flow<String>
    fun getConfiguration(): Flow<TMDBConfiguration>
    suspend fun searchMovies(query: String): Flow<PagingData<TMDBSearchResult>>
//    suspend fun getUpcomingMovies(): Flow<PagingData<UpComingResult>>
    fun getUpcomingMovies(): Flow<List<UpComingResult>>
    fun getMovieDetail(id: Int): Flow<TMDBMovieDetail>
    fun discoverMovie(
        releaseDateGte: String,
        releaseDateLte: String
    ): Flow<TMDBSearch>
    fun availableLanguage(): Flow<List<TMDBLanguageItem>>
    fun availableRegion(): Flow<TMDBRegion>
    fun getPeople(personId: Int): Flow<TMDBPeopleDetail>
    suspend fun syncWith(): Boolean
}