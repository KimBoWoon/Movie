package com.bowoon.data.di

import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.data.repository.DatabaseRepositoryImpl
import com.bowoon.data.repository.KOBISRepository
import com.bowoon.data.repository.KOBISRepositoryImpl
import com.bowoon.data.repository.MainMenuRepository
import com.bowoon.data.repository.MainMenuRepositoryImpl
import com.bowoon.data.repository.MyDataRepository
import com.bowoon.data.repository.MyDataRepositoryImpl
import com.bowoon.data.repository.TMDBRepository
import com.bowoon.data.repository.TMDBRepositoryImpl
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
    abstract fun bindKobisRepository(
        repository: KOBISRepositoryImpl
    ): KOBISRepository

    @Binds
    abstract fun bindUserRepository(
        repository: UserDataRepositoryImpl
    ): UserDataRepository

    @Binds
    abstract fun bindTMDBRepository(
        repository: TMDBRepositoryImpl
    ): TMDBRepository

    @Binds
    abstract fun bindDatabaseRepository(
        repository: DatabaseRepositoryImpl
    ): DatabaseRepository

    @Binds
    abstract fun bindMainMenuRepository(
        repository: MainMenuRepositoryImpl
    ): MainMenuRepository

    @Binds
    @Singleton
    abstract fun bindMyDataRepository(
        repository: MyDataRepositoryImpl
    ): MyDataRepository
}