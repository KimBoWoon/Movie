package com.bowoon.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bowoon.model.CombineCredits
import com.bowoon.model.DetailImage
import com.bowoon.model.PeopleDetail
import com.bowoon.model.PeopleExternalIds

@Entity(tableName = "peoples")
data class PeopleEntity(
    @PrimaryKey
    val id: Int,
    val timestamp: Long,
    val name: String?,
    val gender: Int?,
    val biography: String?,
    val birthday: String?,
    val deathday: String?,
    val combineCredits: CombineCredits?,
    val externalIds: PeopleExternalIds?,
    val images: List<DetailImage>?,
    val placeOfBirth: String?,
    val profilePath: String?,
)

fun PeopleEntity.asExternalModel(): PeopleDetail = PeopleDetail(
    id = id,
    name = name,
    gender = gender,
    biography = biography,
    birthday = birthday,
    deathday = deathday,
    combineCredits = combineCredits,
    externalIds = externalIds,
    images = images,
    placeOfBirth = placeOfBirth,
    profilePath = profilePath
)