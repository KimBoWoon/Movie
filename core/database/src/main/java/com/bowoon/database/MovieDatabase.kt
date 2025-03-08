package com.bowoon.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import com.bowoon.database.dao.MovieDao
import com.bowoon.database.dao.PeopleDao
import com.bowoon.database.model.MovieEntity
import com.bowoon.database.model.PeopleEntity

@Database(
    entities = [
        MovieEntity::class,
        PeopleEntity::class
    ],
    version = 2,
    autoMigrations = [
        AutoMigration(
            from = 1,
            to = 2,
            spec = DatabaseMigrations.Schema1to2::class
        )
    ],
    exportSchema = true,
)
internal abstract class MovieDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun peopleDao(): PeopleDao
}

internal object DatabaseMigrations {
    @DeleteColumn.Entries(
        DeleteColumn(tableName = "peoples", columnName = "gender"),
        DeleteColumn(tableName = "peoples", columnName = "biography"),
        DeleteColumn(tableName = "peoples", columnName = "birthday"),
        DeleteColumn(tableName = "peoples", columnName = "deathday"),
        DeleteColumn(tableName = "peoples", columnName = "combineCredits"),
        DeleteColumn(tableName = "peoples", columnName = "externalIds"),
        DeleteColumn(tableName = "peoples", columnName = "images"),
        DeleteColumn(tableName = "peoples", columnName = "placeOfBirth"),
        DeleteColumn(tableName = "movies", columnName = "releaseDate"),
        DeleteColumn(tableName = "movies", columnName = "releases"),
        DeleteColumn(tableName = "movies", columnName = "title")
    )
    class Schema1to2 : AutoMigrationSpec
}