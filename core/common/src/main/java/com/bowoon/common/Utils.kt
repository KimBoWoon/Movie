package com.bowoon.common

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import kotlin.math.roundToInt

val Configuration.isSystemInDarkTheme
    get() = (uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

val Int.px: Int get() = (this.toFloat() * Resources.getSystem().displayMetrics.density).roundToInt()

@Suppress("DEPRECATION")
inline fun <reified T> Bundle.getSafetyParcelable(key: String): T? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelable(key, T::class.java)
    } else {
        getParcelable(key)
    }

@Suppress("DEPRECATION")
inline fun <reified T> Intent.getSafetyParcelable(key: String): T? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelableExtra(key, T::class.java)
    } else {
        getParcelableExtra(key)
    }

fun getVersionName(context: Context): String = runCatching {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        context.packageManager.getPackageInfo(context.packageName, PackageManager.PackageInfoFlags.of(0))
    } else {
        context.packageManager.getPackageInfo(context.packageName, 0)
    }.versionName ?: ""
}.getOrElse { e ->
    Log.printStackTrace(tr = e)
    ""
}