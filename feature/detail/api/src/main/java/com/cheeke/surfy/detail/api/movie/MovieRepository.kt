package com.cheeke.surfy.detail.api.movie

import androidx.paging.PagingData
import com.cheeke.surfy.detail.api.DataBaseRepository
import com.cheeke.surfy.model.Movie
import kotlinx.coroutines.flow.Flow

interface MovieRepository : DataBaseRepository<Movie> {
    fun getUpComingMovies(): Flow<PagingData<Movie>>
    fun getNowPlayingMovies(): Flow<PagingData<Movie>>
    suspend fun getPopularMovies(): List<Movie>
    suspend fun getNextWeekReleaseMovies(): List<Movie>
}