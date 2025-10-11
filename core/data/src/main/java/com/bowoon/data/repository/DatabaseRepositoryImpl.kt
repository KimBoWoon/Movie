package com.bowoon.data.repository

import com.bowoon.database.dao.MovieDao
import com.bowoon.database.dao.PeopleDao
import com.bowoon.database.model.MovieEntity
import com.bowoon.database.model.PeopleEntity
import com.bowoon.database.model.asExternalModel
import com.bowoon.model.Movie
import com.bowoon.model.People
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.threeten.bp.Instant
import javax.inject.Inject

class DatabaseRepositoryImpl @Inject constructor(
    private val movieDao: MovieDao,
    private val peopleDao: PeopleDao
) : DatabaseRepository {
    override fun getMovies(): Flow<List<Movie>> =
        movieDao.getMovieEntities()
            .map { movieEntities ->
                movieEntities.sortedByDescending { entity -> entity.timestamp }
                    .map(transform = MovieEntity::asExternalModel)
            }

    override suspend fun insertMovie(movie: Movie): Long =
        movieDao.insertOrIgnoreMovies(
            MovieEntity(
                id = movie.id ?: -1,
                posterPath = movie.posterPath ?: "",
                title = movie.title ?: "",
                releaseDate = movie.releaseDate ?: "",
                timestamp = Instant.now().toEpochMilli()
            )
        )

    override suspend fun deleteMovie(movie: Movie) {
        movie.id?.let { id ->
            movieDao.deleteMovie(id = id)
        }
    }

    override suspend fun upsertMovies(movies: List<Movie>) {
        movieDao.upsertMovies(
            entities = movies.map { movie ->
                MovieEntity(
                    id = movie.id ?: -1,
                    posterPath = movie.posterPath ?: "",
                    title = movie.title ?: "",
                    releaseDate = movie.releaseDate ?: "",
                    timestamp = Instant.now().toEpochMilli()
                )
            }
        )
    }

    override fun getPeople(): Flow<List<People>> =
        peopleDao.getPeopleEntities()
            .map { peopleEntities ->
                peopleEntities.sortedByDescending { entity -> entity.timestamp }
                    .map(transform = PeopleEntity::asExternalModel)
            }

    override suspend fun insertPeople(people: People): Long =
        peopleDao.insertOrIgnorePeoples(
            PeopleEntity(
                id = people.id ?: -1,
                timestamp = Instant.now().toEpochMilli(),
                name = people.name ?: "",
                profilePath = people.profilePath ?: ""
            )
        )

    override suspend fun deletePeople(people: People) {
        people.id?.let { id ->
            peopleDao.deletePeople(id = id)
        }
    }

    override suspend fun upsertPeoples(peoples: List<People>) =
        peopleDao.upsertPeoples(
            entities = peoples.map { people ->
                PeopleEntity(
                    id = people.id ?: -1,
                    timestamp = Instant.now().toEpochMilli(),
                    name = people.name ?: "",
                    profilePath = people.profilePath ?: ""
                )
            }
        )
}