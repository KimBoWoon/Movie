package com.cheeke.surfy.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.cheeke.surfy.database.model.MovieEntity
import com.cheeke.surfy.database.model.NowPlayingMovieEntity
import com.cheeke.surfy.database.model.UpComingMovieEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

@Dao
interface MovieDao {
    @Query(value = "SELECT EXISTS(SELECT 1 FROM movies WHERE id = :id)")
    fun isFavoriteMovie(id: Int): Observable<Boolean>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertOrIgnoreMovies(movie: MovieEntity): Single<Long>

    @Upsert
    fun upsertMovies(entities: List<MovieEntity>): Completable

    @Query(value = "DELETE FROM movies WHERE id = (:id)")
    fun deleteMovie(id: Int): Completable

    @Query(value = "SELECT * FROM movies WHERE releaseDate BETWEEN DATE('now', 'localtime') AND DATE('now', '+7 day', 'localtime') ORDER BY releaseDate ASC, title ASC")
    fun getNextWeekReleaseMovies(): Single<List<MovieEntity>>

    @Query(value = "SELECT * FROM nowplayingmovie WHERE voteCount > 500 AND voteAverage > 7.0")
    fun getPopularMovies(): Observable<List<NowPlayingMovieEntity>>

    @Query(value = "SELECT * FROM movies ORDER BY timestamp DESC")
    fun getFavoriteMovie(): PagingSource<Int, MovieEntity>

    @Query(value = "SELECT * FROM nowplayingmovie")
    fun getNowPlayingMovie(): PagingSource<Int, NowPlayingMovieEntity>

    @Query(value = "SELECT * FROM upcomingmovie ORDER BY releaseDate ASC, title ASC")
    fun getUpComingMovie(): PagingSource<Int, UpComingMovieEntity>

    @Query(value = "DELETE FROM nowplayingmovie")
    fun deleteNowPlayingMovie(): Completable

    @Query(value = "DELETE FROM upcomingmovie")
    fun deleteUpComingMovie(): Completable

    @Upsert
    fun upsertNowPlayingMovie(entities: List<NowPlayingMovieEntity>): Completable

    @Upsert
    fun upsertUpComingMovie(entities: List<UpComingMovieEntity>): Completable

    @Query(value = "DELETE FROM movies")
    fun deleteAllFavoriteMovies()
}