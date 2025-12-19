package com.bowoon.data.repository

import com.bowoon.common.Log
import com.bowoon.data.model.asNowPlayingMovieEntity
import com.bowoon.data.model.asUpComingMovieEntity
import com.bowoon.data.util.Synchronizer
import com.bowoon.data.util.updateMovieSync
import com.bowoon.database.dao.MovieDao
import com.bowoon.datastore.InternalDataSource
import com.bowoon.model.Movie
import com.bowoon.network.MovieNetworkDataSource
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.time.LocalDate
import javax.inject.Inject

class MainMenuRepositoryImpl @Inject constructor(
    private val apis: MovieNetworkDataSource,
    private val datastore: InternalDataSource,
    private val movieDao: MovieDao
) : MainMenuRepository {
    override suspend fun syncWith(synchronizer: Synchronizer): Boolean = coroutineScope {
        val nowPlayingMovieDeferred = async {
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
                    val language = datastore.getLanguage()
                    val region = datastore.getRegion()

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
        val upComingMovieDeferred = async {
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
                    val language = datastore.getLanguage()
                    val region = datastore.getRegion()

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
        val result = awaitAll(nowPlayingMovieDeferred, upComingMovieDeferred)
        datastore.updateMainDate(value = LocalDate.now().minusDays(1).toString())
        result
    }.all { it }
}