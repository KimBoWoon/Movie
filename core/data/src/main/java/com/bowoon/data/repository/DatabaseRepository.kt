package com.bowoon.data.repository

import com.bowoon.model.MovieDetail
import com.bowoon.model.PeopleDetail
import kotlinx.coroutines.flow.Flow

interface DatabaseRepository {
    fun getMovies(): Flow<List<MovieDetail>>
    suspend fun insertMovie(movie: MovieDetail): Long
    suspend fun deleteMovie(movie: MovieDetail)
    suspend fun upsertMovies(movies: List<MovieDetail>)

    fun getPeople(): Flow<List<PeopleDetail>>
    suspend fun insertPeople(people: PeopleDetail): Long
    suspend fun deletePeople(people: PeopleDetail)
    suspend fun upsertPeoples(peoples: List<PeopleDetail>)
}