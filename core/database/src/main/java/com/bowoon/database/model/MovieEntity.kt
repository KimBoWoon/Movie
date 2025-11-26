package com.bowoon.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bowoon.model.Movie

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey
    val id: Int,
    val posterPath: String,
    val timestamp: Long,
    val title: String?,
    val releaseDate: String?
)

fun MovieEntity.asExternalModel(): Movie = Movie(
    id = id,
    posterPath = posterPath,
    title = title,
    releaseDate = releaseDate
)