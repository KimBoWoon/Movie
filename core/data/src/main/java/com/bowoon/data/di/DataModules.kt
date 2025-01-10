package com.bowoon.data.di

import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.data.repository.DatabaseRepositoryImpl
import com.bowoon.data.repository.KobisRepository
import com.bowoon.data.repository.KobisRepositoryImpl
import com.bowoon.data.repository.SyncRepository
import com.bowoon.data.repository.SyncRepositoryImpl
import com.bowoon.data.repository.TMDBRepository
import com.bowoon.data.repository.TMDBRepositoryImpl
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.data.repository.UserDataRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModules {
    @Binds
    abstract fun bindKobisRepository(
        repository: KobisRepositoryImpl
    ): KobisRepository

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
    abstract fun bindSyncRepository(
        repository: SyncRepositoryImpl
    ): SyncRepository
}