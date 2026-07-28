package com.cheeke.surfy.sync.impl.repository

import com.cheeke.surfy.common.Log
import com.cheeke.surfy.database.impl.dao.MovieDao
import com.cheeke.surfy.database.impl.model.NowPlayingMovieEntity
import com.cheeke.surfy.database.impl.model.UpComingMovieEntity
import com.cheeke.surfy.network.SyncRemoteDataSource
import com.cheeke.surfy.sync.api.SyncRepository
import com.cheeke.surfy.sync.api.Synchronizer
import com.cheeke.surfy.sync.impl.utils.updateMovieSync
import com.cheeke.surfy.userdata.api.UserDataRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject

class SyncRepositoryImpl @Inject constructor(
    private val apis: SyncRemoteDataSource,
    private val userdataRepository: UserDataRepository,
    private val movieDao: MovieDao
) : SyncRepository {
    override suspend fun syncWith(synchronizer: Synchronizer): Boolean = coroutineScope {
        val result = awaitAll(
            getNowPlayingMovies(synchronizer),
            getUpComingMovies(synchronizer)
        )
        userdataRepository.updateMainDate(value = LocalDate.now().minusDays(1).toString())
        result
    }.all { it }

    private suspend fun getNowPlayingMovies(synchronizer: Synchronizer): Deferred<Boolean> = coroutineScope {
        async {
            synchronizer.updateMovieSync(
                updateChecker = {
                    val date = getVersion()
                    val targetDt = LocalDate.now().minusDays(1)
                    val updateDate = when (date.isNotEmpty()) {
                        true -> LocalDate.parse(date)
                        false -> LocalDate.MIN
                    }

                    targetDt.isAfter(updateDate) || getSyncInputData().firstOrNull { it.first == "IS_FORCE" }?.second as Boolean
                },
                getList = {
                    val internalData = userdataRepository.internalData.first()

                    runCatching {
                        apis.getNowPlaying(language = internalData.language, region = internalData.region, page = 1)
                    }.getOrElse { e ->
                        Log.e(e.message ?: "sync error!")
                        emptyList()
                    }
                },
                versionUpdater = { "" },
                modelDeleter = { movieDao.deleteNowPlayingMovie() },
                modelUpdater = {
                    movieDao.upsertNowPlayingMovie(entities = it.map { movie ->
                        NowPlayingMovieEntity(
                            id = movie.id ?: -1,
                            posterPath = movie.posterPath ?: "",
                            title = movie.title,
                            releaseDate = movie.releaseDate,
                            voteAverage = movie.voteAverage,
                            voteCount = movie.voteCount
                        )
                    })
                }
            )
        }
    }

    private suspend fun getUpComingMovies(synchronizer: Synchronizer): Deferred<Boolean> = coroutineScope {
        async {
            synchronizer.updateMovieSync(
                updateChecker = {
                    val date = getVersion()
                    val targetDt = LocalDate.now().minusDays(1)
                    val updateDate = when (date.isNotEmpty()) {
                        true -> LocalDate.parse(date)
                        false -> LocalDate.MIN
                    }

                    targetDt.isAfter(updateDate) || getSyncInputData().firstOrNull { it.first == "IS_FORCE" }?.second as Boolean
                },
                getList = {
                    val internalData = userdataRepository.internalData.first()

                    runCatching {
                        apis.getUpcomingMovie(language = internalData.language, region = internalData.region, page = 1)
                    }.getOrElse { e ->
                        Log.e(e.message ?: "sync error!")
                        emptyList()
                    }
                },
                versionUpdater = { "" },
                modelDeleter = { movieDao.deleteUpComingMovie() },
                modelUpdater = {
                    movieDao.upsertUpComingMovie(entities = it.map { movie ->
                        UpComingMovieEntity(
                            id = movie.id ?: -1,
                            posterPath = movie.posterPath ?: "",
                            title = movie.title,
                            releaseDate = movie.releaseDate,
                            voteAverage = movie.voteAverage,
                            voteCount = movie.voteCount
                        )
                    })
                }
            )
        }
    }
}