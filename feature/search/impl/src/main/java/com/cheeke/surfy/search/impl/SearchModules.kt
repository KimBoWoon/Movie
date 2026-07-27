package com.cheeke.surfy.search.impl

import com.cheeke.surfy.search.api.KeywordDataBaseRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SearchModules {
    @Binds
    abstract fun bindKeywordDatabaseRepository(
        databaseRepository: KeywordDataBaseRepositoryImpl
    ): KeywordDataBaseRepository
}