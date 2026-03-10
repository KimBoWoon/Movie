package com.cheeke.surfy.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SharedTransitionScope
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import com.cheeke.surfy.model.Image
import com.cheeke.surfy.ui.image.DynamicAsyncImageLoader
import com.cheeke.surfy.ui.utils.dp0
import com.cheeke.surfy.ui.utils.dp10
import com.cheeke.surfy.ui.utils.dp12
import com.cheeke.surfy.ui.utils.dp120
import com.cheeke.surfy.ui.utils.dp16
import com.cheeke.surfy.ui.utils.dp209
import com.cheeke.surfy.ui.utils.dp246
import com.cheeke.surfy.ui.utils.dp260
import com.cheeke.surfy.ui.utils.dp433
import com.cheeke.surfy.ui.utils.roundedCornerClickable

enum class ImageType {
    BACKDROP,
    POSTER
}

@Composable
fun ImagesComponent(
    backdrops: List<Image>,
    posters: List<Image>,
    sharedTransitionScope: SharedTransitionScope,
    selectedImage: Image?,
    selectedIndex: Int?,
    overlayVisible: Boolean,
    onSelect: (ImageType, Image, Int) -> Unit
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
                type = ImageType.BACKDROP,
                images = backdrops,
                width = dp260,
                sharedTransitionScope = sharedTransitionScope,
                selectedImage = selectedImage,
                selectedIndex = selectedIndex,
                selectedType = ImageType.BACKDROP,
                overlayVisible = overlayVisible,
                onSelect = onSelect
            )
        }

        if (posters.isNotEmpty()) {
            Spacer(modifier = Modifier.height(height = dp16))
            SubSectionTitleComponent(text = "Posters")
            Spacer(modifier = Modifier.height(height = dp10))
            ImageRow(
                type = ImageType.POSTER,
                images = posters,
                width = dp120,
                sharedTransitionScope = sharedTransitionScope,
                selectedImage = selectedImage,
                selectedIndex = selectedIndex,
                selectedType = ImageType.POSTER,
                overlayVisible = overlayVisible,
                onSelect = onSelect
            )
        }
    }
}

@Composable
private fun ImageRow(
    type: ImageType,
    images: List<Image>,
    width: Dp,
    sharedTransitionScope: SharedTransitionScope,
    selectedImage: Image?,
    selectedIndex: Int?,
    selectedType: ImageType?,
    overlayVisible: Boolean,
    onSelect: (ImageType, Image, Int) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = dp10),
        horizontalArrangement = Arrangement.spacedBy(space = dp10)
    ) {
        itemsIndexed(
            items = images,
            key = { index, image -> image.filePath ?: "image-$type-$index" }
        ) { index, image ->
            val key = "image-$type-$index"
            val isSelected = (selectedType == type && selectedIndex == index)
            val hideOriginal = overlayVisible && isSelected

            with(receiver = sharedTransitionScope) {
                AnimatedVisibility(
                    visible = true,
                    enter = EnterTransition.None,
                    exit = ExitTransition.None
                ) {
                    val cell = Modifier
                        .width(width = width)
                        .aspectRatio(ratio = image.aspectRatio?.toFloat() ?: 1f)

                    AnimatedContent(
                        targetState = hideOriginal,
                        label = "thumbSwap",
                        transitionSpec = {
                            EnterTransition.None togetherWith ExitTransition.None
                        }
                    ) { hidden ->
                        if (hidden) {
                            Spacer(modifier = cell)
                        } else {
                            DynamicAsyncImageLoader(
                                source = image.filePath.orEmpty(),
                                contentDescription = null,
                                modifier = cell
                                    .then(
                                        other = if (isSelected) {
                                            Modifier.sharedElement(
                                                sharedContentState = rememberSharedContentState(key = key),
                                                animatedVisibilityScope = this@AnimatedVisibility
                                            )
                                        } else {
                                            Modifier
                                        }
                                    ).roundedCornerClickable(
                                        onClick = { onSelect(type, image, index) },
                                        cornerRadius = dp10
                                    ),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SharedTransitionScope.ImageOverlay(
    selectedType: ImageType?,
    selectedImage: Image?,
    selectedIndex: Int?,
    overlayVisible: Boolean,
    overlayImageVisible: Boolean,
    onDismiss: () -> Unit,
) {
    // 백버튼으로 닫기
    BackHandler(enabled = overlayVisible) {
        onDismiss()
    }

    if (overlayVisible) {
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
            visible = overlayImageVisible,
            enter = EnterTransition.None,
            exit = ExitTransition.None
        ) {
            val img = selectedImage ?: return@AnimatedVisibility
            val key = "image-$selectedType-$selectedIndex"

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