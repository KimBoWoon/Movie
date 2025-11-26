package com.bowoon.movie.startup

import android.content.Context
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface InitializerEntryPoint {
    fun inject(imageLoaderInitializer: ImageLoaderInitializer)
    fun inject(firebaseInitializer: FirebaseInitializer)
    fun inject(syncInitializer: SyncInitializer)

    companion object {
        fun resolve(context: Context): InitializerEntryPoint {
            val appContext = context.applicationContext ?: throw IllegalStateException()
            return EntryPointAccessors.fromApplication(
                context = appContext,
                entryPoint = InitializerEntryPoint::class.java
            )
        }
    }
}