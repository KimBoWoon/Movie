package com.cheeke.surfy.common.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import jakarta.inject.Qualifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ActivityRetainedScopeCoroutine

@Module
@InstallIn(ActivityRetainedComponent::class)
object CoroutineModule {
    @Provides
    @ActivityRetainedScoped
    @ActivityRetainedScopeCoroutine
    fun provideScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.Main)
    }
}