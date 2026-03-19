package com.cheeke.surfy.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cheeke.surfy.model.Movie

@Entity(tableName = "nowPlayingMovie")
data class NowPlayingMovieEntity(
    @PrimaryKey
    val id: Int,
    val posterPath: String,
    val title: String?,
    val releaseDate: String?,
    val voteAverage: Float?,
    val voteCount: Int?
)

@Entity(tableName = "upComingMovie")
data class UpComingMovieEntity(
    @PrimaryKey
    val id: Int,
    val posterPath: String,
    val title: String?,
    val releaseDate: String?,
    val voteAverage: Float?,
    val voteCount: Int?
)

fun NowPlayingMovieEntity.asExternalModel(): Movie = Movie(
    id = id,
    posterPath = posterPath,
    title = title,
    releaseDate = releaseDate,
    voteAverage = voteAverage,
    voteCount = voteCount
)

fun UpComingMovieEntity.asExternalModel(): Movie = Movie(
    id = id,
    posterPath = posterPath,
    title = title,
    releaseDate = releaseDate,
    voteAverage = voteAverage,
    voteCount = voteCount
)