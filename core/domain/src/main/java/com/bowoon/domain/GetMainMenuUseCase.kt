package com.bowoon.domain

import com.bowoon.common.Log
import com.bowoon.data.repository.KobisRepository
import com.bowoon.data.repository.TMDBRepository
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.DailyBoxOffice
import com.bowoon.model.KOBISBoxOffice
import com.bowoon.model.MainMenu
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import javax.inject.Inject

class GetMainMenuUseCase @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val kobisRepository: KobisRepository,
    private val tmdbRepository: TMDBRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(
        targetDt: LocalDate,
        kobisOpenApiKey: String
    ): Flow<MainMenu> = userDataRepository.userData.flatMapMerge { userData ->
        Log.d("boxOfficeDate > ${userData.updateDate}, mainMenu > ${userData.mainMenu}")

        val boxOfficeDate = when (userData.updateDate.isEmpty()) {
            true -> LocalDate.now()
            false -> LocalDate.parse(userData.updateDate)
        }
        val isUpdate = targetDt.minusDays(1).isAfter(boxOfficeDate)

        Log.d("isUpdate > $isUpdate")

        if (userData.updateDate.isEmpty() || isUpdate) {
            Log.d("need update!!")

            val target = targetDt.minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"))

            combine(
                kobisRepository.getDailyBoxOffice(kobisOpenApiKey, target),
                tmdbRepository.getUpcomingMovies()
            ) { kobisBoxOffice, upcoming ->
                MainMenu(
                    dailyBoxOffice = createDailyBoxOffice(kobisBoxOffice),
                    favoriteMovies = userData.favoriteMovies,
                    upcomingMovies = upcoming.results ?: emptyList()
                ).also { mainMenu ->
                    Log.d("$mainMenu")
                    userDataRepository.updateBoxOfficeDate(targetDt.minusDays(1).toString())
                    userDataRepository.updateMainMenu(mainMenu)
                }
            }
        } else {
            flowOf(
                MainMenu(
                    dailyBoxOffice = userData.mainMenu.dailyBoxOffice,
                    favoriteMovies = userData.favoriteMovies,
                    upcomingMovies = userData.mainMenu.upcomingMovies
                )
            )
        }
    }

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

    private suspend fun createDailyBoxOffice(
        kobisBoxOffice: KOBISBoxOffice
    ): List<DailyBoxOffice> =
        kobisBoxOffice.boxOfficeResult?.dailyBoxOfficeList?.map { kobisDailyBoxOffice ->
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
                posterUrl = "https://image.tmdb.org/t/p/original${tmdbMovie?.posterPath}",
                tmdbId = tmdbMovie?.id
            )
        } ?: emptyList()
}