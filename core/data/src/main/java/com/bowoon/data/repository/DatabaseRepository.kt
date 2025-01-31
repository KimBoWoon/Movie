package com.bowoon.data.repository

import com.bowoon.model.MovieDetail
import com.bowoon.model.PeopleDetailData
import kotlinx.coroutines.flow.Flow

interface DatabaseRepository {
    fun getMovies(): Flow<List<MovieDetail>>
    suspend fun insertMovie(movie: MovieDetail): Long
    suspend fun deleteMovie(movie: MovieDetail)
    suspend fun upsertMovies(movies: List<MovieDetail>)

    fun getPeople(): Flow<List<PeopleDetailData>>
    suspend fun insertPeople(people: PeopleDetailData): Long
    suspend fun deletePeople(people: PeopleDetailData)
    suspend fun upsertPeoples(peoples: List<PeopleDetailData>)
}