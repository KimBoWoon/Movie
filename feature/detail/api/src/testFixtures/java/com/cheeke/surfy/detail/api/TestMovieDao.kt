package com.cheeke.surfy.detail.api

import androidx.paging.PagingSource
import androidx.paging.testing.asPagingSourceFactory
import com.cheeke.surfy.database.impl.dao.MovieDao
import com.cheeke.surfy.database.impl.model.MovieEntity
import com.cheeke.surfy.database.impl.model.NowPlayingMovieEntity
import com.cheeke.surfy.database.impl.model.UpComingMovieEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.time.LocalDate

class TestMovieDao : MovieDao {
    private val entitiesStateFlow = MutableStateFlow(value = emptyList<MovieEntity>())
    private val nowPlayingMovieFlow = MutableStateFlow(value = emptyList<NowPlayingMovieEntity>())
    private val upComingMovieFlow = MutableStateFlow(value = emptyList<UpComingMovieEntity>())

    override suspend fun insertOrIgnoreMovies(movie: MovieEntity): Long {
        entitiesStateFlow.update { oldValues ->
            (oldValues + movie).distinctBy(MovieEntity::id)
        }
        return movie.id.toLong()
    }

    override suspend fun upsertMovies(entities: List<MovieEntity>) {
        entitiesStateFlow.update { oldValues -> (entities + oldValues).distinctBy(MovieEntity::id) }
    }

    override suspend fun deleteMovie(id: Int) {
        entitiesStateFlow.update { entities -> entities.filterNot { it.id == id } }
    }

    override suspend fun getNextWeekReleaseMovies(): List<MovieEntity> = entitiesStateFlow.map { favoriteMovieList ->
        favoriteMovieList.filter {
            LocalDate.parse(it.releaseDate) in LocalDate.now()..LocalDate.now().plusDays(7)
        }.sortedWith(compareBy({ it.releaseDate }, { it.title }))
    }.first()

    override fun getNowPlayingMovie(): PagingSource<Int, NowPlayingMovieEntity> =
        (0..100).map {
            NowPlayingMovieEntity(
                releaseDate = "releaseDate_$it",
                title = "nowPlaying_$it",
                id = it,
                posterPath = "/imagePath_$it.png",
                voteCount = 687,
                voteAverage = 7.8f
            )
        }.asPagingSourceFactory().invoke()

    override fun getUpComingMovie(): PagingSource<Int, UpComingMovieEntity> =
        (0..100).map {
            UpComingMovieEntity(
                releaseDate = "releaseDate_$it",
                title = "nowPlaying_$it",
                id = it,
                posterPath = "/imagePath_$it.png",
                voteCount = 687,
                voteAverage = 7.8f
            )
        }.asPagingSourceFactory().invoke()

    override suspend fun deleteNowPlayingMovie() {
        nowPlayingMovieFlow.tryEmit(value = emptyList())
    }

    override suspend fun deleteUpComingMovie() {
        upComingMovieFlow.tryEmit(value = emptyList())
    }

    override suspend fun upsertNowPlayingMovie(entities: List<NowPlayingMovieEntity>) {
        nowPlayingMovieFlow.update { oldValues -> (entities + oldValues).distinctBy(NowPlayingMovieEntity::id) }
    }

    override suspend fun upsertUpComingMovie(entities: List<UpComingMovieEntity>) {
        upComingMovieFlow.update { oldValues -> (entities + oldValues).distinctBy(UpComingMovieEntity::id) }
    }

    override fun isFavoriteMovie(id: Int): Flow<Boolean> = entitiesStateFlow.map {
        it.find { entity -> entity.id == id } != null
    }.distinctUntilChanged()

    override fun deleteAllFavoriteMovies() {
        entitiesStateFlow.tryEmit(value = emptyList())
    }

    override fun getPopularMovies(): Flow<List<NowPlayingMovieEntity>> = nowPlayingMovieFlow

    override fun getFavoriteMovie(): PagingSource<Int, MovieEntity> =
        entitiesStateFlow.value.asPagingSourceFactory().invoke()
}