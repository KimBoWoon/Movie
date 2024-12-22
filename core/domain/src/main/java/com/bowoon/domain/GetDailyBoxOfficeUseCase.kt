package com.bowoon.domain

import com.bowoon.common.Log
import com.bowoon.data.repository.KmdbRepository
import com.bowoon.data.repository.KobisRepository
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.DailyBoxOffice
import com.bowoon.model.KOBISDailyBoxOffice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.threeten.bp.Duration
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import javax.inject.Inject

class GetDailyBoxOfficeUseCase @Inject constructor(
    private val kobisRepository: KobisRepository,
    private val kmdbRepository: KmdbRepository,
    private val userDataRepository: UserDataRepository
) {
    companion object {
        private const val TAG = "GetDailyBoxOfficeUseCase"
    }

    operator fun invoke(
        targetDt: LocalDate,
        kobisOpenApiKey: String,
        kmdbOpenApiKey: String
    ): Flow<List<DailyBoxOffice>> = userDataRepository.userData.flatMapLatest {
        Log.d("boxOfficeDate > ${it.boxOfficeDate}, dailyBoxOffices > ${it.dailyBoxOffices}")

        val boxOfficeDate = if (it.boxOfficeDate.isEmpty()) LocalDate.now() else LocalDate.parse(it.boxOfficeDate)
        val betweenDay = Duration.between(
            boxOfficeDate.atTime(0, 0, 0, 0),
            targetDt.atTime(0, 0, 0, 0)
        ).toDays()

        Log.d("betweenDay > $betweenDay")

        if (it.boxOfficeDate.isEmpty() || betweenDay > 1 || it.dailyBoxOffices.isEmpty()) {
            Log.d("dailyBoxOffice is empty")

            val target = targetDt.format(DateTimeFormatter.ofPattern("yyyyMMdd"))

            kobisRepository.getDailyBoxOffice(kobisOpenApiKey, target)
                .map { kobisBoxOffice ->
                    (kobisBoxOffice.boxOfficeResult?.dailyBoxOfficeList?.map { kobisDailyBoxOffice ->
                        DailyBoxOffice(
                            audiAcc = kobisDailyBoxOffice.audiAcc,
                            audiChange = kobisDailyBoxOffice.audiChange,
                            audiCnt = kobisDailyBoxOffice.audiCnt,
                            audiInten = kobisDailyBoxOffice.audiInten,
                            movieCd = kobisDailyBoxOffice.movieCd,
                            movieNm = kobisDailyBoxOffice.movieNm,
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
                            posterUrl = getPosterUrl(kobisDailyBoxOffice, kmdbOpenApiKey)
                        )
                    } ?: emptyList()).also { dailyBoxOfficeList ->
                        userDataRepository.updateBoxOfficeDate(targetDt.toString())
                        userDataRepository.updateDailyBoxOffices(dailyBoxOfficeList)
                    }
                }
        } else {
            flowOf(it.dailyBoxOffices)
        }
    }

    private suspend fun getPosterUrl(
        kobisDailyBoxOffice: KOBISDailyBoxOffice,
        kmdbOpenApiKey: String
    ): String =
        kmdbRepository.getMovieInfo("https://api.koreafilm.or.kr/openapi-data2/wisenut/search_api/search_json2.jsp?collection=kmdb_new2&detail=Y&title=${kobisDailyBoxOffice.movieNm}&releaseDts=${kobisDailyBoxOffice.openDt}&listCount=1&ServiceKey=${kmdbOpenApiKey}")
            .firstOrNull()
            ?.data
            ?.firstOrNull()
            ?.result
            ?.firstOrNull()
            ?.getPosterList()
            ?.firstOrNull() ?: ""
}