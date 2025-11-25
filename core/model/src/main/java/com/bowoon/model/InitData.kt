package com.bowoon.model

data class InitData(
    val configuration: Configuration,
    val languages: List<Language>,
    val regions: Regions,
    val genres: Genres,
    val internalData: InternalData
)