package com.bowoon.data.repository

import com.bowoon.model.Favorite
import kotlinx.coroutines.flow.Flow

interface DatabaseRepository {
    fun getMovies(): Flow<List<Favorite>>
    suspend fun insertMovie(movie: Favorite): Long
    suspend fun deleteMovie(movie: Favorite)
    suspend fun upsertMovies(movies: List<Favorite>)

    fun getPeople(): Flow<List<Favorite>>
    suspend fun insertPeople(people: Favorite): Long
    suspend fun deletePeople(people: Favorite)
    suspend fun upsertPeoples(peoples: List<Favorite>)
}