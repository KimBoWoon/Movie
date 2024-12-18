package com.bowoon.model


data class BoxOffice(
    val boxOfficeResult: BoxOfficeResult? = null
)

data class BoxOfficeResult(
    val boxofficeType: String? = null,
    val dailyBoxOfficeList: List<DailyBoxOffice>? = null,
    val showRange: String? = null
)

data class DailyBoxOffice(
    val audiAcc: String? = null,
    val audiChange: String? = null,
    val audiCnt: String? = null,
    val audiInten: String? = null,
    val movieCd: String? = null,
    val movieNm: String? = null,
    val openDt: String? = null,
    val rank: String? = null,
    val rankInten: String? = null,
    val rankOldAndNew: String? = null,
    val rnum: String? = null,
    val salesAcc: String? = null,
    val salesAmt: String? = null,
    val salesChange: String? = null,
    val salesInten: String? = null,
    val salesShare: String? = null,
    val scrnCnt: String? = null,
    val showCnt: String? = null
)