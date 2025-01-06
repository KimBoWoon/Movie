package com.bowoon.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.bowoon.database.model.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query(value = " SELECT * FROM movies WHERE id = (:movieId)")
    fun getMovieEntity(movieId: Int): Flow<MovieEntity>

    @Query(value = "SELECT * FROM movies")
    fun getMovieEntities(): Flow<List<MovieEntity>>

    @Query(value = " SELECT * FROM movies WHERE id IN (:ids)")
    fun getMovieEntities(ids: Set<Int>): Flow<List<MovieEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreMovies(movie: MovieEntity): Long

    @Upsert
    suspend fun upsertMovies(entities: List<MovieEntity>)

    @Query(value = "DELETE FROM movies WHERE id in (:ids)")
    suspend fun deleteMovies(ids: List<Int>)

    @Query(value = "DELETE FROM movies WHERE id = (:id)")
    suspend fun deleteMovie(id: Int)
}