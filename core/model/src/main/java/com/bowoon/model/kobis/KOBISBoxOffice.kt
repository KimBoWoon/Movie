package com.bowoon.model.kobis


data class KOBISBoxOffice(
    val boxOfficeResult: KOBISBoxOfficeResult? = null
)

data class KOBISBoxOfficeResult(
    val boxofficeType: String? = null,
    val dailyBoxOfficeList: List<KOBISDailyBoxOffice>? = null,
    val showRange: String? = null
)

data class KOBISDailyBoxOffice(
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