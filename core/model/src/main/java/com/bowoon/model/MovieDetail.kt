package com.bowoon.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class MovieDetail(
    val title: String? = null,
    val titleEng: String? = null,
    val genre: String? = null,
    val plots: KMDBMoviePlots? = null,
    val actors: KMDBMovieActors? = null,
    val directors: KMDBMovieDirectors? = null,
    val rating: String? = null,
    val posters: List<String>? = null,
    val stlls: List<String>? = null,
    val vods: KMDBMovieVods? = null,
    val staffs: KMDBMovieStaffs? = null,
    val openDt: String? = null,
    val runtime: String? = null,
    val salesAcc: String? = null,
    val audiAcc: String? = null,
    val kobisMovieCd: String? = null,
    val isFavorite: Boolean = false
) : Parcelable