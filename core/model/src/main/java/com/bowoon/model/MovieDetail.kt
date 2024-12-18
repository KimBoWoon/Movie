package com.bowoon.model

data class MovieDetail(
    val title: String? = null,
    val titleEng: String? = null,
    val genre: String? = null,
    val plots: KMDBMoviePlots? = null,
    val rating: String? = null,
    val posters: List<String>? = null,
    val stlls: List<String>? = null,
    val vods: KMDBMovieVods? = null,
    val staffs: KMDBMovieStaffs? = null,
    val repRlsDate: String? = null,
    val runtime: String? = null,
    val salesAcc: String? = null,
    val audiAcc: String? = null
)