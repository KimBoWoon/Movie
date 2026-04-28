package com.cheeke.surfy.data.repository

import androidx.paging.PagingSource
import com.cheeke.surfy.core.data.BuildConfig
import com.cheeke.surfy.database.dao.MovieDao
import com.cheeke.surfy.database.dao.PeopleDao
import com.cheeke.surfy.database.dao.TvDao
import com.cheeke.surfy.database.model.MovieEntity
import com.cheeke.surfy.database.model.NowPlayingMovieEntity
import com.cheeke.surfy.database.model.PeopleEntity
import com.cheeke.surfy.database.model.TvEntity
import com.cheeke.surfy.database.model.UpComingMovieEntity
import com.cheeke.surfy.database.model.asExternalModel
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.model.People
import com.cheeke.surfy.model.Tv
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

class DatabaseRepositoryImpl @Inject constructor(
    private val movieDao: MovieDao,
    private val tvDao: TvDao,
    private val peopleDao: PeopleDao
) : DatabaseRepository {
    init {
        if (BuildConfig.BENCHMARK) {
            CoroutineScope(context = Dispatchers.IO).launch {
                movieDao.deleteAllFavoriteMovies()
                movieDao.insertOrIgnoreMovies(
                    MovieEntity(
                        id = 575265,
                        posterPath = "/c5pDU8SW0ZbmO5jHfw7fX6keyyR.jpg",
                        title = "title",
                        releaseDate = "releaseDate",
                        timestamp = Instant.now().toEpochMilli()
                    )
                )
            }
        }
    }

    override fun getMovies(): Flow<List<Movie>> =
        movieDao.getMovieEntities()
            .map { movieEntities ->
                movieEntities.sortedByDescending { entity -> entity.timestamp }
                    .map(transform = MovieEntity::asExternalModel)
            }

    override fun isFavoriteMovie(id: Int): Flow<Boolean> = movieDao.isFavoriteMovie(id = id)

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

    override suspend fun getPopularMovies(): List<Movie> =
        movieDao.getPopularMovies().map { nowPlayingMovieEntities ->
            nowPlayingMovieEntities.asExternalModel()
        }

    override suspend fun getNextWeekReleaseMovies(): List<Movie> =
        movieDao.getNextWeekReleaseMovies().map { movieEntity ->
            movieEntity.asExternalModel()
        }

    override fun getPeople(): Flow<List<People>> =
        peopleDao.getPeopleEntities()
            .map { peopleEntities ->
                peopleEntities.sortedByDescending { entity -> entity.timestamp }
                    .map(transform = PeopleEntity::asExternalModel)
            }

    override fun isFavoritePeople(id: Int): Flow<Boolean> = peopleDao.isFavoritePeople(id = id)

    override suspend fun insertPeople(people: People): Long =
        peopleDao.insertOrIgnorePeoples(
            PeopleEntity(
                id = people.id ?: -1,
                timestamp = Instant.now().toEpochMilli(),
                name = people.title ?: "",
                profilePath = people.posterPath ?: ""
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
                    name = people.title ?: "",
                    profilePath = people.posterPath ?: ""
                )
            }
        )

    override fun getFavoriteMovie(): PagingSource<Int, MovieEntity> =
        movieDao.getFavoriteMovie()
    override fun getFavoritePeople(): PagingSource<Int, PeopleEntity> =
        peopleDao.getFavoritePeople()
    override fun getFavoriteTv(): PagingSource<Int, TvEntity> =
        tvDao.getFavoriteTv()

    override fun getNowPlayingMovies(): PagingSource<Int, NowPlayingMovieEntity> =
        movieDao.getNowPlayingMovie()

    override fun getUpComingMovies(): PagingSource<Int, UpComingMovieEntity> =
        movieDao.getUpComingMovie()

    override fun getTv(): Flow<List<Tv>> = tvDao.getTvEntities()
        .map { tvEntities ->
            tvEntities.sortedByDescending { entity -> entity.timestamp }
                .map(transform = TvEntity::asExternalModel)
        }

    override fun isFavoriteTv(id: Int): Flow<Boolean> = tvDao.isFavoriteTv(id = id)

    override suspend fun insertTv(tv: Tv): Long = tvDao.insertOrIgnoreTvs(
        tv = TvEntity(
            id = tv.id ?: -1,
            posterPath = tv.posterPath ?: "",
            name = tv.title ?: "",
            firstAirDate = tv.firstAirDate ?: "",
            lastAirDate = tv.lastAirDate,
            timestamp = Instant.now().toEpochMilli()
        )
    )

    override suspend fun deleteTv(tv: Tv) {
        tv.id?.let { id ->
            tvDao.deleteTv(id = id)
        }
    }

    override suspend fun upsertTvs(tvs: List<Tv>) {
        tvDao.upsertTvs(
            entities = tvs.map { tv ->
                TvEntity(
                    id = tv.id ?: -1,
                    posterPath = tv.posterPath ?: "",
                    name = tv.title ?: "",
                    firstAirDate = tv.firstAirDate ?: "",
                    lastAirDate = tv.lastAirDate ?: "",
                    timestamp = Instant.now().toEpochMilli()
                )
            }
        )
    }

    override suspend fun getNextWeekReleaseTvs(): List<Tv> =
        tvDao.getNextWeekReleaseTvs().map { tvEntity ->
            tvEntity.asExternalModel()
        }
}