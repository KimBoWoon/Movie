package com.cheeke.surfy.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.cheeke.surfy.core.ui.R
import com.cheeke.surfy.model.Image
import com.cheeke.surfy.ui.image.DynamicAsyncImageLoader
import com.cheeke.surfy.ui.utils.dp0
import com.cheeke.surfy.ui.utils.dp10
import com.cheeke.surfy.ui.utils.dp12
import com.cheeke.surfy.ui.utils.dp120
import com.cheeke.surfy.ui.utils.dp16
import com.cheeke.surfy.ui.utils.dp209
import com.cheeke.surfy.ui.utils.dp246
import com.cheeke.surfy.ui.utils.dp433
import com.cheeke.surfy.ui.utils.roundedCornerClickable

enum class ImageType(val label: String) {
    BACKDROP(label = "backdrops"),
    POSTER(label = "posters")
}

@Composable
fun ImagesComponent(
    backdrops: List<Image>,
    posters: List<Image>,
    sharedTransitionScope: SharedTransitionScope,
    selectedImage: Image?,
    onSelect: (Image) -> Unit
) {
    if (backdrops.isEmpty() && posters.isEmpty()) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(height = if (backdrops.isNotEmpty() && posters.isNotEmpty()) dp433 else if (backdrops.isNotEmpty()) dp209 else if (posters.isNotEmpty()) dp246 else dp0)
    ) {
        SectionHeader(title = stringResource(id = R.string.movie_image))

        if (backdrops.isNotEmpty()) {
            Spacer(modifier = Modifier.height(height = dp12))
            SubSectionTitleComponent(text = stringResource(id = R.string.backdrops))
            Spacer(modifier = Modifier.height(height = dp10))
            ImageRow(
                type = ImageType.BACKDROP,
                images = backdrops,
                sharedTransitionScope = sharedTransitionScope,
                selectedImage = selectedImage,
                onSelect = onSelect
            )
        }

        if (posters.isNotEmpty()) {
            Spacer(modifier = Modifier.height(height = dp16))
            SubSectionTitleComponent(text = stringResource(id = R.string.posters))
            Spacer(modifier = Modifier.height(height = dp10))
            ImageRow(
                type = ImageType.POSTER,
                images = posters,
                sharedTransitionScope = sharedTransitionScope,
                selectedImage = selectedImage,
                onSelect = onSelect
            )
        }
    }
}

@Composable
private fun ImageRow(
    type: ImageType,
    images: List<Image>,
    sharedTransitionScope: SharedTransitionScope,
    selectedImage: Image?,
    onSelect: (Image) -> Unit
) {
    val scrollState = rememberLazyListState()
    var savedIndex by remember { mutableIntStateOf(value = 0) }
    var savedOffset by remember { mutableIntStateOf(value = 0) }

    LaunchedEffect(key1 = selectedImage) {
        scrollState.scrollToItem(index = savedIndex, scrollOffset = savedOffset)
    }

    LazyRow(
        modifier = Modifier.semantics { contentDescription = type.label },
        state = scrollState,
        contentPadding = PaddingValues(horizontal = dp10),
        horizontalArrangement = Arrangement.spacedBy(space = dp10)
    ) {
        items(
            items = images,
            key = { image -> image.filePath.orEmpty() }
        ) { image ->
            val key = "image-${image.filePath}"

            with(receiver = sharedTransitionScope) {
                AnimatedVisibility(
                    modifier = Modifier.animateItem(),
                    visible = selectedImage != image,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    DynamicAsyncImageLoader(
                        source = image.filePath.orEmpty(),
                        contentDescription = image.filePath,
                        modifier = Modifier
                            .width(width = if (type == ImageType.BACKDROP) dp246 else dp120)
                            .sharedElement(
                                sharedContentState = rememberSharedContentState(key = key),
                                animatedVisibilityScope = this@AnimatedVisibility
                            )
                            .roundedCornerClickable(
                                onClick = {
                                    savedIndex = scrollState.firstVisibleItemIndex
                                    savedOffset = scrollState.firstVisibleItemScrollOffset
                                    onSelect(image)
                                },
                                cornerRadius = dp10
                            ),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

@Composable
fun SharedTransitionScope.ImageOverlay(
    selectedImage: Image?,
    onDismiss: () -> Unit,
) {
    // 백버튼으로 닫기
    BackHandler(enabled = selectedImage != null) {
        onDismiss()
    }

    AnimatedContent(
        modifier = Modifier,
        targetState = selectedImage,
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
        label = "ImageOverlay"
    ) { image ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (image != null) {
                val key = "image-${image.filePath}"

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable(
                            interactionSource = null,
                            indication = null,
                            onClick = {}
                        )
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(alignment = Alignment.TopEnd)
                        .padding(top = dp16, end = dp16)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = Color.White
                    )
                }

                DynamicAsyncImageLoader(
                    source = image.filePath.orEmpty(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(ratio = image.aspectRatio?.toFloat() ?: 1f)
                        .sharedElement(
                            sharedContentState = rememberSharedContentState(key = key),
                            animatedVisibilityScope = this@AnimatedContent
                        )
                        .clip(shape = RoundedCornerShape(size = dp10)),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}