package com.cheeke.surfy.datastore.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.datastore.rxjava3.RxDataStore
import androidx.datastore.rxjava3.RxDataStoreBuilder
import com.cheeke.surfy.core.datastore.InternalDataPreferences
import com.cheeke.surfy.datastore.protobuf.InternalDataPreferencesSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    @Singleton
    fun providesDatastore(
        @ApplicationContext context: Context,
        serializer: InternalDataPreferencesSerializer,
    ): RxDataStore<InternalDataPreferences> = RxDataStoreBuilder(
        serializer = serializer,
        produceFile = { context.preferencesDataStoreFile(name = "surfy") }
    ).build()
}