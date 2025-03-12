package com.bowoon.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bowoon.model.Favorite

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey
    val id: Int,
    val posterPath: String,
    val timestamp: Long
)

fun MovieEntity.asExternalModel(): Favorite = Favorite(
    id = id,
    imagePath = posterPath
)