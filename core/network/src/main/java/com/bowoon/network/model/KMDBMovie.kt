package com.bowoon.network.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KMDBMovieDetail(
    @SerialName("Data")
    val `data`: List<KMDBMovieData>? = null,
    @SerialName("KMAQuery")
    val kMAQuery: String? = null,
    @SerialName("Query")
    val query: String? = null,
    @SerialName("TotalCount")
    val totalCount: Int? = null
)

@Serializable
data class KMDBMovieData(
    @SerialName("CollName")
    val collName: String? = null,
    @SerialName("Count")
    val count: Int? = null,
    @SerialName("Result")
    val result: List<KMDBMovieResult>? = null,
    @SerialName("TotalCount")
    val totalCount: Int? = null
)

@Serializable
data class KMDBMovieResult(
    @SerialName("ALIAS")
    val aLIAS: String? = null,
    @SerialName("actors")
    val actors: KMDBMovieActors? = null,
    @SerialName("audiAcc")
    val audiAcc: String? = null,
    @SerialName("Awards1")
    val awards1: String? = null,
    @SerialName("Awards2")
    val awards2: String? = null,
    @SerialName("Codes")
    val codes: KMDBMovieCodes? = null,
    @SerialName("CommCodes")
    val commCodes: KMDBMovieCommCodes? = null,
    @SerialName("company")
    val company: String? = null,
    @SerialName("DOCID")
    val dOCID: String? = null,
    @SerialName("directors")
    val directors: KMDBMovieDirectors? = null,
    @SerialName("episodes")
    val episodes: String? = null,
    @SerialName("fLocation")
    val fLocation: String? = null,
    @SerialName("genre")
    val genre: String? = null,
    @SerialName("keywords")
    val keywords: String? = null,
    @SerialName("kmdbUrl")
    val kmdbUrl: String? = null,
    @SerialName("modDate")
    val modDate: String? = null,
    @SerialName("movieId")
    val movieId: String? = null,
    @SerialName("movieSeq")
    val movieSeq: String? = null,
    @SerialName("nation")
    val nation: String? = null,
    @SerialName("openThtr")
    val openThtr: String? = null,
    @SerialName("plots")
    val plots: KMDBMoviePlots? = null,
    @SerialName("posters")
    val posters: String? = null,
    @SerialName("prodYear")
    val prodYear: String? = null,
    @SerialName("ratedYn")
    val ratedYn: String? = null,
    @SerialName("rating")
    val rating: String? = null,
    @SerialName("ratings")
    val ratings: KMDBMovieRatings? = null,
    @SerialName("regDate")
    val regDate: String? = null,
    @SerialName("repRatDate")
    val repRatDate: String? = null,
    @SerialName("repRlsDate")
    val repRlsDate: String? = null,
    @SerialName("runtime")
    val runtime: String? = null,
    @SerialName("salesAcc")
    val salesAcc: String? = null,
    @SerialName("screenArea")
    val screenArea: String? = null,
    @SerialName("screenCnt")
    val screenCnt: String? = null,
    @SerialName("soundtrack")
    val soundtrack: String? = null,
    @SerialName("staffs")
    val staffs: KMDBMovieStaffs? = null,
    @SerialName("stat")
    val stat: List<KMDBMovieStat>? = null,
    @SerialName("statDate")
    val statDate: String? = null,
    @SerialName("statSouce")
    val statSouce: String? = null,
    @SerialName("stlls")
    val stlls: String? = null,
    @SerialName("themeSong")
    val themeSong: String? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("titleEng")
    val titleEng: String? = null,
    @SerialName("titleEtc")
    val titleEtc: String? = null,
    @SerialName("titleOrg")
    val titleOrg: String? = null,
    @SerialName("type")
    val type: String? = null,
    @SerialName("use")
    val use: String? = null,
    @SerialName("vods")
    val vods: KMDBMovieVods? = null
)

@Serializable
data class KMDBMovieActors(
    @SerialName("actor")
    val actor: List<KMDBMovieActor>? = null
)

@Serializable
data class KMDBMovieActor(
    @SerialName("actorEnNm")
    val actorEnNm: String? = null,
    @SerialName("actorId")
    val actorId: String? = null,
    @SerialName("actorNm")
    val actorNm: String? = null
)

@Serializable
data class KMDBMovieCodes(
    @SerialName("Code")
    val code: List<KMDBMovieCode>? = null
)

@Serializable
data class KMDBMovieCode(
    @SerialName("CodeNm")
    val codeNm: String? = null,
    @SerialName("CodeNo")
    val codeNo: String? = null
)

@Serializable
data class KMDBMovieCommCodes(
    @SerialName("CommCode")
    val commCode: List<KMDBMovieCommCode>? = null
)

@Serializable
data class KMDBMovieCommCode(
    @SerialName("CodeNm")
    val codeNm: String? = null,
    @SerialName("CodeNo")
    val codeNo: String? = null
)

@Serializable
data class KMDBMovieDirectors(
    @SerialName("director")
    val director: List<KMDBMovieDirector>? = null
)

@Serializable
data class KMDBMovieDirector(
    @SerialName("directorEnNm")
    val directorEnNm: String? = null,
    @SerialName("directorId")
    val directorId: String? = null,
    @SerialName("directorNm")
    val directorNm: String? = null
)

@Serializable
data class KMDBMoviePlots(
    @SerialName("plot")
    val plot: List<KMDBMoviePlot>? = null
)

@Serializable
data class KMDBMoviePlot(
    @SerialName("plotLang")
    val plotLang: String? = null,
    @SerialName("plotText")
    val plotText: String? = null
)

@Serializable
data class KMDBMovieRatings(
    @SerialName("rating")
    val rating: List<KMDBMovieRating>? = null
)

@Serializable
data class KMDBMovieRating(
    @SerialName("ratingDate")
    val ratingDate: String? = null,
    @SerialName("ratingGrade")
    val ratingGrade: String? = null,
    @SerialName("ratingMain")
    val ratingMain: String? = null,
    @SerialName("ratingNo")
    val ratingNo: String? = null,
    @SerialName("releaseDate")
    val releaseDate: String? = null,
    @SerialName("runtime")
    val runtime: String? = null
)

@Serializable
data class KMDBMovieStaffs(
    @SerialName("staff")
    val staff: List<KMDBMovieStaff>? = null
)

@Serializable
data class KMDBMovieStaff(
    @SerialName("staffEnNm")
    val staffEnNm: String? = null,
    @SerialName("staffEtc")
    val staffEtc: String? = null,
    @SerialName("staffId")
    val staffId: String? = null,
    @SerialName("staffNm")
    val staffNm: String? = null,
    @SerialName("staffRole")
    val staffRole: String? = null,
    @SerialName("staffRoleGroup")
    val staffRoleGroup: String? = null
)

@Serializable
data class KMDBMovieStat(
    @SerialName("audiAcc")
    val audiAcc: String? = null,
    @SerialName("salesAcc")
    val salesAcc: String? = null,
    @SerialName("screenArea")
    val screenArea: String? = null,
    @SerialName("screenCnt")
    val screenCnt: String? = null,
    @SerialName("statDate")
    val statDate: String? = null,
    @SerialName("statSouce")
    val statSouce: String? = null
)

@Serializable
data class KMDBMovieVods(
    @SerialName("vod")
    val vod: List<KMDBMovieVod>? = null
)

@Serializable
data class KMDBMovieVod(
    @SerialName("vodClass")
    val vodClass: String? = null,
    @SerialName("vodUrl")
    val vodUrl: String? = null
)

fun KMDBMovieDetail.asExternalModel(): com.bowoon.model.KMDBMovieDetail =
    com.bowoon.model.KMDBMovieDetail(
        data = data?.asExternalModel(),
        kMAQuery = kMAQuery,
        query = query,
        totalCount = totalCount
    )

@JvmName("KmdbMovieDataAsExternalModel")
fun List<KMDBMovieData>.asExternalModel(): List<com.bowoon.model.KMDBMovieData> =
    map {
        com.bowoon.model.KMDBMovieData(
            collName = it.collName,
            count = it.count,
            result = it.result?.asExternalModel(),
            totalCount = it.totalCount
        )
    }

@JvmName("KmdbMovieResultAsExternalModel")
fun List<KMDBMovieResult>.asExternalModel(): List<com.bowoon.model.KMDBMovieResult> =
    map {
        com.bowoon.model.KMDBMovieResult(
            actors = it.actors?.asExternalModel(),
            company = it.company,
            dOCID = it.dOCID,
            directors = it.directors?.asExternalModel(),
            genre = it.genre,
            kmdbUrl = it.kmdbUrl,
            movieId = it.movieId,
            movieSeq = it.movieSeq,
            nation = it.nation,
            plots = it.plots?.asExternalModel(),
            prodYear = it.prodYear,
            rating = it.rating,
            runtime = it.runtime,
            title = it.title,
            titleEng = it.titleEng,
            titleEtc = it.titleEtc,
            titleOrg = it.titleOrg,
            awards1 = it.awards1,
            awards2 = it.awards2,
            aLIAS = it.aLIAS,
            posters = it.posters,
            openThtr = it.openThtr,
            modDate = it.modDate,
            keywords = it.keywords,
            fLocation = it.fLocation,
            episodes = it.episodes,
            commCodes = it.commCodes?.asExternalModel(),
            codes = it.codes?.asExternalModel(),
            audiAcc = it.audiAcc,
            soundtrack = it.soundtrack,
            screenCnt = it.screenCnt,
            screenArea = it.screenArea,
            salesAcc = it.salesAcc,
            repRlsDate = it.repRlsDate,
            repRatDate = it.repRatDate,
            regDate = it.regDate,
            ratings = it.ratings?.asExternalModel(),
            ratedYn = it.ratedYn,
            staffs = it.staffs?.asExternalModel(),
            stat = it.stat?.asExternalModel(),
            statDate = it.statDate,
            statSouce = it.statSouce,
            stlls = it.stlls,
            themeSong = it.themeSong,
            type = it.type,
            use = it.use,
            vods = it.vods?.asExternalModel()
        )
    }

fun KMDBMovieActors.asExternalModel(): com.bowoon.model.KMDBMovieActors =
    com.bowoon.model.KMDBMovieActors(
        actor = actor?.asExternalModel()
    )

@JvmName("KmdbActorAsExternalModel")
fun List<KMDBMovieActor>.asExternalModel(): List<com.bowoon.model.KMDBMovieActor> =
    map {
        com.bowoon.model.KMDBMovieActor(
            actorEnNm = it.actorEnNm,
            actorId = it.actorId,
            actorNm = it.actorNm
        )
    }

fun KMDBMovieDirectors.asExternalModel(): com.bowoon.model.KMDBMovieDirectors =
    com.bowoon.model.KMDBMovieDirectors(
        director = director?.asExternalModel()
    )

@JvmName("KmdbDirectorAsExternalModel")
fun List<KMDBMovieDirector>.asExternalModel(): List<com.bowoon.model.KMDBMovieDirector> =
    map {
        com.bowoon.model.KMDBMovieDirector(
            directorEnNm = it.directorEnNm,
            directorId = it.directorId,
            directorNm = it.directorNm
        )
    }

fun KMDBMoviePlots.asExternalModel(): com.bowoon.model.KMDBMoviePlots =
    com.bowoon.model.KMDBMoviePlots(
        plot = plot?.asExternalModel()
    )

@JvmName("KmdbPlotAsExternalModel")
fun List<KMDBMoviePlot>.asExternalModel(): List<com.bowoon.model.KMDBMoviePlot> =
    map {
        com.bowoon.model.KMDBMoviePlot(
            plotLang = it.plotLang,
            plotText = it.plotText
        )
    }

fun KMDBMovieCommCodes.asExternalModel(): com.bowoon.model.KMDBMovieCommCodes =
    com.bowoon.model.KMDBMovieCommCodes(
        commCode = commCode?.asExternalModel()
    )

@JvmName("KMDBMovieCommCodeAsExternalModel")
fun List<KMDBMovieCommCode>.asExternalModel(): List<com.bowoon.model.KMDBMovieCommCode> =
    map {
        com.bowoon.model.KMDBMovieCommCode(
            codeNm = it.codeNm,
            codeNo = it.codeNo
        )
    }

fun KMDBMovieCodes.asExternalModel(): com.bowoon.model.KMDBMovieCodes =
    com.bowoon.model.KMDBMovieCodes(
        code = code?.asExternalModel()
    )

@JvmName("KMDBMovieCodeAsExternalModel")
fun List<KMDBMovieCode>.asExternalModel(): List<com.bowoon.model.KMDBMovieCode> =
    map {
        com.bowoon.model.KMDBMovieCode(
            codeNm = it.codeNm,
            codeNo = it.codeNo
        )
    }

fun KMDBMovieRatings.asExternalModel(): com.bowoon.model.KMDBMovieRatings =
    com.bowoon.model.KMDBMovieRatings(
        rating = rating?.asExternalModel()
    )

@JvmName("KMDBMovieRatingAsExternalModel")
fun List<KMDBMovieRating>.asExternalModel(): List<com.bowoon.model.KMDBMovieRating> =
    map {
        com.bowoon.model.KMDBMovieRating(
            ratingDate = it.ratingDate,
            ratingGrade = it.ratingGrade,
            ratingMain = it.ratingMain,
            ratingNo = it.ratingNo,
            releaseDate = it.releaseDate,
            runtime = it.runtime
        )
    }

fun KMDBMovieStaffs.asExternalModel(): com.bowoon.model.KMDBMovieStaffs =
    com.bowoon.model.KMDBMovieStaffs(
        staff = staff?.asExternalModel()
    )

@JvmName("KMDBMovieStaffAsExternalModel")
fun List<KMDBMovieStaff>.asExternalModel(): List<com.bowoon.model.KMDBMovieStaff> =
    map {
        com.bowoon.model.KMDBMovieStaff(
            staffEnNm = it.staffEnNm,
            staffEtc = it.staffEtc,
            staffId = it.staffId,
            staffNm = it.staffNm,
            staffRole = it.staffRole,
            staffRoleGroup = it.staffRoleGroup
        )
    }

@JvmName("KMDBMovieStatAsExternalModel")
fun List<KMDBMovieStat>.asExternalModel(): List<com.bowoon.model.KMDBMovieStat> =
    map {
        com.bowoon.model.KMDBMovieStat(
            audiAcc = it.audiAcc,
            salesAcc = it.salesAcc,
            screenArea = it.screenArea,
            screenCnt = it.screenCnt,
            statDate = it.statDate,
            statSouce = it.statSouce
        )
    }

fun KMDBMovieVods.asExternalModel(): com.bowoon.model.KMDBMovieVods =
    com.bowoon.model.KMDBMovieVods(
        vod = vod?.asExternalModel()
    )

@JvmName("KMDBMovieVodAsExternalModel")
fun List<KMDBMovieVod>.asExternalModel(): List<com.bowoon.model.KMDBMovieVod> =
    map {
        com.bowoon.model.KMDBMovieVod(
            vodClass = it.vodClass,
            vodUrl = it.vodUrl
        )
    }