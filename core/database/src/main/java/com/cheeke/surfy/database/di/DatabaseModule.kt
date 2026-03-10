package com.cheeke.surfy.database.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.cheeke.surfy.database.SurfyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {
    @Provides
    @Singleton
    fun providesMovieDatabase(
        @ApplicationContext context: Context
    ): SurfyDatabase = Room.databaseBuilder(
        context = context,
        klass = SurfyDatabase::class.java,
        name = "surfy-database"
    )/*.addCallback(
        callback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
//                initMovies(db = db)
//                initPeoples(db = db)
//                initTvs(db = db)
            }
        }
    )*/.build()
}