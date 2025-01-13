package com.bowoon.database.util

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.bowoon.model.CombineCredits
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@ProvidedTypeConverter
internal class CombineCreditsConverter(
    private val json: Json
) {
    @TypeConverter
    fun fromString(value: String?): CombineCredits? =
        value?.let { json.decodeFromString<CombineCredits>(it) }

    @TypeConverter
    fun fromCombineCredits(value: CombineCredits?): String? =
        value?.let { json.encodeToString(it) }
}