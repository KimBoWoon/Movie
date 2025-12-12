package com.bowoon.data.repository

import com.bowoon.common.Log
import com.bowoon.data.model.asNowPlayingMovieEntity
import com.bowoon.data.model.asUpComingMovieEntity
import com.bowoon.data.util.Synchronizer
import com.bowoon.data.util.suspendRunCatching
import com.bowoon.database.dao.MovieDao
import com.bowoon.datastore.InternalDataSource
import com.bowoon.model.Movie
import com.bowoon.network.MovieNetworkDataSource
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.threeten.bp.LocalDate
import javax.inject.Inject

class MainMenuRepositoryImpl @Inject constructor(
    private val apis: MovieNetworkDataSource,
    private val datastore: InternalDataSource,
    private val movieDao: MovieDao
) : MainMenuRepository {
    override suspend fun getNowPlaying(): List<Movie> {
        val language = datastore.getUserData().language
        val region = datastore.getUserData().region

        return apis.getNowPlaying(language = language, region = region, page = 1)
    }

    override suspend fun getUpcomingMovies(): List<Movie> {
        val language = datastore.getUserData().language
        val region = datastore.getUserData().region

        return apis.getUpcomingMovie(language = language, region = region, page = 1)
    }

    override suspend fun syncWith(synchronizer: Synchronizer): Boolean =
        synchronizer.changeListSync(
            updateChecker = {
                val date = getChangeListVersions()
                val targetDt = LocalDate.now().minusDays(1)
                val updateDate = when (date.isNotEmpty()) {
                    true -> LocalDate.parse(date)
                    false -> LocalDate.MIN
                }

                targetDt.isAfter(updateDate) || getIsForce()
            },
            nowPlayingMovies = {
                val language = datastore.getUserData().language
                val region = datastore.getUserData().region

                apis.getNowPlaying(language = language, region = region, page = 1)
            },
            upComingMovies = {
                val language = datastore.getUserData().language
                val region = datastore.getUserData().region

                apis.getUpcomingMovie(language = language, region = region, page = 1)
            },
            versionUpdater = {
                LocalDate.now().minusDays(1).toString()
            },
            modelDeleter = {
                movieDao.deleteNowPlayingMovie()
                movieDao.deleteUpComingMovie()
            },
            modelUpdater = {
                movieDao.upsertNowPlayingMovie(entities = it[0].map(transform = Movie::asNowPlayingMovieEntity))
                movieDao.upsertUpComingMovie(entities = it[1].map(transform = Movie::asUpComingMovieEntity))
            }
        )
}

suspend fun Synchronizer.changeListSync(
    updateChecker: suspend Synchronizer.() -> Boolean,
    nowPlayingMovies: suspend () -> List<Movie>,
    upComingMovies: suspend () -> List<Movie>,
    versionUpdater: () -> String,
    modelDeleter: suspend () -> Unit,
    modelUpdater: suspend (List<List<Movie>>) -> Unit,
): Boolean = suspendRunCatching {
    if (updateChecker()) {
        val updateMovies = coroutineScope {
            listOf(
                async { nowPlayingMovies() },
                async { upComingMovies() }
            ).awaitAll()
        }
        Log.d("changeListSync -> $updateMovies")
        modelDeleter()
        modelUpdater(updateMovies)
        updateChangeListVersions(update = { versionUpdater() })
    } else {
        Log.d("changeListSync -> else")
    }
}.isSuccess

suspend fun Synchronizer.changeListSync(
    updateChecker: suspend Synchronizer.() -> Boolean,
    getList: suspend () -> List<Movie>,
    versionUpdater: () -> String,
    modelDeleter: suspend () -> Unit,
    modelUpdater: suspend (List<Movie>) -> Unit,
): Boolean = suspendRunCatching {
    if (updateChecker()) {
        val updateMovies = getList()
        Log.d("changeListSync -> $updateMovies")
        modelDeleter()
        modelUpdater(updateMovies)
        updateChangeListVersions(update = { versionUpdater() })
    } else {
        Log.d("changeListSync -> else")
    }
}.isSuccess