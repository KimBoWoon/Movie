package com.bowoon.testing.repository

import android.annotation.SuppressLint
import androidx.paging.PagingSource
import androidx.paging.testing.asPagingSourceFactory
import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.database.model.NowPlayingMovieEntity
import com.bowoon.database.model.UpComingMovieEntity
import com.bowoon.model.Movie
import com.bowoon.model.People
import com.bowoon.testing.model.nowPlayingMovieTest
import com.bowoon.testing.model.upComingMovieTest
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate

class TestDatabaseRepository : DatabaseRepository {
    val movieDatabase = MutableSharedFlow<List<Movie>>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val peopleDatabase = MutableSharedFlow<List<People>>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private val currentMovieDatabase get() = movieDatabase.replayCache.firstOrNull() ?: emptyList()
    private val currentPeopleDatabase get() = peopleDatabase.replayCache.firstOrNull() ?: emptyList()

    override fun getMovies(): Flow<List<Movie>> = movieDatabase

    override suspend fun insertMovie(movie: Movie): Long {
        movieDatabase.emit(currentMovieDatabase + movie)
        return movie.id?.toLong() ?: throw RuntimeException("room database insert failed...")
    }

    override suspend fun deleteMovie(movie: Movie) {
        movieDatabase.emit(currentMovieDatabase.filter { it.id != movie.id })
    }

    override suspend fun upsertMovies(movies: List<Movie>) {
        movieDatabase.emit(
            (currentMovieDatabase + movies).map { movie ->
                movies.find { it.id == movie.id } ?: movie
            }
        )
    }

    override suspend fun getNextWeekReleaseMovies(): List<Movie> {
        val now = LocalDate.now()
        val nextWeekReleaseMovies = currentMovieDatabase.filter { movie ->
            !movie.releaseDate?.trim().isNullOrEmpty() && LocalDate.parse(movie.releaseDate ?: "") in (now..now.plusDays(7))
        }
        movieDatabase.emit(nextWeekReleaseMovies)
        return nextWeekReleaseMovies
    }

    override fun getNextWeekReleaseMoviesFlow(): Flow<List<Movie>> {
        val now = LocalDate.now()
        val nextWeekReleaseMovies = currentMovieDatabase.filter { movie ->
            !movie.releaseDate?.trim().isNullOrEmpty() && LocalDate.parse(movie.releaseDate ?: "") in (now..now.plusDays(7))
        }
        movieDatabase.tryEmit(nextWeekReleaseMovies)
        return flow { emit(value = nextWeekReleaseMovies) }
    }

    override fun getPeople(): Flow<List<People>> = peopleDatabase

    override suspend fun insertPeople(people: People): Long {
        peopleDatabase.emit(currentPeopleDatabase + people)
        return people.id?.toLong() ?: throw RuntimeException("room database insert failed...")
    }

    override suspend fun deletePeople(people: People) {
        peopleDatabase.emit(currentPeopleDatabase.filter { it.id != people.id })
    }

    override suspend fun upsertPeoples(peoples: List<People>) {
        peopleDatabase.emit(
            (currentPeopleDatabase + peoples).map { people ->
                peoples.find { it.id == people.id } ?: people
            }
        )
    }

    @SuppressLint("VisibleForTests")
    override fun getNowPlayingMovies(): PagingSource<Int, NowPlayingMovieEntity> =
        nowPlayingMovieTest.asPagingSourceFactory().invoke()

    @SuppressLint("VisibleForTests")
    override fun getUpComingMovies(): PagingSource<Int, UpComingMovieEntity> =
        upComingMovieTest.asPagingSourceFactory().invoke()
}