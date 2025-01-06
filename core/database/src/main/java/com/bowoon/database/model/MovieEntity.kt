package com.bowoon.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bowoon.model.MovieDetail

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(defaultValue = "")
    val posterPath: String,
    @ColumnInfo(defaultValue = "", typeAffinity = ColumnInfo.INTEGER)
    val timestamp: Long
)

fun MovieEntity.asExternalModel(): MovieDetail = MovieDetail(
    id = id,
    posterPath = posterPath
)