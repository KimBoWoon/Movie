package com.bowoon.data.di

import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.data.repository.DatabaseRepositoryImpl
import com.bowoon.data.repository.KobisRepository
import com.bowoon.data.repository.KobisRepositoryImpl
import com.bowoon.data.repository.TMDBRepository
import com.bowoon.data.repository.TMDBRepositoryImpl
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.data.repository.UserDataRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class DataModules {
    @Binds
    @ViewModelScoped
    abstract fun bindKobisRepository(
        repository: KobisRepositoryImpl
    ): KobisRepository

    @Binds
    @ViewModelScoped
    abstract fun bindUserRepository(
        repository: UserDataRepositoryImpl
    ): UserDataRepository

    @Binds
    @ViewModelScoped
    abstract fun bindTMDBRepository(
        repository: TMDBRepositoryImpl
    ): TMDBRepository

    @Binds
    @ViewModelScoped
    abstract fun bindDatabaseRepository(
        repository: DatabaseRepositoryImpl
    ): DatabaseRepository
}