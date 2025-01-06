package com.bowoon.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bowoon.database.dao.MovieDao
import com.bowoon.database.model.MovieEntity
import com.bowoon.database.util.InstantConverter

@Database(
    entities = [MovieEntity::class],
    version = 1,
    autoMigrations = [],
    exportSchema = true,
)
@TypeConverters(InstantConverter::class)
internal abstract class MovieDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}