package com.bowoon.data.repository

import androidx.paging.PagingSource
import com.bowoon.database.model.NowPlayingMovieEntity
import com.bowoon.database.model.UpComingMovieEntity
import com.bowoon.model.Movie
import com.bowoon.model.People
import kotlinx.coroutines.flow.Flow

interface DatabaseRepository {
    fun getMovies(): Flow<List<Movie>>
    suspend fun insertMovie(movie: Movie): Long
    suspend fun deleteMovie(movie: Movie)
    suspend fun upsertMovies(movies: List<Movie>)
    suspend fun getNextWeekReleaseMovies(): List<Movie>

    fun getPeople(): Flow<List<People>>
    suspend fun insertPeople(people: People): Long
    suspend fun deletePeople(people: People)
    suspend fun upsertPeoples(peoples: List<People>)

    fun getNowPlayingMovies(): PagingSource<Int, NowPlayingMovieEntity>
    fun getUpComingMovies(): PagingSource<Int, UpComingMovieEntity>
}