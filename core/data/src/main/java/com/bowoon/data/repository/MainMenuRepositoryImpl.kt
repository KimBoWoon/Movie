package com.bowoon.data.repository

import com.bowoon.data.model.asNowPlayingMovieEntity
import com.bowoon.data.model.asUpComingMovieEntity
import com.bowoon.data.util.Synchronizer
import com.bowoon.data.util.changeMainSync
import com.bowoon.database.dao.MovieDao
import com.bowoon.datastore.InternalDataSource
import com.bowoon.model.Movie
import com.bowoon.network.MovieNetworkDataSource
import org.threeten.bp.LocalDate
import javax.inject.Inject

class MainMenuRepositoryImpl @Inject constructor(
    private val apis: MovieNetworkDataSource,
    private val datastore: InternalDataSource,
    private val movieDao: MovieDao
) : MainMenuRepository {
    override suspend fun syncWith(synchronizer: Synchronizer): Boolean =
        synchronizer.changeMainSync(
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