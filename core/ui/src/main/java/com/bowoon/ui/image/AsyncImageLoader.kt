package com.bowoon.ui.image

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Unspecified
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import coil3.Image
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.bowoon.common.Log
import com.bowoon.ui.theme.LocalTintTheme

@Composable
fun DynamicAsyncImageLoader(
    source: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    placeholder: Painter = ColorPainter(Color.Gray),
    error: Painter = ColorPainter(Color.Gray)
) {
    val iconTint = LocalTintTheme.current.iconTint
    val isLocalInspection = LocalInspectionMode.current
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }
    val imageLoader = rememberAsyncImagePainter(
        model = source,
        onState = { state ->
            isLoading = state is AsyncImagePainter.State.Loading
            isError = state is AsyncImagePainter.State.Error
        }
    )

    Box(
        contentAlignment = Alignment.Center,
    ) {
        if (isLoading && !isLocalInspection) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.tertiary,
            )
        }
        when (isError) {
            true -> {
                Image(
                    modifier = modifier,
                    contentScale = ContentScale.Crop,
                    painter = error,
                    contentDescription = contentDescription,
                    colorFilter = if (iconTint != Unspecified) ColorFilter.tint(iconTint) else null,
                )
            }
            false -> {
                Image(
                    modifier = modifier,
                    contentScale = ContentScale.Crop,
                    painter = if (!isLocalInspection) imageLoader else placeholder,
                    contentDescription = contentDescription,
                    colorFilter = if (iconTint != Unspecified) ColorFilter.tint(iconTint) else null,
                )
            }
        }
    }
}

@JvmName("ContextExtensionGetScreenHeight")
fun Context.getScreenHeight(): Int =
    runCatching {
        resources.displayMetrics.heightPixels
    }.getOrElse { e ->
        Log.printStackTrace(e)
        val displayMetrics = DisplayMetrics()
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager?
        wm?.defaultDisplay?.getMetrics(displayMetrics)
        displayMetrics.heightPixels
    }

fun getScreenHeight(context: Context): Int =
    runCatching {
        context.resources.displayMetrics.heightPixels
    }.getOrElse { e ->
        Log.printStackTrace(e)
        val displayMetrics = DisplayMetrics()
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager?
        wm?.defaultDisplay?.getMetrics(displayMetrics)
        displayMetrics.heightPixels
    }

@JvmName("ContextExtensionGetClientHeight")
fun Context.getClientHeight(): Int =
    runCatching {
        when {
            this is Activity -> {
                val rect = Rect()
                window.decorView.getWindowVisibleDisplayFrame(rect)
                getScreenHeight() - rect.top
            }
            else -> getScreenHeight()
        }
    }.getOrElse { e ->
        Log.printStackTrace(e)
        -1
    }

fun getClientHeight(context: Context): Int =
    runCatching {
        when {
            context is Activity -> {
                val rect = Rect()
                context.window.decorView.getWindowVisibleDisplayFrame(rect)
                getScreenHeight(context) - rect.top
            }
            else -> getScreenHeight(context)
        }
    }.getOrElse { e ->
        Log.printStackTrace(e)
        -1
    }

fun resize(context: Context, image: Image): Pair<Int, Int>? {
    val rate: Float
    val bitmapWidth = image.width
    val bitmapHeight = image.height
    var newWidth = bitmapWidth.toFloat()
    var newHeight = bitmapHeight.toFloat()
    val clientHeight = getClientHeight(context).toFloat()

    if (bitmapWidth == 0 || bitmapHeight == 0 || clientHeight == 0f) {
        return null
    }

    if (bitmapWidth >= bitmapHeight) {
        if (bitmapWidth > clientHeight) {
            rate = clientHeight / bitmapWidth
            newHeight = bitmapHeight * rate
            newWidth = clientHeight
        }
    } else {
        if (bitmapHeight > clientHeight) {
            rate = clientHeight / bitmapHeight
            newWidth = bitmapWidth * rate
            newHeight = clientHeight
        }
    }

    return newWidth.toInt() to newHeight.toInt()
}