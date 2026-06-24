package com.cheeke.surfy.testing.repository

import androidx.annotation.VisibleForTesting
import androidx.paging.PagingSource
import androidx.paging.testing.asPagingSourceFactory
import com.cheeke.surfy.data.repository.MovieDataBaseRepository
import com.cheeke.surfy.database.model.MovieEntity
import com.cheeke.surfy.database.model.NowPlayingMovieEntity
import com.cheeke.surfy.database.model.UpComingMovieEntity
import com.cheeke.surfy.database.model.asExternalModel
import com.cheeke.surfy.model.Media
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.testing.model.nowPlayingMovieTest
import com.cheeke.surfy.testing.model.upComingMovieTest
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class TestMovieDatabaseRepository() : MovieDataBaseRepository {
    val movieDatabase = MutableSharedFlow<List<Movie>>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val currentMovieDatabase get() = movieDatabase.replayCache.firstOrNull().orEmpty()

    override fun getFavorite(): PagingSource<Int, MovieEntity> {
        println("getFavorite called size=${movieDatabase.replayCache.size}")

        return currentMovieDatabase.map(transform = Movie::asExternalModel)
            .asPagingSourceFactory()
            .invoke()
    }

    override fun isFavorite(id: Int): Flow<Boolean> =
        movieDatabase
            .map { medias ->
                medias.firstOrNull { it.id == id } != null
            }.distinctUntilChanged()

    override suspend fun insert(media: Media): Long {
        movieDatabase.emit(value = currentMovieDatabase + (media as Movie))
        return media.id?.toLong() ?: throw RuntimeException("room database insert failed...")
    }

    override suspend fun delete(media: Media) {
        movieDatabase.emit(value = currentMovieDatabase.filter { it.id != media.id })
    }

    override suspend fun upsert(medias: List<Media>) {
        movieDatabase.emit(
            value = (currentMovieDatabase + (medias as List<Movie>)).map { movie ->
                medias.find { it.id == movie.id } ?: movie
            }
        )
    }

    override suspend fun getNextWeekReleaseMovies(): List<Movie> {
        val now = LocalDate.now()
        val nextWeekReleaseMovies = currentMovieDatabase.filter { movie ->
            !movie.releaseDate?.trim().isNullOrEmpty() && LocalDate.parse(movie.releaseDate ?: "") in (now..now.plusDays(7))
        }
        movieDatabase.tryEmit(value = nextWeekReleaseMovies)
        return movieDatabase.first()
    }

    override suspend fun getPopularMovies(): List<Movie> = movieDatabase.map { movies ->
        movies.filter { movie -> (movie.voteCount ?: 0) > 500 && (movie.voteAverage ?: 0f) > 7.0f }
    }.first()

    override fun getUpComingMovies(): PagingSource<Int, UpComingMovieEntity> =
        upComingMovieTest
            .asPagingSourceFactory()
            .invoke()

    override fun getNowPlayingMovies(): PagingSource<Int, NowPlayingMovieEntity> =
        nowPlayingMovieTest
            .asPagingSourceFactory()
            .invoke()

    @VisibleForTesting
    suspend fun setMovies(list: List<Movie>) {
//        movieDatabase.tryEmit(value = list)
        movieDatabase.emit(value = list)
    }
}