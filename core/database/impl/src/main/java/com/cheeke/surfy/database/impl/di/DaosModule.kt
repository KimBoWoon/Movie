package com.cheeke.surfy.database.impl.di

import com.cheeke.surfy.database.impl.SurfyDatabase
import com.cheeke.surfy.database.impl.dao.KeywordDao
import com.cheeke.surfy.database.impl.dao.MovieDao
import com.cheeke.surfy.database.impl.dao.PeopleDao
import com.cheeke.surfy.database.impl.dao.TvDao
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