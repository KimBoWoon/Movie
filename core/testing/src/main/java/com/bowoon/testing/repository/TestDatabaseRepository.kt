package com.bowoon.testing.repository

import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.model.Favorite
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update

class TestDatabaseRepository : DatabaseRepository {
    private val _movieDatabase = MutableStateFlow<List<Favorite>>(emptyList())
    private val _peopleDatabase = MutableStateFlow<List<Favorite>>(emptyList())
    private val currentMovieDatabase get() = _movieDatabase.replayCache.firstOrNull() ?: emptyList()
    private val currentPeopleDatabase get() = _peopleDatabase.replayCache.firstOrNull() ?: emptyList()
    val movieDatabase: Flow<List<Favorite>> = _movieDatabase.filterNotNull()
    val peopleDatabase: Flow<List<Favorite>> = _peopleDatabase.filterNotNull()

    override fun getMovies(): Flow<List<Favorite>> = movieDatabase

    override suspend fun insertMovie(movie: Favorite): Long {
        _movieDatabase.update { currentMovieDatabase + movie }
        return movie.id?.toLong() ?: throw RuntimeException("room database insert failed...")
    }

    override suspend fun deleteMovie(movie: Favorite) {
        _movieDatabase.update { currentMovieDatabase - movie }
    }

    override suspend fun upsertMovies(movies: List<Favorite>) {
        _movieDatabase.update {
            (currentMovieDatabase + movies).map { movie ->
                movies.find { it.id == movie.id } ?: movie
            }
        }
    }

    override fun getPeople(): Flow<List<Favorite>> = peopleDatabase

    override suspend fun insertPeople(people: Favorite): Long {
        _peopleDatabase.update { currentPeopleDatabase + people }
        return people.id?.toLong() ?: throw RuntimeException("room database insert failed...")
    }

    override suspend fun deletePeople(people: Favorite) {
        _peopleDatabase.update { currentPeopleDatabase - people }
    }

    override suspend fun upsertPeoples(peoples: List<Favorite>) {
        _peopleDatabase.update {
            (currentPeopleDatabase + peoples).map { people ->
                peoples.find { it.id == people.id } ?: people
            }
        }
    }
}