package com.cheeke.surfy.database.di

import com.cheeke.surfy.database.SurfyDatabase
import com.cheeke.surfy.database.dao.KeywordDao
import com.cheeke.surfy.database.dao.MovieDao
import com.cheeke.surfy.database.dao.PeopleDao
import com.cheeke.surfy.database.dao.TvDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object DaosModule {
    @Provides
    fun providesMovieDao(
        database: SurfyDatabase,
    ): MovieDao = database.movieDao()

    @Provides
    fun providesPeopleDao(
        database: SurfyDatabase,
    ): PeopleDao = database.peopleDao()

    @Provides
    fun providesTvDao(
        database: SurfyDatabase,
    ): TvDao = database.tvDao()

    @Provides
    fun providesKeywordDao(
        database: SurfyDatabase,
    ): KeywordDao = database.keywordDao()
}