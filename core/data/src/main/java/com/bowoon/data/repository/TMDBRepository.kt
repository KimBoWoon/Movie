package com.bowoon.data.repository

import androidx.paging.PagingData
import com.bowoon.model.Movie
import com.bowoon.model.UpComingResult
import com.bowoon.model.tmdb.TMDBCombineCredits
import com.bowoon.model.tmdb.TMDBExternalIds
import com.bowoon.model.tmdb.TMDBMovieDetail
import com.bowoon.model.tmdb.TMDBNowPlayingResult
import com.bowoon.model.tmdb.TMDBPeopleDetail
import com.bowoon.model.tmdb.TMDBSearch
import kotlinx.coroutines.flow.Flow

interface TMDBRepository {
    suspend fun searchMovies(type: String, query: String): Flow<PagingData<Movie>>
    suspend fun getNowPlaying(): List<TMDBNowPlayingResult>
    suspend fun getUpcomingMovies(): List<UpComingResult>
    fun getMovieDetail(id: Int): Flow<TMDBMovieDetail>
    suspend fun getSimilarMovies(id: Int): Flow<PagingData<Movie>>
    fun discoverMovie(
        releaseDateGte: String,
        releaseDateLte: String
    ): Flow<TMDBSearch>
    fun getPeople(personId: Int): Flow<TMDBPeopleDetail>
    fun getCombineCredits(personId: Int): Flow<TMDBCombineCredits>
    fun getExternalIds(personId: Int): Flow<TMDBExternalIds>
}