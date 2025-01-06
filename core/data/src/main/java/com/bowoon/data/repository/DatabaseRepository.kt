package com.bowoon.data.repository

import com.bowoon.model.MovieDetail
import kotlinx.coroutines.flow.Flow

interface DatabaseRepository {
    fun getMovies(): Flow<List<MovieDetail>>
    suspend fun insert(movie: MovieDetail): Long
    suspend fun delete(movie: MovieDetail)
    suspend fun updateMovies(movies: List<MovieDetail>)
}