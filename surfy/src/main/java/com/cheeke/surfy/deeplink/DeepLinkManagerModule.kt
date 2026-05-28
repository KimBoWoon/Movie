package com.cheeke.surfy.deeplink

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DeepLinkManagerModule {
    @Binds
    @Singleton
    abstract fun bindDeepLinkManager(deepLinkManagerImpl: DeepLinkManagerImpl): DeepLinkManager
}