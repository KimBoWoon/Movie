package com.cheeke.surfy.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cheeke.surfy.database.dao.KeywordDao
import com.cheeke.surfy.database.dao.MovieDao
import com.cheeke.surfy.database.dao.PeopleDao
import com.cheeke.surfy.database.dao.TvDao
import com.cheeke.surfy.database.model.KeywordEntity
import com.cheeke.surfy.database.model.MovieEntity
import com.cheeke.surfy.database.model.NowPlayingMovieEntity
import com.cheeke.surfy.database.model.PeopleEntity
import com.cheeke.surfy.database.model.TvEntity
import com.cheeke.surfy.database.model.UpComingMovieEntity

@Database(
    entities = [
        MovieEntity::class,
        TvEntity::class,
        PeopleEntity::class,
        NowPlayingMovieEntity::class,
        UpComingMovieEntity::class,
        KeywordEntity::class
    ],
    version = 1,
    autoMigrations = [],
    exportSchema = true
)
internal abstract class SurfyDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun peopleDao(): PeopleDao
    abstract fun tvDao(): TvDao
    abstract fun keywordDao(): KeywordDao
}