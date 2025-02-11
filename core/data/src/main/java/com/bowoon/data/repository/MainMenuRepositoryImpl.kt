package com.bowoon.data.repository

import com.bowoon.data.util.suspendRunCatching
import com.bowoon.datastore.InternalDataSource
import com.bowoon.model.MainMenu
import com.bowoon.model.Movie
import com.bowoon.model.NowPlaying
import com.bowoon.model.UpComingResult
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
    override suspend fun syncWith(isForce: Boolean): Boolean =
        suspendRunCatching {
            val date = datastore.getUserData().updateDate
            val targetDt = LocalDate.now().minusDays(1)
            val updateDate = when (date.isNotEmpty()) {
                true -> LocalDate.parse(date)
                false -> LocalDate.MIN
            }
            val isUpdate = targetDt.isAfter(updateDate)

            if (isUpdate || isForce) {
                val posterUrl = myDataRepository.posterUrl.first()
                val nowPlaying = getNowPlaying()
                val upcomingMovies = getUpcomingMovies()

                createMainMenu(posterUrl, nowPlaying, upcomingMovies).also {
                    datastore.updateMainMenu(it)
                    datastore.updateMainOfDate(targetDt.toString())
                }
            }
        }.isSuccess

    override suspend fun getNowPlaying(): List<NowPlaying> {
        val result = mutableListOf<NowPlaying>()
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
                        response.data.asExternalModel().results?.map {
                            NowPlaying(
                                adult = it.adult,
                                backdropPath = it.backdropPath,
                                genreIds = it.genreIds,
                                id = it.id,
                                originalLanguage = it.originalLanguage,
                                originalTitle = it.originalTitle,
                                overview = it.overview,
                                popularity = it.popularity,
                                posterPath = "${it.posterPath}",
                                releaseDate = it.releaseDate,
                                title = it.title,
                                video = it.video,
                                voteAverage = it.voteAverage,
                                voteCount = it.voteCount
                            )
                        } ?: emptyList()
                    )
                }
            }
        } while (page <= totalPage && page < 5)

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

    override suspend fun getUpcomingMovies(): List<UpComingResult> {
        val result = mutableListOf<UpComingResult>()
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
                        response.data.asExternalModel().results?.map {
                            UpComingResult(
                                adult = it.adult,
                                backdropPath = it.backdropPath,
                                genreIds = it.genreIds,
                                id = it.id,
                                originalLanguage = it.originalLanguage,
                                originalTitle = it.originalTitle,
                                overview = it.overview,
                                popularity = it.popularity,
                                posterPath = "${it.posterPath}",
                                releaseDate = it.releaseDate,
                                title = it.title,
                                video = it.video,
                                voteAverage = it.voteAverage,
                                voteCount = it.voteCount
                            )
                        } ?: emptyList()
                    )
                }
            }
        } while (page <= totalPage && page < 5)

        return result.filter { (it.releaseDate ?: "") > LocalDate.now().toString() }.distinctBy { it.id }.sortedBy { it.releaseDate }
    }

    private fun createMainMenu(
        posterUrl: String,
        nowPlaying: List<NowPlaying>,
        upComing: List<UpComingResult>
    ): MainMenu = MainMenu(
        nowPlaying = nowPlaying.map { tmdbMovie ->
            Movie(
                genreIds = tmdbMovie.genreIds,
                id = tmdbMovie.id,
                originalLanguage = tmdbMovie.originalLanguage,
                originalTitle = tmdbMovie.originalTitle,
                overview = tmdbMovie.overview,
                popularity = tmdbMovie.popularity,
                posterPath = "$posterUrl${tmdbMovie.posterPath}",
                releaseDate = tmdbMovie.releaseDate,
                title = tmdbMovie.title,
                voteAverage = tmdbMovie.voteAverage,
                voteCount = tmdbMovie.voteCount
            )
        },
        upcomingMovies = upComing.map { upComingMovie ->
            Movie(
                genreIds = upComingMovie.genreIds,
                id = upComingMovie.id,
                title = upComingMovie.title,
                originalLanguage = upComingMovie.originalLanguage,
                originalTitle = upComingMovie.originalTitle,
                overview = upComingMovie.overview,
                popularity = upComingMovie.popularity,
                posterPath = "$posterUrl${upComingMovie.posterPath}",
                releaseDate = upComingMovie.releaseDate,
                voteAverage = upComingMovie.voteAverage,
                voteCount = upComingMovie.voteCount,
            )
        }
    )
}