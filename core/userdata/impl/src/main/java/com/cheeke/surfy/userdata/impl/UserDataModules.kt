package com.cheeke.surfy.userdata.impl

import com.cheeke.surfy.userdata.api.UserDataRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(value = [SingletonComponent::class])
abstract class UserDataModules {
    @Binds
    abstract fun bindUserDataRepository(
        userDataRepository: UserDataRepositoryImpl
    ): UserDataRepository
}