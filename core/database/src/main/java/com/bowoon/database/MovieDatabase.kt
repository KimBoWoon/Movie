package com.bowoon.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bowoon.database.dao.MovieDao
import com.bowoon.database.dao.PeopleDao
import com.bowoon.database.model.MovieEntity
import com.bowoon.database.model.PeopleEntity
import com.bowoon.database.util.CombineCreditsConverter
import com.bowoon.database.util.ExternalIdsConverter
import com.bowoon.database.util.ImagesConverter
import com.bowoon.database.util.InstantConverter
import com.bowoon.database.util.TMDBReleasesConverter

@Database(
    entities = [
        MovieEntity::class,
        PeopleEntity::class
    ],
    version = 1,
    autoMigrations = [],
    exportSchema = true,
)
@TypeConverters(
    value = [
        InstantConverter::class,
        TMDBReleasesConverter::class,
        CombineCreditsConverter::class,
        ExternalIdsConverter::class,
        ImagesConverter::class
    ]
)
internal abstract class MovieDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun peopleDao(): PeopleDao
}