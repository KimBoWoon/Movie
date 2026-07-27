package com.cheeke.surfy.detail.impl.movie

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.cheeke.surfy.database.dao.MovieDao
import com.cheeke.surfy.database.model.MovieEntity
import com.cheeke.surfy.database.model.NowPlayingMovieEntity
import com.cheeke.surfy.database.model.UpComingMovieEntity
import com.cheeke.surfy.database.model.asExternalModel
import com.cheeke.surfy.detail.api.movie.MovieRepository
import com.cheeke.surfy.feature.detail.impl.BuildConfig
import com.cheeke.surfy.model.Movie
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.Instant

class MovieRepositoryImpl @Inject constructor(
    private val movieDao: MovieDao
) : MovieRepository {
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

    override fun getFavorite(): Flow<PagingData<Movie>> =
        Pager(config = PagingConfig(pageSize = 20)) {
            movieDao.getFavoriteMovie()
        }.flow.map { pagingData -> pagingData.map(transform = MovieEntity::asExternalModel) }

    override fun isFavorite(id: Int): Flow<Boolean> =
        movieDao.isFavoriteMovie(id = id)

    override suspend fun insert(media: Movie): Long =
        movieDao.insertOrIgnoreMovies(
            movie = MovieEntity(
                id = media.id ?: -1,
                posterPath = media.posterPath ?: "",
                title = media.title ?: "",
                releaseDate = media.releaseDate ?: "",
                timestamp = Instant.now().toEpochMilli()
            )
        )

    override suspend fun delete(media: Movie) {
        media.id?.let { id ->
            movieDao.deleteMovie(id = id)
        }
    }

    override suspend fun upsert(medias: List<Movie>) {
        movieDao.upsertMovies(entities = medias.map { it.asExternalModel() })
    }

    override fun getUpComingMovies(): Flow<PagingData<Movie>> =
        Pager(config = PagingConfig(pageSize = 20)) {
            movieDao.getUpComingMovie()
        }.flow.map { pagingData -> pagingData.map(transform = UpComingMovieEntity::asExternalModel) }

    override suspend fun getPopularMovies(): List<Movie> =
        movieDao.getPopularMovies().map { it.asExternalModel() }

    override fun getNowPlayingMovies(): Flow<PagingData<Movie>> =
        Pager(config = PagingConfig(pageSize = 20)) {
            movieDao.getNowPlayingMovie()
        }.flow.map { pagingData -> pagingData.map(transform = NowPlayingMovieEntity::asExternalModel) }

    override suspend fun getNextWeekReleaseMovies(): List<Movie> =
        movieDao.getNextWeekReleaseMovies().map { it.asExternalModel() }
}