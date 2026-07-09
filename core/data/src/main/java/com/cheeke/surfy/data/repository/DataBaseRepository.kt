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
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface DataBaseRepository<T : Any> {
    fun getFavorite(): PagingSource<Int, T>
    fun isFavorite(id: Int): Observable<Boolean>
    fun insert(media: Media): Single<Long>
    fun delete(media: Media): Completable
    fun upsert(medias: List<Media>): Completable
}

interface MovieDataBaseRepository : DataBaseRepository<MovieEntity> {
    fun getUpComingMovies(): PagingSource<Int, UpComingMovieEntity>
    fun getNowPlayingMovies(): PagingSource<Int, NowPlayingMovieEntity>
    fun getPopularMovies(): Observable<List<Movie>>
    fun getNextWeekReleaseMovies(): Single<List<Movie>>
}

interface PeopleDataBaseRepository : DataBaseRepository<PeopleEntity> {}

interface TvDataBaseRepository : DataBaseRepository<TvEntity> {
    fun getNextWeekReleaseTvs(): Single<List<Tv>>
}