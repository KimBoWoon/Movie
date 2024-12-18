package com.bowoon.model


data class KMDBMovieDetail(
    val `data`: List<KMDBMovieData>? = null,
    val kMAQuery: String? = null,
    val query: String? = null,
    val totalCount: Int? = null
)

data class KMDBMovieData(
    val collName: String? = null,
    val count: Int? = null,
    val result: List<KMDBMovieResult>? = null,
    val totalCount: Int? = null
)

data class KMDBMovieResult(
    val aLIAS: String? = null,
    val actors: KMDBMovieActors? = null,
    val audiAcc: String? = null,
    val awards1: String? = null,
    val awards2: String? = null,
    val codes: KMDBMovieCodes? = null,
    val commCodes: KMDBMovieCommCodes? = null,
    val company: String? = null,
    val dOCID: String? = null,
    val directors: KMDBMovieDirectors? = null,
    val episodes: String? = null,
    val fLocation: String? = null,
    val genre: String? = null,
    val keywords: String? = null,
    val kmdbUrl: String? = null,
    val modDate: String? = null,
    val movieId: String? = null,
    val movieSeq: String? = null,
    val nation: String? = null,
    val openThtr: String? = null,
    val plots: KMDBMoviePlots? = null,
    val posters: String? = null,
    val prodYear: String? = null,
    val ratedYn: String? = null,
    val rating: String? = null,
    val ratings: KMDBMovieRatings? = null,
    val regDate: String? = null,
    val repRatDate: String? = null,
    val repRlsDate: String? = null,
    val runtime: String? = null,
    val salesAcc: String? = null,
    val screenArea: String? = null,
    val screenCnt: String? = null,
    val soundtrack: String? = null,
    val staffs: KMDBMovieStaffs? = null,
    val stat: List<KMDBMovieStat>? = null,
    val statDate: String? = null,
    val statSouce: String? = null,
    val stlls: String? = null,
    val themeSong: String? = null,
    val title: String? = null,
    val titleEng: String? = null,
    val titleEtc: String? = null,
    val titleOrg: String? = null,
    val type: String? = null,
    val use: String? = null,
    val vods: KMDBMovieVods? = null
) {
    fun getPosterList(): List<String> = posters?.split("|")?.map {
        if (it.startsWith("http://", true)) {
            it.replace("http://", "https://")
        } else {
            it
        }
    } ?: emptyList()

    fun getStllList(): List<String> = stlls?.split("|")?.map {
        if (it.startsWith("http://", true)) {
            it.replace("http://", "https://")
        } else {
            it
        }
    } ?: emptyList()
}

data class KMDBMovieActors(
    val actor: List<KMDBMovieActor>? = null
)

data class KMDBMovieActor(
    val actorEnNm: String? = null,
    val actorId: String? = null,
    val actorNm: String? = null
)

data class KMDBMovieCodes(
    val code: List<KMDBMovieCode>? = null
)

data class KMDBMovieCode(
    val codeNm: String? = null,
    val codeNo: String? = null
)

data class KMDBMovieCommCodes(
    val commCode: List<KMDBMovieCommCode>? = null
)

data class KMDBMovieCommCode(
    val codeNm: String? = null,
    val codeNo: String? = null
)

data class KMDBMovieDirectors(
    val director: List<KMDBMovieDirector>? = null
)

data class KMDBMovieDirector(
    val directorEnNm: String? = null,
    val directorId: String? = null,
    val directorNm: String? = null
)

data class KMDBMoviePlots(
    val plot: List<KMDBMoviePlot>? = null
)

data class KMDBMoviePlot(
    val plotLang: String? = null,
    val plotText: String? = null
)

data class KMDBMovieRatings(
    val rating: List<KMDBMovieRating>? = null
)

data class KMDBMovieRating(
    val ratingDate: String? = null,
    val ratingGrade: String? = null,
    val ratingMain: String? = null,
    val ratingNo: String? = null,
    val releaseDate: String? = null,
    val runtime: String? = null
)

data class KMDBMovieStaffs(
    val staff: List<KMDBMovieStaff>? = null
)

data class KMDBMovieStaff(
    val staffEnNm: String? = null,
    val staffEtc: String? = null,
    val staffId: String? = null,
    val staffNm: String? = null,
    val staffRole: String? = null,
    val staffRoleGroup: String? = null
)

data class KMDBMovieStat(
    val audiAcc: String? = null,
    val salesAcc: String? = null,
    val screenArea: String? = null,
    val screenCnt: String? = null,
    val statDate: String? = null,
    val statSouce: String? = null
)

data class KMDBMovieVods(
    val vod: List<KMDBMovieVod>? = null
)

data class KMDBMovieVod(
    val vodClass: String? = null,
    val vodUrl: String? = null
)