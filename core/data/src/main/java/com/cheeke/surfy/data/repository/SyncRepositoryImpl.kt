package com.cheeke.surfy.data.repository

import com.cheeke.surfy.common.Log
import com.cheeke.surfy.data.model.asNowPlayingMovieEntity
import com.cheeke.surfy.data.model.asUpComingMovieEntity
import com.cheeke.surfy.data.util.Synchronizer
import com.cheeke.surfy.data.util.updateMovieSync
import com.cheeke.surfy.database.dao.MovieDao
import com.cheeke.surfy.datastore.InternalDataSource
import com.cheeke.surfy.model.InternalData
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.network.SyncRemoteDataSource
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.time.LocalDate
import javax.inject.Inject

class SyncRepositoryImpl @Inject constructor(
    private val apis: SyncRemoteDataSource,
    private val datastore: InternalDataSource,
    private val movieDao: MovieDao
) : SyncRepository {
    override fun syncWith(synchronizer: Synchronizer): Single<Boolean> =
        Single.zip(
            getNowPlayingMovies(synchronizer = synchronizer).subscribeOn(Schedulers.io()),
            getUpComingMovies(synchronizer = synchronizer).subscribeOn(Schedulers.io())
        ) { nowPlayingResult: Boolean, upComingResult: Boolean ->
            nowPlayingResult && upComingResult
        }.flatMap { allSucceeded: Boolean ->
            datastore.updateMainDate(value = LocalDate.now().minusDays(1).toString())
            Single.just(allSucceeded)
        }

    private fun getNowPlayingMovies(synchronizer: Synchronizer): Single<Boolean> =
        synchronizer.updateMovieSync(
            updateChecker = { buildUpdateChecker() },
            getList = {
                datastore.userData
                    .firstOrError()
                    .flatMap { internalData: InternalData ->
                        Log.d("language -> ${internalData.language}, region -> ${internalData.region}")
                        apis.getNowPlaying(
                            language = internalData.language,
                            region = internalData.region,
                            page = 1
                        )
                    }.onErrorReturn { throwable: Throwable ->
                        Log.e(throwable.message ?: "sync error!")
                        emptyList()
                    }
            },
            modelDeleter = { movieDao.deleteNowPlayingMovie() },
            modelUpdater = { movies: List<Movie> ->
                movieDao.upsertNowPlayingMovie(entities = movies.map(transform = Movie::asNowPlayingMovieEntity))
            }
        )

    private fun getUpComingMovies(synchronizer: Synchronizer): Single<Boolean> =
        synchronizer.updateMovieSync(
            updateChecker = { buildUpdateChecker() },
            getList = {
                datastore.userData
                    .firstOrError()
                    .flatMap { internalData: InternalData ->
                        Log.d("language -> ${internalData.language}, region -> ${internalData.region}")
                        apis.getUpcomingMovie(
                            language = internalData.language,
                            region = internalData.region,
                            page = 1
                        )
                    }.onErrorReturn { throwable: Throwable ->
                        Log.e(throwable.message ?: "sync error!")
                        emptyList()
                    }
            },
            modelDeleter = { movieDao.deleteUpComingMovie() },
            modelUpdater = { movies: List<Movie> ->
                movieDao.upsertUpComingMovie(entities = movies.map(transform = Movie::asUpComingMovieEntity))
            }
        )

    private fun Synchronizer.buildUpdateChecker(): Single<Boolean> =
        getVersion().map { date: String ->
            val targetDt = LocalDate.now().minusDays(1)
            val updateDate = if (date.isNotEmpty()) LocalDate.parse(date) else LocalDate.MIN
            val isForced = getSyncInputData().firstOrNull { it.first == "IS_FORCE" }?.second as? Boolean ?: false

            targetDt.isAfter(updateDate) || isForced
        }
}