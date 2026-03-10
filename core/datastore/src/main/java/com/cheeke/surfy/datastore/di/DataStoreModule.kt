package com.cheeke.surfy.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import com.cheeke.surfy.common.Dispatcher
import com.cheeke.surfy.common.Dispatchers
import com.cheeke.surfy.common.di.ApplicationScope
import com.cheeke.surfy.datastore.protobuf.InternalDataPreferencesSerializer
import com.cheeke.surfy.core.datastore.InternalDataPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    @Singleton
    fun providesDatastore(
        @ApplicationContext context: Context,
        @Dispatcher(Dispatchers.IO) ioDispatcher: CoroutineDispatcher,
        @ApplicationScope scope: CoroutineScope,
        serializer: InternalDataPreferencesSerializer,
    ): DataStore<InternalDataPreferences> = DataStoreFactory.create(
        serializer = serializer,
        scope = CoroutineScope(context = scope.coroutineContext + ioDispatcher),
        migrations = listOf(),
        produceFile = { context.preferencesDataStoreFile(name = "surfy") }
    )
}