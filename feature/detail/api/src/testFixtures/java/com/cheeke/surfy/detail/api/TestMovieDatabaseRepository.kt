package com.cheeke.surfy.detail.api

import androidx.annotation.VisibleForTesting
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.testing.asPagingSourceFactory
import com.cheeke.surfy.detail.api.movie.MovieRepository
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.model.Review
import com.cheeke.surfy.model.SimilarMedia
import com.cheeke.surfy.testing.model.similarMoviesTestData
import com.cheeke.surfy.testing.model.testMovieReviews
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class TestMovieDatabaseRepository : MovieRepository {
    val movieDatabase = MutableSharedFlow<List<Movie>>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val currentMovieDatabase get() = movieDatabase.replayCache.firstOrNull().orEmpty()

    override fun getFavorite(): Flow<PagingData<Movie>> =
        flow { emit(value = PagingData.from(data = currentMovieDatabase)) }

    override fun isFavorite(id: Int): Flow<Boolean> =
        movieDatabase
            .map { medias ->
                medias.firstOrNull { it.id == id } != null
            }.distinctUntilChanged()

    override suspend fun insert(media: Movie): Long {
        movieDatabase.emit(value = currentMovieDatabase + media)
        return media.id?.toLong() ?: throw RuntimeException("room database insert failed...")
    }

    override suspend fun delete(media: Movie) {
        movieDatabase.emit(value = currentMovieDatabase.filter { it.id != media.id })
    }

    override suspend fun upsert(medias: List<Movie>) {
        movieDatabase.emit(
            value = (currentMovieDatabase + medias).map { movie ->
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

    override fun getUpComingMovies(): Flow<PagingData<Movie>> =
        flow { emit(value = PagingData.from(data = currentMovieDatabase)) }

    override fun getNowPlayingMovies(): Flow<PagingData<Movie>> =
        flow { emit(value = PagingData.from(data = currentMovieDatabase)) }

    @VisibleForTesting
    suspend fun setMovies(list: List<Movie>) {
//        movieDatabase.tryEmit(value = list)
        movieDatabase.emit(value = list)
    }

    override fun getSimilarMoviePagingSource(
        id: Int,
        language: String,
        region: String
    ): PagingSource<Int, SimilarMedia> = (similarMoviesTestData.results ?: emptyList()).asPagingSourceFactory().invoke()

    override fun getMovieReviews(
        movieId: Int,
        language: String,
        region: String
    ): PagingSource<Int, Review> = testMovieReviews.asPagingSourceFactory().invoke()
}