package com.bowoon.common

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.Environment
import java.io.File
import kotlin.math.roundToInt

val Int.px: Int get() = (this.toFloat() * Resources.getSystem().displayMetrics.density).roundToInt()

object StorageUtils {
    private const val NO_MEDIA = ".nomedia"

    fun getCacheDirectory(context: Context, dir: String?): File? {
        if (dir == null) return null
        val cacheDir = if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            File(context.externalCacheDir, dir)
        } else {
            File(context.cacheDir, dir)
        }
        if (!cacheDir.exists()) {
            if (!cacheDir.mkdirs()) {
                return null
            }
            kotlin.runCatching {
                val ret = File(cacheDir, NO_MEDIA).createNewFile()
                Log.d("StorageUtils", "getExternalCacheDir create: $ret")
            }.onFailure {
                Log.printStackTrace(it)
            }
        }
        return cacheDir
    }
}

//fun NavController.navigate(
//    route: String,
//    args: Bundle,
//    navOptions: NavOptions? = null,
//    navigatorExtras: Navigator.Extras? = null
//) {
//    graph.findNode(route = route)?.id?.let {
//        navigate(it, args = args, navOptions = navOptions, navigatorExtras = navigatorExtras)
//    }
//}

inline fun <reified T> Bundle.getSafetyParcelable(key: String): T? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelable(key, T::class.java)
    } else {
        getParcelable(key)
    }

inline fun <reified T> Intent.getSafetyParcelable(key: String): T? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelableExtra(key, T::class.java)
    } else {
        getParcelableExtra(key)
    }