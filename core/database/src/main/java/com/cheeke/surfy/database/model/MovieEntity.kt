package com.cheeke.surfy.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cheeke.surfy.model.MediaType
import com.cheeke.surfy.model.Movie
import kotlin.time.Clock

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
    releaseDate = releaseDate,
    mediaType = MediaType.MOVIE
)

fun Movie.asExternalModel(): MovieEntity = MovieEntity(
    id = id ?: -1,
    posterPath = posterPath ?: "",
    timestamp = Clock.System.now().toEpochMilliseconds(),
    title = title,
    releaseDate = releaseDate
)