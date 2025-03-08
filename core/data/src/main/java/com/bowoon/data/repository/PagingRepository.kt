package com.bowoon.data.repository

import androidx.paging.PagingData
import com.bowoon.model.Movie
import kotlinx.coroutines.flow.Flow

interface PagingRepository {
//    suspend fun getNowPlaying(): Flow<PagingData<NowPlaying>>
//    suspend fun getUpcomingMovies(): Flow<PagingData<UpComingResult>>
    suspend fun searchMovies(type: String, query: String): Flow<PagingData<Movie>>
    suspend fun getSimilarMovies(id: Int): Flow<PagingData<Movie>>
}