package com.bowoon.ui.image

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
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
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
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        if (isLoading && !isLocalInspection) {
            // Display a progress bar while loading
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.tertiary,
            )
        }
        when (isError) {
            true -> {
                Image(
                    contentScale = ContentScale.Crop,
                    painter = error,
                    contentDescription = contentDescription,
                    colorFilter = if (iconTint != Unspecified) ColorFilter.tint(iconTint) else null,
                )
            }
            false -> {
                Image(
                    contentScale = ContentScale.Crop,
                    painter = if (!isLocalInspection) imageLoader else placeholder,
                    contentDescription = contentDescription,
                    colorFilter = if (iconTint != Unspecified) ColorFilter.tint(iconTint) else null,
                )
            }
        }
    }
}