package com.bowoon.movie.startup

import android.content.Context
import androidx.startup.Initializer
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import com.bowoon.common.Log
import javax.inject.Inject

class ImageLoaderInitializer : Initializer<Unit>, SingletonImageLoader.Factory {
    @Inject
    lateinit var imageLoader: ImageLoader

    override fun create(context: Context): Unit {
        InitializerEntryPoint.resolve(context).inject(imageLoaderInitializer = this)

        Log.d("ImageLoaderInitializer end")
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader = imageLoader

    override fun dependencies(): List<Class<out Initializer<*>?>?> =
        emptyList()
}