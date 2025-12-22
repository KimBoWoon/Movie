package com.bowoon.database.di

import android.content.Context
import androidx.room.Room
import com.bowoon.database.DatabaseMigrations
import com.bowoon.database.MovieDatabase
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
    ): MovieDatabase = Room.databaseBuilder(
        context = context,
        klass = MovieDatabase::class.java,
        name = "movie-database",
    ).addMigrations(DatabaseMigrations.MIGRATION_3_4, DatabaseMigrations.MIGRATION_4_5)
        .build()
}