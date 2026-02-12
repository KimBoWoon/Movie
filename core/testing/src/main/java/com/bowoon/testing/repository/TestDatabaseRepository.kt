package com.bowoon.testing.repository

import android.annotation.SuppressLint
import androidx.paging.PagingSource
import androidx.paging.testing.asPagingSourceFactory
import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.database.model.NowPlayingMovieEntity
import com.bowoon.database.model.UpComingMovieEntity
import com.bowoon.model.Movie
import com.bowoon.model.People
import com.bowoon.model.Tv
import com.bowoon.testing.model.nowPlayingMovieTest
import com.bowoon.testing.model.upComingMovieTest
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class TestDatabaseRepository : DatabaseRepository {
    val movieDatabase = MutableSharedFlow<List<Movie>>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val peopleDatabase = MutableSharedFlow<List<People>>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val tvDatabase = MutableSharedFlow<List<Tv>>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private val currentMovieDatabase get() = movieDatabase.replayCache.firstOrNull() ?: emptyList()
    private val currentPeopleDatabase get() = peopleDatabase.replayCache.firstOrNull() ?: emptyList()
    private val currentTvDatabase get() = tvDatabase.replayCache.firstOrNull() ?: emptyList()

    override fun getMovies(): Flow<List<Movie>> = movieDatabase

    override suspend fun insertMovie(movie: Movie): Long {
        movieDatabase.emit(value = currentMovieDatabase + movie)
        return movie.id?.toLong() ?: throw RuntimeException("room database insert failed...")
    }

    override suspend fun deleteMovie(movie: Movie) {
        movieDatabase.emit(value = currentMovieDatabase.filter { it.id != movie.id })
    }

    override suspend fun upsertMovies(movies: List<Movie>) {
        movieDatabase.emit(
            value = (currentMovieDatabase + movies).map { movie ->
                movies.find { it.id == movie.id } ?: movie
            }
        )
    }

    override fun getNextWeekReleaseMovies(): Flow<List<Movie>> {
        val now = LocalDate.now()
        val nextWeekReleaseMovies = currentMovieDatabase.filter { movie ->
            !movie.releaseDate?.trim().isNullOrEmpty() && LocalDate.parse(movie.releaseDate ?: "") in (now..now.plusDays(7))
        }
        movieDatabase.tryEmit(nextWeekReleaseMovies)
        return movieDatabase
    }

    override fun getPeople(): Flow<List<People>> = peopleDatabase

    override suspend fun insertPeople(people: People): Long {
        peopleDatabase.emit(value = currentPeopleDatabase + people)
        return people.id?.toLong() ?: throw RuntimeException("room database insert failed...")
    }

    override suspend fun deletePeople(people: People) {
        peopleDatabase.emit(value = currentPeopleDatabase.filter { it.id != people.id })
    }

    override suspend fun upsertPeoples(peoples: List<People>) {
        peopleDatabase.emit(
            value = (currentPeopleDatabase + peoples).map { people ->
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

    override fun isFavoriteMovie(id: Int): Flow<Boolean> = movieDatabase
        .map { movies ->
            movies.firstOrNull { it.id == id } != null
        }.distinctUntilChanged()

    override fun isFavoritePeople(id: Int): Flow<Boolean> = peopleDatabase
        .map { peoples ->
            peoples.firstOrNull { it.id == id } != null
        }.distinctUntilChanged()

    override fun getTv(): Flow<List<Tv>> = tvDatabase

    override fun isFavoriteTv(id: Int): Flow<Boolean> = tvDatabase
    .map { tvs ->
        tvs.firstOrNull { it.id == id } != null
    }.distinctUntilChanged()

    override suspend fun insertTv(tv: Tv): Long {
        tvDatabase.emit(value = currentTvDatabase + tv)
        return tv.id?.toLong() ?: throw RuntimeException("room database insert failed...")
    }

    override suspend fun deleteTv(tv: Tv) {
        tvDatabase.emit(value = currentTvDatabase.filter { it.id != tv.id })
    }

    override suspend fun upsertTvs(tvs: List<Tv>) {
        tvDatabase.emit(
            value = (currentTvDatabase + tvs).map { tv ->
                tvs.find { it.id == tv.id } ?: tv
            }
        )
    }

    override fun getNextWeekReleaseTvs(): Flow<List<Tv>> {
        val now = LocalDate.now()
        val nextWeekReleaseMovies = currentTvDatabase.filter { movie ->
            !movie.firstAirDate?.trim().isNullOrEmpty() && LocalDate.parse(movie.firstAirDate ?: "") in (now..now.plusDays(7))
        }
        tvDatabase.tryEmit(nextWeekReleaseMovies)
        return tvDatabase
    }
}