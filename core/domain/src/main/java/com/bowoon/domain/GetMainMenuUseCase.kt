package com.bowoon.domain

import com.bowoon.data.repository.KobisRepository
import com.bowoon.data.repository.TMDBRepository
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.DailyBoxOffice
import com.bowoon.model.KOBISBoxOffice
import com.bowoon.model.MainMenu
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import javax.inject.Inject

class GetMainMenuUseCase @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val kobisRepository: KobisRepository,
    private val tmdbRepository: TMDBRepository
) {
    operator fun invoke(
        targetDt: LocalDate,
        kobisOpenApiKey: String
    ): Flow<MainMenu> = combine(
        userDataRepository.userData,
        kobisRepository.getDailyBoxOffice(kobisOpenApiKey, targetDt.format(DateTimeFormatter.ofPattern("yyyyMMdd"))),
        tmdbRepository.posterUrl
    ) { userData, kobisBoxOffice, posterUrl ->
        MainMenu(
            dailyBoxOffice = createDailyBoxOffice(kobisBoxOffice, posterUrl),
            favoriteMovies = userData.favoriteMovies
        )
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
        kobisBoxOffice: KOBISBoxOffice,
        posterUrl: String
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
                posterUrl = "$posterUrl${tmdbMovie?.posterPath}",
                tmdbId = tmdbMovie?.id
            )
        } ?: emptyList()
}