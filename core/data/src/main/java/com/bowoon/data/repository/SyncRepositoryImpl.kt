package com.bowoon.data.repository

import com.bowoon.data.BuildConfig
import com.bowoon.data.util.suspendRunCatching
import com.bowoon.model.DailyBoxOffice
import com.bowoon.model.KOBISBoxOffice
import com.bowoon.model.MainMenu
import com.bowoon.model.UpComingResult
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import javax.inject.Inject

class SyncRepositoryImpl @Inject constructor(
    private val kobisRepository: KobisRepository,
    private val tmdbRepository: TMDBRepository,
    private val userDataRepository: UserDataRepository
) : SyncRepository {
    override suspend fun syncWith(isForce: Boolean): Boolean =
        suspendRunCatching {
            val key = BuildConfig.KOBIS_OPEN_API_KEY
            val date = userDataRepository.getMainOfDate()
            val targetDt = LocalDate.now().minusDays(1)
            val updateDate = when (date.isNotEmpty()) {
                true -> LocalDate.parse(date)
                false -> LocalDate.MIN
            }
            val isUpdate = targetDt.isAfter(updateDate)

            if (isUpdate || isForce) {
                val kobisBoxOffice = kobisRepository.getDailyBoxOffice(key, targetDt.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).first()
                val posterUrl = tmdbRepository.posterUrl.first()
                val upcomingMovies = tmdbRepository.getUpcomingMovies().first()

                createMainMenu(kobisBoxOffice, posterUrl, upcomingMovies).also {
                    userDataRepository.updateMainMenu(it)
                    userDataRepository.updateMainOfDate(targetDt.toString())
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
        kobisBoxOffice: KOBISBoxOffice,
        posterUrl: String,
        upComingResult: List<UpComingResult>
    ): MainMenu = MainMenu(
        dailyBoxOffice = kobisBoxOffice.boxOfficeResult?.dailyBoxOfficeList?.map { kobisDailyBoxOffice ->
            val tmdbMovie = getTMDBMovie(
                movieName = kobisDailyBoxOffice.movieNm ?: "",
                releaseDateGte = kobisDailyBoxOffice.openDt ?: "",
                releaseDateLte = kobisDailyBoxOffice.openDt ?: ""
            ).firstOrNull()

            DailyBoxOffice(
                audiAcc = kobisDailyBoxOffice.audiAcc,
                audiChange = kobisDailyBoxOffice.audiChange,
                audiCnt = kobisDailyBoxOffice.audiCnt,
                audiInten = kobisDailyBoxOffice.audiInten,
                movieCd = kobisDailyBoxOffice.movieCd,
                movieNm = tmdbMovie?.title ?: kobisDailyBoxOffice.movieNm,
                openDt = kobisDailyBoxOffice.openDt,
                rank = kobisDailyBoxOffice.rank,
                rankInten = kobisDailyBoxOffice.rankInten,
                rankOldAndNew = kobisDailyBoxOffice.rankOldAndNew,
                rnum = kobisDailyBoxOffice.rnum,
                salesAcc = kobisDailyBoxOffice.salesAcc,
                salesAmt = kobisDailyBoxOffice.salesAmt,
                salesChange = kobisDailyBoxOffice.salesChange,
                salesInten = kobisDailyBoxOffice.salesInten,
                salesShare = kobisDailyBoxOffice.salesShare,
                scrnCnt = kobisDailyBoxOffice.scrnCnt,
                showCnt = kobisDailyBoxOffice.showCnt,
                posterUrl = "$posterUrl${tmdbMovie?.posterPath}",
                tmdbId = tmdbMovie?.id
            )
        } ?: emptyList(),
        upcomingMovies = upComingResult
    )
}