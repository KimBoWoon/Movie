package com.bowoon.database.util

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.bowoon.model.Releases
import kotlinx.serialization.json.Json

@ProvidedTypeConverter
internal class TMDBReleasesConverter(
    private val json: Json
) {
    @TypeConverter
    fun fromString(value: String?): Releases? =
        value?.let { json.decodeFromString<Releases>(it) }

    @TypeConverter
    fun fromTMDBRelease(value: Releases?): String? =
        value?.let { json.encodeToString(it) }
}