package com.bowoon.data.repository

import androidx.paging.PagingData
import com.bowoon.model.TMDBCombineCredits
import com.bowoon.model.TMDBConfiguration
import com.bowoon.model.TMDBExternalIds
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
    suspend fun searchMovies(query: String): Flow<PagingData<TMDBSearchResult>>
    fun getUpcomingMovies(): Flow<List<UpComingResult>>
    fun getMovieDetail(id: Int): Flow<TMDBMovieDetail>
    fun discoverMovie(
        releaseDateGte: String,
        releaseDateLte: String
    ): Flow<TMDBSearch>
    fun getConfiguration(): Flow<TMDBConfiguration>
    fun getAvailableLanguage(): Flow<List<TMDBLanguageItem>>
    fun getAvailableRegion(): Flow<TMDBRegion>
    fun getPeople(personId: Int): Flow<TMDBPeopleDetail>
    fun getCombineCredits(personId: Int): Flow<TMDBCombineCredits>
    fun getExternalIds(personId: Int): Flow<TMDBExternalIds>
    suspend fun syncWith(): Boolean
}