package com.cheeke.surfy.data.repository

import com.cheeke.surfy.common.Log
import com.cheeke.surfy.data.model.asNowPlayingMovieEntity
import com.cheeke.surfy.data.model.asUpComingMovieEntity
import com.cheeke.surfy.data.util.Synchronizer
import com.cheeke.surfy.data.util.updateMovieSync
import com.cheeke.surfy.database.dao.MovieDao
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.network.SyncRemoteDataSource
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.time.LocalDate
import javax.inject.Inject

class SyncRepositoryImpl @Inject constructor(
    private val apis: SyncRemoteDataSource,
    private val userdata: UserDataRepository,
    private val movieDao: MovieDao
) : SyncRepository {
    override suspend fun syncWith(synchronizer: Synchronizer): Boolean = coroutineScope {
        val result = awaitAll(
            getNowPlayingMovies(synchronizer),
            getUpComingMovies(synchronizer)
        )
        userdata.updateMainDate(value = LocalDate.now().minusDays(1).toString())
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
                    val language = userdata.getLanguage()
                    val region = userdata.getRegion()

                    runCatching {
                        apis.getNowPlaying(language = language, region = region, page = 1)
                    }.getOrElse { e ->
                        Log.e(e.message ?: "sync error!")
                        emptyList()
                    }
                },
                versionUpdater = { "" },
                modelDeleter = { movieDao.deleteNowPlayingMovie() },
                modelUpdater = {
                    movieDao.upsertNowPlayingMovie(entities = it.map(transform = Movie::asNowPlayingMovieEntity))
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
                    val language = userdata.getLanguage()
                    val region = userdata.getRegion()

                    runCatching {
                        apis.getUpcomingMovie(language = language, region = region, page = 1)
                    }.getOrElse { e ->
                        Log.e(e.message ?: "sync error!")
                        emptyList()
                    }
                },
                versionUpdater = { "" },
                modelDeleter = { movieDao.deleteUpComingMovie() },
                modelUpdater = {
                    movieDao.upsertUpComingMovie(entities = it.map(transform = Movie::asUpComingMovieEntity))
                }
            )
        }
    }
}