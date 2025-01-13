package com.bowoon.database.util

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.bowoon.model.PeopleExternalIds
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@ProvidedTypeConverter
internal class ExternalIdsConverter(
    private val json: Json
) {
    @TypeConverter
    fun fromString(value: String?): PeopleExternalIds? =
        value?.let { json.decodeFromString<PeopleExternalIds>(it) }

    @TypeConverter
    fun fromExternalIds(value: PeopleExternalIds?): String? =
        value?.let { json.encodeToString(it) }
}