package com.cheeke.surfy.data.repository

import androidx.paging.PagingSource
import com.cheeke.surfy.database.model.MovieEntity
import com.cheeke.surfy.database.model.NowPlayingMovieEntity
import com.cheeke.surfy.database.model.PeopleEntity
import com.cheeke.surfy.database.model.TvEntity
import com.cheeke.surfy.database.model.UpComingMovieEntity
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.model.People
import com.cheeke.surfy.model.Tv
import kotlinx.coroutines.flow.Flow

interface DatabaseRepository {
    fun getMovies(): Flow<List<Movie>>
    fun isFavoriteMovie(id: Int): Flow<Boolean>
    suspend fun insertMovie(movie: Movie): Long
    suspend fun deleteMovie(movie: Movie)
    suspend fun upsertMovies(movies: List<Movie>)
    suspend fun getNextWeekReleaseMovies(): List<Movie>

    suspend fun getPopularMovies(): List<Movie>

    fun getPeople(): Flow<List<People>>
    fun isFavoritePeople(id: Int): Flow<Boolean>
    suspend fun insertPeople(people: People): Long
    suspend fun deletePeople(people: People)
    suspend fun upsertPeoples(peoples: List<People>)

    fun getFavoriteMovie(): PagingSource<Int, MovieEntity>
    fun getFavoritePeople(): PagingSource<Int, PeopleEntity>
    fun getFavoriteTv(): PagingSource<Int, TvEntity>

    fun getNowPlayingMovies(): PagingSource<Int, NowPlayingMovieEntity>
    fun getUpComingMovies(): PagingSource<Int, UpComingMovieEntity>

    fun getTv(): Flow<List<Tv>>
    fun isFavoriteTv(id: Int): Flow<Boolean>
    suspend fun insertTv(tv: Tv): Long
    suspend fun deleteTv(tv: Tv)
    suspend fun upsertTvs(tvs: List<Tv>)
    suspend fun getNextWeekReleaseTvs(): List<Tv>
}