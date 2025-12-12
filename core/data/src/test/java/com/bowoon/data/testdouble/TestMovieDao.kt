package com.bowoon.data.testdouble

import androidx.paging.PagingSource
import androidx.paging.testing.asPagingSourceFactory
import com.bowoon.database.dao.MovieDao
import com.bowoon.database.model.MovieEntity
import com.bowoon.database.model.NowPlayingMovieEntity
import com.bowoon.database.model.UpComingMovieEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.threeten.bp.LocalDate

class TestMovieDao : MovieDao {
    private val entitiesStateFlow = MutableStateFlow(value = emptyList<MovieEntity>())
    private val nowPlayingMovieFlow = MutableStateFlow(value = emptyList<NowPlayingMovieEntity>())
    private val upComingMovieFlow = MutableStateFlow(value = emptyList<UpComingMovieEntity>())

    override fun getMovieEntities(): Flow<List<MovieEntity>> = entitiesStateFlow

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

    override suspend fun getNextWeekReleaseMovies(): List<MovieEntity> = entitiesStateFlow.value.filter {
        LocalDate.parse(it.releaseDate) in LocalDate.now()..LocalDate.now().plusDays(7)
    }.sortedWith(compareBy({ it.releaseDate }, { it.title }))

    override fun getNowPlayingMovie(): PagingSource<Int, NowPlayingMovieEntity> =
        (0..100).map {
            NowPlayingMovieEntity(
                releaseDate = "releaseDate_$it",
                title = "nowPlaying_$it",
                id = it,
                posterPath = "/imagePath_$it.png"
            )
        }.asPagingSourceFactory().invoke()

    override fun getUpComingMovie(): PagingSource<Int, UpComingMovieEntity> =
        (0..100).map {
            UpComingMovieEntity(
                releaseDate = "releaseDate_$it",
                title = "nowPlaying_$it",
                id = it,
                posterPath = "/imagePath_$it.png"
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
}