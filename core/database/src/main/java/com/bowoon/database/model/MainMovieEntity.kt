package com.bowoon.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nowPlayingMovie")
data class NowPlayingMovieEntity(
    @PrimaryKey
    val id: Int,
    val posterPath: String,
    val title: String?,
    val releaseDate: String?
)

@Entity(tableName = "upComingMovie")
data class UpComingMovieEntity(
    @PrimaryKey
    val id: Int,
    val posterPath: String,
    val title: String?,
    val releaseDate: String?
)