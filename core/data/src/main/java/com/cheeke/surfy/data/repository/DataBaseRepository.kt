package com.cheeke.surfy.data.repository

import androidx.paging.PagingSource
import com.cheeke.surfy.database.model.MovieEntity
import com.cheeke.surfy.database.model.NowPlayingMovieEntity
import com.cheeke.surfy.database.model.PeopleEntity
import com.cheeke.surfy.database.model.TvEntity
import com.cheeke.surfy.database.model.UpComingMovieEntity
import com.cheeke.surfy.model.Media
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.model.Tv
import kotlinx.coroutines.flow.Flow

interface DataBaseRepository<T : Any> {
    fun getFavorite(): PagingSource<Int, T>
    fun isFavorite(id: Int): Flow<Boolean>
    suspend fun insert(media: Media): Long
    suspend fun delete(media: Media)
    suspend fun upsert(medias: List<Media>)
}

interface MovieDataBaseRepository : DataBaseRepository<MovieEntity> {
    fun getUpComingMovies(): PagingSource<Int, UpComingMovieEntity>
    fun getNowPlayingMovies(): PagingSource<Int, NowPlayingMovieEntity>
    suspend fun getPopularMovies(): List<Movie>
    suspend fun getNextWeekReleaseMovies(): List<Movie>
}

interface PeopleDataBaseRepository : DataBaseRepository<PeopleEntity> {}

interface TvDataBaseRepository : DataBaseRepository<TvEntity> {
    suspend fun getNextWeekReleaseTvs(): List<Tv>
}