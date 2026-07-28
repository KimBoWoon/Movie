package com.cheeke.surfy.database.impl.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cheeke.surfy.model.People

@Entity(tableName = "peoples")
data class PeopleEntity(
    @PrimaryKey
    val id: Int,
    val timestamp: Long,
    val name: String?,
    val profilePath: String?
)

fun PeopleEntity.asExternalModel(): People = People(
    id = id,
    title = name,
    posterPath = profilePath
)

fun People.asExternalModel(): PeopleEntity = PeopleEntity(
    id = id ?: -1,
    timestamp = -1L,//Clock.System.now().toEpochMilliseconds(),
    name = title,
    profilePath = posterPath
)