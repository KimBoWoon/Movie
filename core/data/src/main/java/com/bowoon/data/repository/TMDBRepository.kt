package com.bowoon.data.repository

import com.bowoon.model.TMDBConfiguration
import com.bowoon.model.TMDBMovieDetail
import com.bowoon.model.TMDBSearch
import com.bowoon.model.Upcoming
import kotlinx.coroutines.flow.Flow

interface TMDBRepository {
    val posterUrl: Flow<String>
    fun getConfiguration(): Flow<TMDBConfiguration>
    fun getUpcomingMovies(): Flow<Upcoming>
    fun searchMovies(query: String): Flow<TMDBSearch>
    fun getMovieDetail(id: Int): Flow<TMDBMovieDetail>
    fun discoverMovie(
        releaseDateGte: String,
        releaseDateLte: String
    ): Flow<TMDBSearch>
}