package com.bowoon.model


data class KOBISMovieData(
    val movieInfoResult: KOBISMovieInfoResult? = null
)

data class KOBISMovieInfoResult(
    val movieInfo: KOBISMovieInfo? = null,
    val source: String? = null
)

data class KOBISMovieInfo(
    val actors: List<KOBISActor>? = null,
    val audits: List<KOBISAudit>? = null,
    val companys: List<KOBISCompany>? = null,
    val directors: List<KOBISDirector>? = null,
    val genres: List<KOBISGenre>? = null,
    val movieCd: String? = null,
    val movieNm: String? = null,
    val movieNmEn: String? = null,
    val movieNmOg: String? = null,
    val nations: List<KOBISNation>? = null,
    val openDt: String? = null,
    val prdtStatNm: String? = null,
    val prdtYear: String? = null,
    val showTm: String? = null,
    val showTypes: List<KOBISShowType>? = null,
    val staffs: List<KOBISStaff>? = null,
    val typeNm: String? = null
)

data class KOBISActor(
    val cast: String? = null,
    val castEn: String? = null,
    val peopleNm: String? = null,
    val peopleNmEn: String? = null
)

data class KOBISAudit(
    val auditNo: String? = null,
    val watchGradeNm: String? = null
)

data class KOBISCompany(
    val companyCd: String? = null,
    val companyNm: String? = null,
    val companyNmEn: String? = null,
    val companyPartNm: String? = null
)

data class KOBISDirector(
    val peopleNm: String? = null,
    val peopleNmEn: String? = null
)

data class KOBISGenre(
    val genreNm: String? = null
)

data class KOBISNation(
    val nationNm: String? = null
)

data class KOBISShowType(
    val showTypeGroupNm: String? = null,
    val showTypeNm: String? = null
)

data class KOBISStaff(
    val peopleNm: String? = null,
    val peopleNmEn: String? = null,
    val staffRoleNm: String? = null
)