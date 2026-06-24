package com.cheeke.surfy.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cheeke.surfy.model.MediaType
import com.cheeke.surfy.model.Tv

@Entity(tableName = "tvs")
data class TvEntity(
    @PrimaryKey
    val id: Int,
    val posterPath: String,
    val timestamp: Long,
    val name: String?,
    val firstAirDate: String?,
    val lastAirDate: String?
)

fun TvEntity.asExternalModel(): Tv = Tv(
    id = id,
    posterPath = posterPath,
    title = name,
    releaseDate = firstAirDate,
    firstAirDate = firstAirDate,
    lastAirDate = lastAirDate,
    mediaType = MediaType.TV
)

fun Tv.asExternalModel(): TvEntity = TvEntity(
    id = id ?: -1,
    posterPath = posterPath.orEmpty(),
    timestamp = -1L,//Clock.System.now().toEpochMilliseconds(),
    name = title,
    firstAirDate = firstAirDate,
    lastAirDate = lastAirDate
)