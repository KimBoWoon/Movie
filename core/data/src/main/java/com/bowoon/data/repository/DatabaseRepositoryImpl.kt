package com.bowoon.data.repository

import com.bowoon.database.dao.MovieDao
import com.bowoon.database.model.MovieEntity
import com.bowoon.database.model.asExternalModel
import com.bowoon.model.MovieDetail
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ViewModelScoped
class DatabaseRepositoryImpl @Inject constructor(
    private val movieDao: MovieDao
) : DatabaseRepository {
    override fun getMovies(): Flow<List<MovieDetail>> =
        movieDao.getMovieEntities()
            .map { it.map(MovieEntity::asExternalModel) }

    override suspend fun insert(movie: MovieDetail): Long =
        movieDao.insertOrIgnoreMovies(MovieEntity(id = movie.id ?: -1, posterPath = movie.posterPath ?: ""))

    override suspend fun delete(movie: MovieDetail) {
        movie.id?.let {
            movieDao.deleteMovie(it)
        }
    }

    override suspend fun updateMovies(movies: List<MovieDetail>) {
        movieDao.upsertMovies(
            movies.map {
                MovieEntity(
                    id = it.id ?: -1,
                    posterPath = it.posterPath ?: ""
                )
            }
        )
    }
}