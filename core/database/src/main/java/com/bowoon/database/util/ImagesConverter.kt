package com.bowoon.database.util

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.bowoon.model.PeopleImage
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@ProvidedTypeConverter
internal class ImagesConverter(
    private val json: Json
) {
    @TypeConverter
    fun fromString(value: String?): List<PeopleImage>? =
        value?.let { json.decodeFromString<List<PeopleImage>>(it) }

    @TypeConverter
    fun fromImages(value: List<PeopleImage>?): String? =
        value?.let { json.encodeToString(it) }
}