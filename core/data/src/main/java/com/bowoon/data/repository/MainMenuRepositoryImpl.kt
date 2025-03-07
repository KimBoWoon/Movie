package com.bowoon.data.repository

import com.bowoon.data.util.suspendRunCatching
import com.bowoon.datastore.InternalDataSource
import com.bowoon.model.MainMenu
import com.bowoon.model.Movie
import com.bowoon.model.MovieResult
import com.bowoon.model.asExternalMovie
import com.bowoon.network.ApiResponse
import com.bowoon.network.model.asExternalModel
import com.bowoon.network.retrofit.Apis
import kotlinx.coroutines.flow.first
import org.threeten.bp.LocalDate
import javax.inject.Inject

class MainMenuRepositoryImpl @Inject constructor(
    private val apis: Apis,
    private val datastore: InternalDataSource,
    private val myDataRepository: MyDataRepository
) : MainMenuRepository {
    override suspend fun syncWith(isForce: Boolean): Boolean = suspendRunCatching {
        val date = datastore.getUserData().updateDate
        val targetDt = LocalDate.now().minusDays(1)
        val updateDate = when (date.isNotEmpty()) {
            true -> LocalDate.parse(date)
            false -> LocalDate.MIN
        }
        val isUpdate = targetDt.isAfter(updateDate)

        if (isUpdate || isForce) {
            val posterUrl = "${myDataRepository.externalData.first().secureBaseUrl}${datastore.getUserData().imageQuality}"
            val nowPlaying = getNowPlaying()
            val upcomingMovies = getUpcomingMovies()

            createMainMenu(posterUrl, nowPlaying, upcomingMovies).also {
                datastore.updateUserData(
                    datastore.getUserData().copy(
                        mainMenu = it,
                        updateDate = targetDt.toString()
                    )
                )
            }
        }
    }.isSuccess

    override suspend fun getNowPlaying(): List<Movie> {
        val result = mutableListOf<Movie>()
        var page = 1
        var totalPage = 1
        val language = "${datastore.getUserData().language}-${datastore.getUserData().region}"
        val region = datastore.getUserData().region

        do {
            when (val response = apis.tmdbApis.getNowPlaying(language = language, region = region, page = page)) {
                is ApiResponse.Failure -> throw response.throwable
                is ApiResponse.Success -> {
                    page = ((response.data.page ?: 1) + 1)
                    totalPage = response.data.totalPages ?: Int.MAX_VALUE
                    result.addAll(
                        response.data.asExternalModel().results?.map(MovieResult::asExternalMovie) ?: emptyList()
                    )
                }
            }
        } while (page <= totalPage && page <= 5)

        return result.distinctBy { it.id }.sortedWith { o1, o2 ->
            if (o1 != null && o2 != null) {
                if (o1.voteAverage == o2.voteAverage) {
                    o1.title?.compareTo(o2.title ?: "") ?: 0
                } else {
                    o2.voteAverage?.compareTo(o1.voteAverage ?: 0.0) ?: 0
                }
            } else 0
        }
    }

    override suspend fun getUpcomingMovies(): List<Movie> {
        val result = mutableListOf<Movie>()
        var page = 1
        var totalPage = 1
        val language = "${datastore.getUserData().language}-${datastore.getUserData().region}"
        val region = datastore.getUserData().region

        do {
            when (val response = apis.tmdbApis.getUpcomingMovie(language = language, region = region, page = page)) {
                is ApiResponse.Failure -> throw response.throwable
                is ApiResponse.Success -> {
                    page = ((response.data.page ?: 1) + 1)
                    totalPage = response.data.totalPages ?: Int.MAX_VALUE
                    result.addAll(
                        response.data.asExternalModel().results?.map(MovieResult::asExternalMovie) ?: emptyList()
                    )
                }
            }
        } while (page <= totalPage && page <= 5)

        return result.filter { (it.releaseDate ?: "") > LocalDate.now().toString() }.distinctBy { it.id }.sortedBy { it.releaseDate }
    }

    private fun createMainMenu(
        posterUrl: String,
        nowPlaying: List<Movie>,
        upComing: List<Movie>
    ): MainMenu = MainMenu(
        nowPlaying = nowPlaying.map { nowPlayingMovie ->
            nowPlayingMovie.copy(posterPath = "$posterUrl${nowPlayingMovie.posterPath}")
        },
        upcomingMovies = upComing.map { upComingMovie ->
            upComingMovie.copy(posterPath = "$posterUrl${upComingMovie.posterPath}")
        }
    )
}