package com.bowoon.database.util

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.bowoon.model.DetailImage
import kotlinx.serialization.json.Json

@ProvidedTypeConverter
internal class ImagesConverter(
    private val json: Json
) {
    @TypeConverter
    fun fromString(value: String?): List<DetailImage>? =
        value?.let { json.decodeFromString<List<DetailImage>>(it) }

    @TypeConverter
    fun fromImages(value: List<DetailImage>?): String? =
        value?.let { json.encodeToString(it) }
}