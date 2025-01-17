package com.bowoon.data.repository

import com.bowoon.data.BuildConfig
import com.bowoon.data.util.suspendRunCatching
import com.bowoon.datastore.InternalDataSource
import com.bowoon.model.MainMenu
import com.bowoon.model.MainMovie
import com.bowoon.model.UpComingResult
import com.bowoon.model.kobis.KOBISBoxOffice
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import javax.inject.Inject

class MainMenuRepositoryImpl @Inject constructor(
    private val kobisRepository: KOBISRepository,
    private val tmdbRepository: TMDBRepository,
    private val datastore: InternalDataSource,
    private val myDataRepository: MyDataRepository
) : MainMenuRepository {
    override suspend fun syncWith(isForce: Boolean): Boolean =
        suspendRunCatching {
            val key = BuildConfig.KOBIS_OPEN_API_KEY
            val date = datastore.getMainOfDate()
            val targetDt = LocalDate.now().minusDays(1)
            val updateDate = when (date.isNotEmpty()) {
                true -> LocalDate.parse(date)
                false -> LocalDate.MIN
            }
            val isUpdate = targetDt.isAfter(updateDate)

            if (isUpdate || isForce) {
                val posterUrl = myDataRepository.posterUrl.first()
                val boxOffice = kobisRepository.getDailyBoxOffice(key, targetDt.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                val upcomingMovies = tmdbRepository.getUpcomingMoviesTemp()

                createMainMenu(posterUrl, boxOffice, upcomingMovies).also {
                    datastore.updateMainMenu(it)
                    datastore.updateMainOfDate(targetDt.toString())
                }
            }
        }.isSuccess

    private fun getTMDBMovie(
        movieName: String,
        releaseDateGte: String,
        releaseDateLte: String,
    ) = tmdbRepository.discoverMovie(
        releaseDateGte = releaseDateGte,
        releaseDateLte = releaseDateLte
    ).map {
        it.results?.find { it.title?.replace(" ", "") == movieName.replace(" ", "") }
    }

    private suspend fun createMainMenu(
        posterUrl: String,
        kobisBoxOffice: KOBISBoxOffice,
        upComingResult: List<UpComingResult>
    ): MainMenu {
        var rank = 0

        return MainMenu(
            dailyBoxOffice = kobisBoxOffice.boxOfficeResult?.dailyBoxOfficeList?.mapNotNull { kobisDailyBoxOffice ->
                getTMDBMovie(
                    movieName = kobisDailyBoxOffice.movieNm ?: "",
                    releaseDateGte = kobisDailyBoxOffice.openDt ?: "",
                    releaseDateLte = kobisDailyBoxOffice.openDt ?: ""
                ).firstOrNull()?.let { tmdbMovie ->
                    rank++
                    MainMovie(
                        genreIds = tmdbMovie.genreIds,
                        id = tmdbMovie.id,
                        originalLanguage = tmdbMovie.originalLanguage,
                        originalTitle = tmdbMovie.originalTitle,
                        overview = tmdbMovie.overview,
                        popularity = tmdbMovie.popularity,
                        posterPath = "$posterUrl${tmdbMovie.posterPath}",
                        releaseDate = tmdbMovie.releaseDate,
                        title = tmdbMovie.title ?: kobisDailyBoxOffice.movieNm,
                        voteAverage = tmdbMovie.voteAverage,
                        voteCount = tmdbMovie.voteCount,
                        rank = rank.toString(),
                        rankOldAndNew = kobisDailyBoxOffice.rankOldAndNew
                    )
                }
            } ?: emptyList(),
            upcomingMovies = upComingResult.map { upComingMovie ->
                MainMovie(
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
}