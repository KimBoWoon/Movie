package com.bowoon.data.di

import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.data.repository.DatabaseRepositoryImpl
import com.bowoon.data.repository.DetailRepository
import com.bowoon.data.repository.DetailRepositoryImpl
import com.bowoon.data.repository.MainMenuRepository
import com.bowoon.data.repository.MainMenuRepositoryImpl
import com.bowoon.data.repository.MyDataRepository
import com.bowoon.data.repository.MyDataRepositoryImpl
import com.bowoon.data.repository.PagingRepository
import com.bowoon.data.repository.PagingRepositoryImpl
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.data.repository.UserDataRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

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

    @Binds
    @Singleton
    abstract fun bindMyDataRepository(
        myDataRepository: MyDataRepositoryImpl
    ): MyDataRepository
}