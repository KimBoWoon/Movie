package com.bowoon.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bowoon.model.Favorite

@Entity(tableName = "peoples")
data class PeopleEntity(
    @PrimaryKey
    val id: Int,
    val timestamp: Long,
    val name: String?,
    val profilePath: String?
)

fun PeopleEntity.asExternalModel(): Favorite = Favorite(
    id = id,
    title = name,
    imagePath = profilePath
)