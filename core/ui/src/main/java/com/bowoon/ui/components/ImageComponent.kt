package com.bowoon.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import com.bowoon.model.Image
import com.bowoon.ui.image.DynamicAsyncImageLoader
import com.bowoon.ui.utils.dp0
import com.bowoon.ui.utils.dp10
import com.bowoon.ui.utils.dp12
import com.bowoon.ui.utils.dp120
import com.bowoon.ui.utils.dp16
import com.bowoon.ui.utils.dp209
import com.bowoon.ui.utils.dp246
import com.bowoon.ui.utils.dp260
import com.bowoon.ui.utils.dp433
import com.bowoon.ui.utils.roundedCornerClickable

@Composable
fun ImagesComponent(
    backdrops: List<Image>,
    posters: List<Image>,
    sharedTransitionScope: SharedTransitionScope,
    selectedImage: Image?,
    onSelect: (Image, Int) -> Unit
) {
    if (backdrops.isEmpty() && posters.isEmpty()) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(height = if (backdrops.isNotEmpty() && posters.isNotEmpty()) dp433 else if (backdrops.isNotEmpty()) dp209 else if (posters.isNotEmpty()) dp246 else dp0)
    ) {
        SectionHeader(title = "Images"/*, actionText = "See all", onActionClick = onSeeAll*/)

        if (backdrops.isNotEmpty()) {
            Spacer(modifier = Modifier.height(height = dp12))
            SubSectionTitleComponent(text = "Backdrops")
            Spacer(modifier = Modifier.height(height = dp10))
            ImageRow(
                images = backdrops,
                width = dp260,
                sharedTransitionScope = sharedTransitionScope,
                selectedImage = selectedImage,
                onSelect = onSelect
            )
        }

        if (posters.isNotEmpty()) {
            Spacer(modifier = Modifier.height(height = dp16))
            SubSectionTitleComponent(text = "Posters")
            Spacer(modifier = Modifier.height(height = dp10))
            ImageRow(
                images = posters,
                width = dp120,
                sharedTransitionScope = sharedTransitionScope,
                selectedImage = selectedImage,
                onSelect = onSelect
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ImageRow(
    images: List<Image>,
    width: Dp,
    sharedTransitionScope: SharedTransitionScope,
    selectedImage: Image?,
    onSelect: (Image, Int) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = dp10),
        horizontalArrangement = Arrangement.spacedBy(space = dp10)
    ) {
        itemsIndexed(
            items = images,
            key = { index, image -> image.filePath ?: "image-$index" }
        ) { index, image ->
            val key = remember(key1 = image.filePath) { image.filePath ?: "image-$index" }

            with(receiver = sharedTransitionScope) {
                AnimatedVisibility(
                    visible = true,
                    enter = EnterTransition.None,
                    exit = ExitTransition.None
                ) {
                    val modifierWithSharedElement = Modifier
                        .width(width = width)
                        .aspectRatio(ratio = image.aspectRatio?.toFloat() ?: 1f)
                        .sharedElement(
                            sharedContentState = rememberSharedContentState(key = key),
                            animatedVisibilityScope = this@AnimatedVisibility
                        ).roundedCornerClickable(
                            onClick = { onSelect(image, index) },
                            cornerRadius = dp10
                        )
                    val modifierWithOutSharedElement = Modifier
                        .width(width = width)
                        .aspectRatio(ratio = image.aspectRatio?.toFloat() ?: 1f)
                        .roundedCornerClickable(
                            onClick = { onSelect(image, index) },
                            cornerRadius = dp10
                        )
                    DynamicAsyncImageLoader(
                        source = image.filePath.orEmpty(),
                        contentDescription = null,
                        modifier = if (selectedImage == null) modifierWithSharedElement else modifierWithOutSharedElement,
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.ImageOverlay(
    selectedImage: Image?,
    selectedIndex: Int?,
    onDismiss: () -> Unit,
) {
    // 백버튼으로 닫기
    BackHandler(enabled = selectedImage != null) {
        onDismiss()
    }

    if (selectedImage != null) {
        Box(
            Modifier
                .fillMaxSize()
                .background(color = Color(color = 0xEB000000))
                .clickable(
                    interactionSource = null,
                    indication = null,
                    onClick = {}
                )
        ) {
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
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AnimatedVisibility(
            modifier = Modifier.align(alignment = Alignment.Center),
            visible = selectedImage != null,
            enter = EnterTransition.None,
            exit = ExitTransition.None
        ) {
            val img = selectedImage ?: return@AnimatedVisibility
            val key = remember(key1 = img.filePath) { img.filePath ?: "image-$selectedIndex" }

            DynamicAsyncImageLoader(
                source = img.filePath.orEmpty(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(ratio = img.aspectRatio?.toFloat() ?: 1f)
                    .sharedElement(
                        sharedContentState = rememberSharedContentState(key = key),
                        animatedVisibilityScope = this@AnimatedVisibility
                    ).clip(shape = RoundedCornerShape(size = dp10)),
                contentScale = ContentScale.Fit
            )
        }
    }
}