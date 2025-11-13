package com.bowoon.data.repository

import com.bowoon.data.util.suspendRunCatching
import com.bowoon.datastore.InternalDataSource
import com.bowoon.model.MainMenu
import com.bowoon.model.Movie
import com.bowoon.network.MovieNetworkDataSource
import org.threeten.bp.LocalDate
import javax.inject.Inject

class MainMenuRepositoryImpl @Inject constructor(
    private val apis: MovieNetworkDataSource,
    private val datastore: InternalDataSource
) : MainMenuRepository {
    override suspend fun syncWith(isForce: Boolean, notification: suspend () -> Unit): Boolean = suspendRunCatching {
        val date = datastore.getUserData().updateDate
        val targetDt = LocalDate.now().minusDays(1)
        val updateDate = when (date.isNotEmpty()) {
            true -> LocalDate.parse(date)
            false -> LocalDate.MIN
        }
        val isUpdate = targetDt.isAfter(updateDate)

        if (isUpdate || isForce) {
            MainMenu(
                nowPlayingMovies = getNowPlaying(),
                upComingMovies = getUpcomingMovies()
            ).also {
                datastore.updateUserData(
                    datastore.getUserData().copy(
                        mainMenu = it,
                        updateDate = targetDt.toString()
                    )
                )

                notification()
            }
        }
    }.isSuccess

    override suspend fun getNowPlaying(): List<Movie> {
        val language = "${datastore.getUserData().language}-${datastore.getUserData().region}"
        val region = datastore.getUserData().region

        return apis.getNowPlaying(language = language, region = region, page = 1)
    }

    override suspend fun getUpcomingMovies(): List<Movie> {
        val language = "${datastore.getUserData().language}-${datastore.getUserData().region}"
        val region = datastore.getUserData().region

        return apis.getUpcomingMovie(language = language, region = region, page = 1)
    }
}