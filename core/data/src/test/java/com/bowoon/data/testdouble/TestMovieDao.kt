package com.bowoon.data.testdouble

import com.bowoon.database.dao.MovieDao
import com.bowoon.database.model.MovieEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.threeten.bp.LocalDate

class TestMovieDao : MovieDao {
    private val entitiesStateFlow = MutableStateFlow(emptyList<MovieEntity>())

    override fun getMovieEntities(): Flow<List<MovieEntity>> = entitiesStateFlow

    override suspend fun insertOrIgnoreMovies(movie: MovieEntity): Long {
        entitiesStateFlow.update { oldValues ->
            (oldValues + movie).distinctBy(MovieEntity::id)
        }
        return movie.id.toLong()
    }

    override suspend fun upsertMovies(entities: List<MovieEntity>) {
        entitiesStateFlow.update { oldValues -> (entities + oldValues).distinctBy(MovieEntity::id) }
    }

    override suspend fun deleteMovie(id: Int) {
        entitiesStateFlow.update { entities -> entities.filterNot { it.id == id } }
    }

    override suspend fun getNextWeekReleaseMovies(): List<MovieEntity> = entitiesStateFlow.value.filter {
        LocalDate.parse(it.releaseDate) in LocalDate.now()..LocalDate.now().plusDays(7)
    }.sortedWith(compareBy({ it.releaseDate }, { it.title }))
}