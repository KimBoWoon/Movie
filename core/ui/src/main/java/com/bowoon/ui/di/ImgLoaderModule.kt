package com.bowoon.ui.di

import android.content.Context
import androidx.compose.ui.util.trace
import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.request.crossfade
import coil3.util.DebugLogger
import com.bowoon.movie.core.ui.BuildConfig
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
        @ApplicationContext context: Context
    ): ImageLoader = trace("ImageLoader") {
        ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, MEMORY_CACHE_PERCENT)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(directory = File(context.externalCacheDir, CACHE_FOLDER_NAME))
                    .maxSizeBytes(CACHE_BYTES_SIZE)
                    .build()
            }
            .crossfade(enable = true)
            .apply {
                if (BuildConfig.DEBUG) {
                    logger(logger = DebugLogger())
                }
            }
            .build()
    }
}