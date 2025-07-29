package com.bowoon.ui.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.bowoon.ui.components.CircularProgressComponent
import com.bowoon.ui.utils.dp10

var imageUrl by mutableStateOf<String>(value = "")

@Composable
fun DynamicAsyncImageLoader(
    source: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    placeholder: Painter = ColorPainter(Color.Gray),
    error: Painter = ColorPainter(Color.Gray)
) {
    val imgUrl = imageUrl
    val isLocalInspection = LocalInspectionMode.current
    var isLoading by remember { mutableStateOf(value = true) }
    var isError by remember { mutableStateOf(value = false) }
    val imageLoader = rememberAsyncImagePainter(
        model = "$imgUrl$source",
        onState = { state ->
            isLoading = state is AsyncImagePainter.State.Loading
            isError = state is AsyncImagePainter.State.Error
        }
    )

    Box(
        contentAlignment = Alignment.Center,
    ) {
        if (isLoading && !isLocalInspection) {
            CircularProgressComponent(modifier = Modifier.align(Alignment.Center))
        }
        when (isError) {
            true -> {
                Image(
                    modifier = modifier
                        .testTag(tag = source)
                        .clip(shape = RoundedCornerShape(size = dp10)),
                    contentScale = ContentScale.Crop,
                    painter = error,
                    contentDescription = contentDescription
                )
            }
            false -> {
                Image(
                    modifier = modifier
                        .testTag(tag = source)
                        .clip(shape = RoundedCornerShape(size = dp10)),
                    contentScale = contentScale,
                    painter = if (!isLocalInspection) imageLoader else placeholder,
                    contentDescription = contentDescription
                )
            }
        }
    }
}