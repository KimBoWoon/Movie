package com.bowoon.database.di

import android.content.Context
import androidx.room.Room
import com.bowoon.database.MovieDatabase
import com.bowoon.database.util.CombineCreditsConverter
import com.bowoon.database.util.ExternalIdsConverter
import com.bowoon.database.util.ImagesConverter
import com.bowoon.database.util.TMDBReleasesConverter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {
    @Provides
    @Singleton
    fun providesMovieDatabase(
        @ApplicationContext context: Context,
        json: Json
    ): MovieDatabase = Room.databaseBuilder(
        context,
        MovieDatabase::class.java,
        "movie-database",
    ).addTypeConverter(TMDBReleasesConverter(json))
        .addTypeConverter(CombineCreditsConverter(json))
        .addTypeConverter(ExternalIdsConverter(json))
        .addTypeConverter(ImagesConverter(json))
        .build()
}
