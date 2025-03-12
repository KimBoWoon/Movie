package com.bowoon.data.repository

import com.bowoon.database.dao.MovieDao
import com.bowoon.database.dao.PeopleDao
import com.bowoon.database.model.MovieEntity
import com.bowoon.database.model.PeopleEntity
import com.bowoon.database.model.asExternalModel
import com.bowoon.model.Favorite
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.threeten.bp.Instant
import javax.inject.Inject

class DatabaseRepositoryImpl @Inject constructor(
    private val movieDao: MovieDao,
    private val peopleDao: PeopleDao
) : DatabaseRepository {
    override fun getMovies(): Flow<List<Favorite>> =
        movieDao.getMovieEntities()
            .map {
                it.sortedByDescending { it.timestamp }
                    .map(MovieEntity::asExternalModel)
            }

    override suspend fun insertMovie(movie: Favorite): Long =
        movieDao.insertOrIgnoreMovies(
            MovieEntity(
                id = movie.id ?: -1,
                posterPath = movie.imagePath ?: "",
                timestamp = Instant.now().toEpochMilli()
            )
        )

    override suspend fun deleteMovie(movie: Favorite) {
        movie.id?.let {
            movieDao.deleteMovie(it)
        }
    }

    override suspend fun upsertMovies(movies: List<Favorite>) {
        movieDao.upsertMovies(
            movies.map {
                MovieEntity(
                    id = it.id ?: -1,
                    posterPath = it.imagePath ?: "",
                    timestamp = Instant.now().toEpochMilli()
                )
            }
        )
    }

    override fun getPeople(): Flow<List<Favorite>> =
        peopleDao.getPeopleEntities()
            .map {
                it.sortedByDescending { it.timestamp }
                    .map(PeopleEntity::asExternalModel)
            }

    override suspend fun insertPeople(people: Favorite): Long =
        peopleDao.insertOrIgnorePeoples(
            PeopleEntity(
                id = people.id ?: -1,
                timestamp = Instant.now().toEpochMilli(),
                name = people.title,
                profilePath = people.imagePath
            )
        )

    override suspend fun deletePeople(people: Favorite) {
        people.id?.let {
            peopleDao.deletePeople(it)
        }
    }

    override suspend fun upsertPeoples(peoples: List<Favorite>) =
        peopleDao.upsertPeoples(
            peoples.map { people ->
                PeopleEntity(
                    id = people.id ?: -1,
                    timestamp = Instant.now().toEpochMilli(),
                    name = people.title,
                    profilePath = people.imagePath
                )
            }
        )
}