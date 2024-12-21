package com.bowoon.domain

import com.bowoon.common.Log
import com.bowoon.data.repository.KmdbRepository
import com.bowoon.data.repository.KobisRepository
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.DailyBoxOffice
import com.bowoon.model.KOBISDailyBoxOffice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class GetDailyBoxOfficeUseCase @Inject constructor(
    private val kobisRepository: KobisRepository,
    private val kmdbRepository: KmdbRepository,
    private val userDataRepository: UserDataRepository
) {
    operator fun invoke(
        key: String,
        targetDt: String,
        kmdbOpenApiKey: String
    ): Flow<List<DailyBoxOffice>> =
        combine(
            userDataRepository.userData,
            kobisRepository.getDailyBoxOffice(key, targetDt)
        ) { userData, kobisBoxOffice->
            userData.favoriteMovie.ifEmpty {
                Log.d("dailyBoxOffice is empty")
                val dailyBoxOfficeList =
                    kobisBoxOffice.boxOfficeResult?.dailyBoxOfficeList?.map { kobisDailyBoxOffice ->
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
                    } ?: emptyList()
                userDataRepository.updateFavoriteMovie(dailyBoxOfficeList)
                dailyBoxOfficeList
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
            ?.posters
            ?.split("|")
            ?.map { posterUrl ->
                if (posterUrl.startsWith("http://", true)) {
                    posterUrl.replace("http://", "https://")
                } else {
                    posterUrl
                }
            }?.firstOrNull() ?: ""
}