package com.bowoon.movie.startup

import android.content.Context
import androidx.startup.Initializer
import coil3.ImageLoader
import com.bowoon.common.Log
import javax.inject.Inject

class ImageLoaderInitializer : Initializer<ImageLoader> {
    @Inject
    lateinit var imageLoader: ImageLoader

    override fun create(context: Context): ImageLoader {
        InitializerEntryPoint.resolve(context).inject(imageLoaderInitializer = this)

        Log.d("ImageLoaderInitializer end")
        return imageLoader
    }

    override fun dependencies(): List<Class<out Initializer<*>?>?> =
        listOf(DependencyGraphInitializer::class.java)
}