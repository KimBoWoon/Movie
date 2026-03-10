package com.cheeke.surfy.ui.di

import android.content.Context
import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.request.allowHardware
import coil3.request.crossfade
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton

private const val TAG = "#ImageLoader"
const val CACHE_FOLDER_NAME = "image"
const val MEMORY_CACHE_PERCENT = 0.25
const val CACHE_BYTES_SIZE = 512L * 1024 * 1024 // 512MB

@Module
@InstallIn(SingletonComponent::class)
object ImgLoaderModule {
    @Provides
    @Singleton
    fun imageLoader(
        @ApplicationContext context: Context,
    ): ImageLoader = ImageLoader.Builder(context = context)
        .memoryCache {
            MemoryCache.Builder()
                .maxSizePercent(context = context, percent = MEMORY_CACHE_PERCENT)
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(directory = File(context.externalCacheDir, CACHE_FOLDER_NAME))
                .maxSizeBytes(size = CACHE_BYTES_SIZE)
                .build()
        }
        .crossfade(enable = true)
        .allowHardware(enable = true)
        .build()
}