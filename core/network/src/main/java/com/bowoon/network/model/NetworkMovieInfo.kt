package com.bowoon.network.model


import com.bowoon.model.KOBISActor
import com.bowoon.model.KOBISAudit
import com.bowoon.model.KOBISCompany
import com.bowoon.model.KOBISDirector
import com.bowoon.model.KOBISGenre
import com.bowoon.model.KOBISMovieData
import com.bowoon.model.KOBISMovieInfo
import com.bowoon.model.KOBISMovieInfoResult
import com.bowoon.model.KOBISNation
import com.bowoon.model.KOBISShowType
import com.bowoon.model.KOBISStaff
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkMovieData(
    @SerialName("movieInfoResult")
    val movieInfoResult: NetworkMovieInfoResult? = null
)

@Serializable
data class NetworkMovieInfoResult(
    @SerialName("movieInfo")
    val movieInfo: NetworkMovieInfo? = null,
    @SerialName("source")
    val source: String? = null
)

@Serializable
data class NetworkMovieInfo(
    @SerialName("actors")
    val actors: List<NetworkActor>? = null,
    @SerialName("audits")
    val audits: List<NetworkAudit>? = null,
    @SerialName("companys")
    val companys: List<NetworkCompany>? = null,
    @SerialName("directors")
    val directors: List<NetworkDirector>? = null,
    @SerialName("genres")
    val genres: List<NetworkGenre>? = null,
    @SerialName("movieCd")
    val movieCd: String? = null,
    @SerialName("movieNm")
    val movieNm: String? = null,
    @SerialName("movieNmEn")
    val movieNmEn: String? = null,
    @SerialName("movieNmOg")
    val movieNmOg: String? = null,
    @SerialName("nations")
    val nations: List<NetworkNation>? = null,
    @SerialName("openDt")
    val openDt: String? = null,
    @SerialName("prdtStatNm")
    val prdtStatNm: String? = null,
    @SerialName("prdtYear")
    val prdtYear: String? = null,
    @SerialName("showTm")
    val showTm: String? = null,
    @SerialName("showTypes")
    val showTypes: List<NetworkShowType>? = null,
    @SerialName("staffs")
    val staffs: List<NetworkStaff>? = null,
    @SerialName("typeNm")
    val typeNm: String? = null
)

@Serializable
data class NetworkActor(
    @SerialName("cast")
    val cast: String? = null,
    @SerialName("castEn")
    val castEn: String? = null,
    @SerialName("peopleNm")
    val peopleNm: String? = null,
    @SerialName("peopleNmEn")
    val peopleNmEn: String? = null
)

@Serializable
data class NetworkAudit(
    @SerialName("auditNo")
    val auditNo: String? = null,
    @SerialName("watchGradeNm")
    val watchGradeNm: String? = null
)

@Serializable
data class NetworkCompany(
    @SerialName("companyCd")
    val companyCd: String? = null,
    @SerialName("companyNm")
    val companyNm: String? = null,
    @SerialName("companyNmEn")
    val companyNmEn: String? = null,
    @SerialName("companyPartNm")
    val companyPartNm: String? = null
)

@Serializable
data class NetworkDirector(
    @SerialName("peopleNm")
    val peopleNm: String? = null,
    @SerialName("peopleNmEn")
    val peopleNmEn: String? = null
)

@Serializable
data class NetworkGenre(
    @SerialName("genreNm")
    val genreNm: String? = null
)

@Serializable
data class NetworkNation(
    @SerialName("nationNm")
    val nationNm: String? = null
)

@Serializable
data class NetworkShowType(
    @SerialName("showTypeGroupNm")
    val showTypeGroupNm: String? = null,
    @SerialName("showTypeNm")
    val showTypeNm: String? = null
)

@Serializable
data class NetworkStaff(
    @SerialName("peopleNm")
    val peopleNm: String? = null,
    @SerialName("peopleNmEn")
    val peopleNmEn: String? = null,
    @SerialName("staffRoleNm")
    val staffRoleNm: String? = null
)

fun NetworkMovieData.asExternalModel(): KOBISMovieData =
    KOBISMovieData(movieInfoResult = movieInfoResult?.asExternalModel())

fun NetworkMovieInfoResult.asExternalModel(): KOBISMovieInfoResult =
    KOBISMovieInfoResult(
        movieInfo = movieInfo?.asExternalModel(),
        source = source
    )

fun NetworkMovieInfo.asExternalModel(): KOBISMovieInfo =
    KOBISMovieInfo(
        actors = actors?.asExternalModel(),
        audits = audits?.asExternalModel(),
        companys = companys?.asExternalModel(),
        directors = directors?.asExternalModel(),
        genres = genres?.asExternalModel(),
        movieCd = movieCd,
        movieNm = movieNm,
        movieNmEn = movieNmEn,
        movieNmOg = movieNmOg,
        nations = nations?.asExternalModel(),
        openDt = openDt,
        prdtStatNm = prdtStatNm,
        prdtYear = prdtYear,
        showTm = showTm,
        showTypes = showTypes?.asExternalModel(),
        staffs = staffs?.asExternalModel(),
        typeNm = typeNm
    )

@JvmName("NetworkActorAsExternalModel")
fun List<NetworkActor>.asExternalModel(): List<KOBISActor> =
    map {
        KOBISActor(
            cast = it.cast,
            castEn = it.castEn,
            peopleNm = it.peopleNm,
            peopleNmEn = it.peopleNmEn
        )
    }

@JvmName("NetworkAuditAsExternalModel")
fun List<NetworkAudit>.asExternalModel(): List<KOBISAudit> =
    map {
        KOBISAudit(
            auditNo = it.auditNo,
            watchGradeNm = it.watchGradeNm
        )
    }

@JvmName("NetworkCompanyAsExternalModel")
fun List<NetworkCompany>.asExternalModel(): List<KOBISCompany> =
    map {
        KOBISCompany(
            companyCd = it.companyCd,
            companyNm = it.companyNm,
            companyNmEn = it.companyNmEn,
            companyPartNm = it.companyPartNm
        )
    }

@JvmName("NetworkDirectorAsExternalModel")
fun List<NetworkDirector>.asExternalModel(): List<KOBISDirector> =
    map {
        KOBISDirector(
            peopleNm = it.peopleNm,
            peopleNmEn = it.peopleNmEn
        )
    }

@JvmName("NetworkGenreAsExternalModel")
fun List<NetworkGenre>.asExternalModel(): List<KOBISGenre> =
    map {
        KOBISGenre(genreNm = it.genreNm)
    }

@JvmName("NetworkNationAsExternalModel")
fun List<NetworkNation>.asExternalModel(): List<KOBISNation> =
    map {
        KOBISNation(
            nationNm = it.nationNm
        )
    }

@JvmName("NetworkShowTypeAsExternalModel")
fun List<NetworkShowType>.asExternalModel(): List<KOBISShowType> =
    map {
        KOBISShowType(
            showTypeGroupNm = it.showTypeGroupNm,
            showTypeNm = it.showTypeNm
        )
    }

@JvmName("NetworkStaffAsExternalModel")
fun List<NetworkStaff>.asExternalModel(): List<KOBISStaff> =
    map {
        KOBISStaff(
            peopleNm = it.peopleNm,
            peopleNmEn = it.peopleNmEn,
            staffRoleNm = it.staffRoleNm
        )
    }