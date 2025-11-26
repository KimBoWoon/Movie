package com.bowoon.model

data class MovieDetailInfo(
    val detail: Movie,
    val series: Series?,
    val autoPlayTrailer: Boolean = false
)