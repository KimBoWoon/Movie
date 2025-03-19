package com.bowoon.testing.repository

import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.model.Favorite
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class TestDatabaseRepository : DatabaseRepository {
    val movieDatabase = MutableSharedFlow<List<Favorite>>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val peopleDatabase = MutableSharedFlow<List<Favorite>>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private val currentMovieDatabase get() = movieDatabase.replayCache.firstOrNull() ?: emptyList()
    private val currentPeopleDatabase get() = peopleDatabase.replayCache.firstOrNull() ?: emptyList()

    override fun getMovies(): Flow<List<Favorite>> = movieDatabase

    override suspend fun insertMovie(movie: Favorite): Long {
        movieDatabase.emit(currentMovieDatabase + movie)
        return movie.id?.toLong() ?: throw RuntimeException("room database insert failed...")
    }

    override suspend fun deleteMovie(movie: Favorite) {
        movieDatabase.emit(currentMovieDatabase.filter { it.id != movie.id })
    }

    override suspend fun upsertMovies(movies: List<Favorite>) {
        movieDatabase.emit(
            (currentMovieDatabase + movies).map { movie ->
                movies.find { it.id == movie.id } ?: movie
            }
        )
    }

    override fun getPeople(): Flow<List<Favorite>> = peopleDatabase

    override suspend fun insertPeople(people: Favorite): Long {
        peopleDatabase.emit(currentPeopleDatabase + people)
        return people.id?.toLong() ?: throw RuntimeException("room database insert failed...")
    }

    override suspend fun deletePeople(people: Favorite) {
        peopleDatabase.emit(currentPeopleDatabase.filter { it.id != people.id })
    }

    override suspend fun upsertPeoples(peoples: List<Favorite>) {
        peopleDatabase.emit(
            (currentPeopleDatabase + peoples).map { people ->
                peoples.find { it.id == people.id } ?: people
            }
        )
    }
}