package com.bowoon.database.util

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.bowoon.model.TMDBMovieDetailReleases
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@ProvidedTypeConverter
internal class TMDBReleasesConverter(
    private val json: Json
) {
    @TypeConverter
    fun fromString(value: String?): TMDBMovieDetailReleases? =
        value?.let { json.decodeFromString<TMDBMovieDetailReleases>(it) }

    @TypeConverter
    fun fromTMDBRelease(value: TMDBMovieDetailReleases?): String? =
        value?.let { json.encodeToString(it) }
}