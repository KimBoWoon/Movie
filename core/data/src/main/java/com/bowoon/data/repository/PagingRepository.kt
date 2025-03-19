package com.bowoon.data.repository

import androidx.paging.PagingSource
import com.bowoon.model.Movie

interface PagingRepository {
//    suspend fun getNowPlaying(): Flow<PagingData<NowPlaying>>
//    suspend fun getUpcomingMovies(): Flow<PagingData<UpComingResult>>
    fun searchMovieSource(
        type: String,
        query: String,
        language: String,
        region: String,
        isAdult: Boolean
    ): PagingSource<Int, Movie>
    fun getSimilarMovies(id: Int, language: String, region: String): PagingSource<Int, Movie>
}