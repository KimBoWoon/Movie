package com.bowoon.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bowoon.model.MovieDetail
import com.bowoon.model.tmdb.TMDBMovieDetailReleases

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(defaultValue = "")
    val posterPath: String,
    val timestamp: Long,
    val releases: TMDBMovieDetailReleases?,
    @ColumnInfo(defaultValue = "", typeAffinity = ColumnInfo.TEXT)
    val releaseDate: String,
    @ColumnInfo(defaultValue = "", typeAffinity = ColumnInfo.TEXT)
    val title: String
)

fun MovieEntity.asExternalModel(): MovieDetail = MovieDetail(
    id = id,
    title = title,
    posterPath = posterPath,
    releases = releases,
    releaseDate = releaseDate
)