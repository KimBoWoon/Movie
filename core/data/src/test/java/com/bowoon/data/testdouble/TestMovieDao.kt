package com.bowoon.data.testdouble

import com.bowoon.database.dao.MovieDao
import com.bowoon.database.model.MovieEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update

class TestMovieDao : MovieDao {
    private val entitiesStateFlow = MutableStateFlow(emptyList<MovieEntity>())

    override fun getMovieEntity(movieId: Int): Flow<MovieEntity> =
        entitiesStateFlow.mapNotNull { movieEntities ->
            movieEntities.find { movie -> movie.id == movieId }
        }

    override fun getMovieEntities(): Flow<List<MovieEntity>> =
        entitiesStateFlow

    override fun getMovieEntities(ids: Set<Int>): Flow<List<MovieEntity>> =
        entitiesStateFlow.map { movieEntities ->
            movieEntities.filter { it.id in ids }
        }

    override suspend fun insertOrIgnoreMovies(movie: MovieEntity): Long {
        entitiesStateFlow.update { oldValues ->
            (oldValues + movie).distinctBy(MovieEntity::id)
        }
        return movie.id.toLong()
    }

    override suspend fun upsertMovies(entities: List<MovieEntity>) {
        entitiesStateFlow.update { oldValues -> (entities + oldValues).distinctBy(MovieEntity::id) }
    }

    override suspend fun deleteMovies(ids: List<Int>) {
        val idSet = ids.toSet()
        entitiesStateFlow.update { entities -> entities.filterNot { it.id in idSet } }
    }

    override suspend fun deleteMovie(id: Int) {
        entitiesStateFlow.update { entities -> entities.filterNot { it.id == id } }
    }
}