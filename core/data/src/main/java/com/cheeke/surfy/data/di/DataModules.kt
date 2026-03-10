package com.cheeke.surfy.data.di

import com.cheeke.surfy.data.repository.DatabaseRepository
import com.cheeke.surfy.data.repository.DatabaseRepositoryImpl
import com.cheeke.surfy.data.repository.DetailRepository
import com.cheeke.surfy.data.repository.DetailRepositoryImpl
import com.cheeke.surfy.data.repository.MainMenuRepository
import com.cheeke.surfy.data.repository.MainMenuRepositoryImpl
import com.cheeke.surfy.data.repository.PagingRepository
import com.cheeke.surfy.data.repository.PagingRepositoryImpl
import com.cheeke.surfy.data.repository.UserDataRepository
import com.cheeke.surfy.data.repository.UserDataRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModules {
    @Binds
    abstract fun bindUserRepository(
        userDataRepository: UserDataRepositoryImpl
    ): UserDataRepository

    @Binds
    abstract fun bindDetailRepository(
        detailRepository: DetailRepositoryImpl
    ): DetailRepository

    @Binds
    abstract fun bindDatabaseRepository(
        databaseRepository: DatabaseRepositoryImpl
    ): DatabaseRepository

    @Binds
    abstract fun bindMainMenuRepository(
        mainMenuRepository: MainMenuRepositoryImpl
    ): MainMenuRepository

    @Binds
    abstract fun bindPagingRepository(
        pagingRepository: PagingRepositoryImpl
    ): PagingRepository
}