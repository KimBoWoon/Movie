package com.bowoon.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.bowoon.database.model.MovieEntity
import com.bowoon.database.model.NowPlayingMovieEntity
import com.bowoon.database.model.UpComingMovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query(value = "SELECT * FROM movies")
    fun getMovieEntities(): Flow<List<MovieEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreMovies(movie: MovieEntity): Long

    @Upsert
    suspend fun upsertMovies(entities: List<MovieEntity>)

    @Query(value = "DELETE FROM movies WHERE id = (:id)")
    suspend fun deleteMovie(id: Int)

    @Query(value = "SELECT * FROM movies WHERE releaseDate BETWEEN DATE('now', 'localtime') AND DATE('now', '+7 day', 'localtime') ORDER BY releaseDate ASC, title ASC")
    suspend fun getNextWeekReleaseMovies(): List<MovieEntity>

    @Query(value = "SELECT * FROM nowplayingmovie")
    fun getNowPlayingMovie(): PagingSource<Int, NowPlayingMovieEntity>

    @Query(value = "SELECT * FROM upcomingmovie")
    fun getUpComingMovie(): PagingSource<Int, UpComingMovieEntity>

    @Query(value = "DELETE FROM nowplayingmovie")
    suspend fun deleteNowPlayingMovie()

    @Query(value = "DELETE FROM upcomingmovie")
    suspend fun deleteUpComingMovie()

    @Upsert
    suspend fun upsertNowPlayingMovie(entities: List<NowPlayingMovieEntity>)

    @Upsert
    suspend fun upsertUpComingMovie(entities: List<UpComingMovieEntity>)
}