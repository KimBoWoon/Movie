package com.cheeke.surfy.data.repository

import androidx.paging.PagingSource
import com.cheeke.surfy.core.data.BuildConfig
import com.cheeke.surfy.database.dao.MovieDao
import com.cheeke.surfy.database.model.MovieEntity
import com.cheeke.surfy.database.model.NowPlayingMovieEntity
import com.cheeke.surfy.database.model.UpComingMovieEntity
import com.cheeke.surfy.database.model.asExternalModel
import com.cheeke.surfy.model.Media
import com.cheeke.surfy.model.Movie
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

class MovieDataBaseRepositoryImpl @Inject constructor(
    private val movieDao: MovieDao
) : MovieDataBaseRepository {
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

    override fun isFavorite(id: Int): Flowable<Boolean> = movieDao.isFavoriteMovie(id = id)

    override suspend fun insert(media: Media): Long =
        movieDao.insertOrIgnoreMovies(
            MovieEntity(
                id = media.id ?: -1,
                posterPath = media.posterPath ?: "",
                title = media.title ?: "",
                releaseDate = media.releaseDate ?: "",
                timestamp = Instant.now().toEpochMilli()
            )
        )

    override suspend fun delete(media: Media) {
        media.id?.let { id ->
            movieDao.deleteMovie(id = id)
        }
    }

    override suspend fun upsert(medias: List<Media>) {
        movieDao.upsertMovies(
            entities = medias.map { movie ->
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

    override fun getUpComingMovies(): PagingSource<Int, UpComingMovieEntity> =
        movieDao.getUpComingMovie()

    override fun getNowPlayingMovies(): PagingSource<Int, NowPlayingMovieEntity> =
        movieDao.getNowPlayingMovie()

    override fun getPopularMovies(): Observable<List<Movie>> =
        movieDao.getPopularMovies().map { nowPlayingMovieEntities ->
            nowPlayingMovieEntities.map(transform = NowPlayingMovieEntity::asExternalModel)
        }.subscribeOn(Schedulers.io())

    override fun getNextWeekReleaseMovies(): Single<List<Movie>> =
        movieDao.getNextWeekReleaseMovies().map { movieEntity ->
            movieEntity.map(transform = MovieEntity::asExternalModel)
        }.subscribeOn(Schedulers.io())

    override fun getFavorite(): PagingSource<Int, MovieEntity> =
        movieDao.getFavoriteMovie()
}