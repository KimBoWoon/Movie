package com.cheeke.surfy.database.impl.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.cheeke.surfy.database.impl.model.MovieEntity
import com.cheeke.surfy.database.impl.model.NowPlayingMovieEntity
import com.cheeke.surfy.database.impl.model.UpComingMovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query(value = "SELECT EXISTS(SELECT 1 FROM movies WHERE id = :id)")
    fun isFavoriteMovie(id: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreMovies(movie: MovieEntity): Long

    @Upsert
    suspend fun upsertMovies(entities: List<MovieEntity>)

    @Query(value = "DELETE FROM movies WHERE id = (:id)")
    suspend fun deleteMovie(id: Int)

    @Query(value = "SELECT * FROM movies WHERE releaseDate BETWEEN DATE('now', 'localtime') AND DATE('now', '+7 day', 'localtime') ORDER BY releaseDate ASC, title ASC")
    suspend fun getNextWeekReleaseMovies(): List<MovieEntity>

    @Query(value = "SELECT * FROM nowplayingmovie WHERE voteCount > 500 AND voteAverage > 7.0")
    fun getPopularMovies(): Flow<List<NowPlayingMovieEntity>>

    @Query(value = "SELECT * FROM movies ORDER BY timestamp DESC")
    fun getFavoriteMovie(): PagingSource<Int, MovieEntity>

    @Query(value = "SELECT * FROM nowplayingmovie")
    fun getNowPlayingMovie(): PagingSource<Int, NowPlayingMovieEntity>

    @Query(value = "SELECT * FROM upcomingmovie ORDER BY releaseDate ASC, title ASC")
    fun getUpComingMovie(): PagingSource<Int, UpComingMovieEntity>

    @Query(value = "DELETE FROM nowplayingmovie")
    suspend fun deleteNowPlayingMovie()

    @Query(value = "DELETE FROM upcomingmovie")
    suspend fun deleteUpComingMovie()

    @Upsert
    suspend fun upsertNowPlayingMovie(entities: List<NowPlayingMovieEntity>)

    @Upsert
    suspend fun upsertUpComingMovie(entities: List<UpComingMovieEntity>)

    @Query(value = "DELETE FROM movies")
    fun deleteAllFavoriteMovies()
}