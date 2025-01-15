package com.bowoon.network.model


import com.bowoon.model.kobis.KOBISBoxOffice
import com.bowoon.model.kobis.KOBISBoxOfficeResult
import com.bowoon.model.kobis.KOBISDailyBoxOffice
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkBoxOffice(
    @SerialName("boxOfficeResult")
    val boxOfficeResult: NetworkBoxOfficeResult? = null
)

@Serializable
data class NetworkBoxOfficeResult(
    @SerialName("boxofficeType")
    val boxofficeType: String? = null,
    @SerialName("dailyBoxOfficeList")
    val dailyBoxOfficeList: List<NetworkDailyBoxOffice>? = null,
    @SerialName("showRange")
    val showRange: String? = null
)

@Serializable
data class NetworkDailyBoxOffice(
    @SerialName("audiAcc")
    val audiAcc: String? = null,
    @SerialName("audiChange")
    val audiChange: String? = null,
    @SerialName("audiCnt")
    val audiCnt: String? = null,
    @SerialName("audiInten")
    val audiInten: String? = null,
    @SerialName("movieCd")
    val movieCd: String? = null,
    @SerialName("movieNm")
    val movieNm: String? = null,
    @SerialName("openDt")
    val openDt: String? = null,
    @SerialName("rank")
    val rank: String? = null,
    @SerialName("rankInten")
    val rankInten: String? = null,
    @SerialName("rankOldAndNew")
    val rankOldAndNew: String? = null,
    @SerialName("rnum")
    val rnum: String? = null,
    @SerialName("salesAcc")
    val salesAcc: String? = null,
    @SerialName("salesAmt")
    val salesAmt: String? = null,
    @SerialName("salesChange")
    val salesChange: String? = null,
    @SerialName("salesInten")
    val salesInten: String? = null,
    @SerialName("salesShare")
    val salesShare: String? = null,
    @SerialName("scrnCnt")
    val scrnCnt: String? = null,
    @SerialName("showCnt")
    val showCnt: String? = null
)

fun NetworkBoxOffice.asExternalModel(): KOBISBoxOffice = KOBISBoxOffice(
    KOBISBoxOfficeResult(
        boxofficeType = boxOfficeResult?.boxofficeType,
        dailyBoxOfficeList = boxOfficeResult?.dailyBoxOfficeList?.asExternalModel(),
        showRange = boxOfficeResult?.showRange
    )
)

fun List<NetworkDailyBoxOffice>.asExternalModel(): List<KOBISDailyBoxOffice> =
    map {
        KOBISDailyBoxOffice(
            audiAcc = it.audiAcc,
            audiChange = it.audiChange,
            audiCnt = it.audiCnt,
            audiInten = it.audiInten,
            movieCd = it.movieCd,
            movieNm = it.movieNm,
            openDt = it.openDt,
            rank = it.rank,
            rankInten = it.rankInten,
            rankOldAndNew = it.rankOldAndNew,
            rnum = it.rnum,
            salesAcc = it.salesAcc,
            salesAmt = it.salesAmt,
            salesChange = it.salesChange,
            salesInten = it.salesInten,
            salesShare = it.salesShare,
            scrnCnt = it.scrnCnt,
            showCnt = it.showCnt
        )
    }