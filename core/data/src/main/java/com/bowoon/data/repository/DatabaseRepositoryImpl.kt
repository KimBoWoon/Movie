package com.bowoon.data.repository

import com.bowoon.database.dao.MovieDao
import com.bowoon.database.dao.PeopleDao
import com.bowoon.database.model.MovieEntity
import com.bowoon.database.model.PeopleEntity
import com.bowoon.database.model.asExternalModel
import com.bowoon.model.MovieDetail
import com.bowoon.model.PeopleDetailData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.threeten.bp.Instant
import javax.inject.Inject

class DatabaseRepositoryImpl @Inject constructor(
    private val movieDao: MovieDao,
    private val peopleDao: PeopleDao
) : DatabaseRepository {
    override fun getMovies(): Flow<List<MovieDetail>> =
        movieDao.getMovieEntities()
            .map {
                it.sortedByDescending { it.timestamp }
                    .map(MovieEntity::asExternalModel)
            }

    override suspend fun insertMovie(movie: MovieDetail): Long =
        movieDao.insertOrIgnoreMovies(
            MovieEntity(
                id = movie.id ?: -1,
                posterPath = movie.posterPath ?: "",
                timestamp = Instant.now().toEpochMilli(),
                releases = movie.releases,
                releaseDate = movie.releaseDate ?: "",
                title = movie.title ?: ""
            )
        )

    override suspend fun deleteMovie(movie: MovieDetail) {
        movie.id?.let {
            movieDao.deleteMovie(it)
        }
    }

    override suspend fun upsertMovies(movies: List<MovieDetail>) {
        movieDao.upsertMovies(
            movies.map {
                MovieEntity(
                    id = it.id ?: -1,
                    posterPath = it.posterPath ?: "",
                    timestamp = Instant.now().toEpochMilli(),
                    releases = it.releases,
                    releaseDate = it.releaseDate ?: "",
                    title = it.title ?: ""
                )
            }
        )
    }

    override fun getPeople(): Flow<List<PeopleDetailData>> =
        peopleDao.getPeopleEntities()
            .map {
                it.sortedByDescending { it.timestamp }
                    .map(PeopleEntity::asExternalModel)
            }

    override suspend fun insertPeople(people: PeopleDetailData): Long =
        peopleDao.insertOrIgnorePeoples(
            PeopleEntity(
                id = people.id ?: -1,
                timestamp = Instant.now().toEpochMilli(),
                name = people.name,
                gender = people.gender,
                biography = people.biography,
                birthday = people.birthday,
                deathday = people.deathday,
                combineCredits = people.combineCredits,
                externalIds = people.externalIds,
                images = people.images,
                placeOfBirth = people.placeOfBirth,
                profilePath = people.profilePath
            )
        )

    override suspend fun deletePeople(people: PeopleDetailData) {
        people.id?.let {
            peopleDao.deletePeople(it)
        }
    }

    override suspend fun upsertPeoples(peoples: List<PeopleDetailData>) =
        peopleDao.upsertPeoples(
            peoples.map { people ->
                PeopleEntity(
                    id = people.id ?: -1,
                    timestamp = Instant.now().toEpochMilli(),
                    name = people.name,
                    gender = people.gender,
                    biography = people.biography,
                    birthday = people.birthday,
                    deathday = people.deathday,
                    combineCredits = people.combineCredits,
                    externalIds = people.externalIds,
                    images = people.images,
                    placeOfBirth = people.placeOfBirth,
                    profilePath = people.profilePath
                )
            }
        )
}