package com.cheeke.surfy.home.impl

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(value = [SingletonComponent::class])
abstract class HomeModules {
    @Binds
    abstract fun bindSearchRepository(
        homeRepository: HomeRepositoryImpl
    ): HomeRepository
}