package com.bowoon.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.bowoon.data.util.POSTER_IMAGE_RATIO
import com.bowoon.model.Image
import com.bowoon.movie.core.ui.R
import com.bowoon.ui.dialog.ModalBottomSheetDialog
import com.bowoon.ui.image.DynamicAsyncImageLoader
import com.bowoon.ui.utils.roundedCornerClickable
import com.bowoon.ui.utils.dp10
import com.bowoon.ui.utils.dp100
import com.bowoon.ui.utils.dp200
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageComponent(
    images: List<Image>
) {
    var isShowing by remember { mutableStateOf(value = false) }
    var index by remember { mutableIntStateOf(value = 0) }
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    if (images.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = stringResource(id = R.string.movie_image_not_found))
        }
    } else {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(minSize = dp100),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(all = dp10),
            horizontalArrangement = Arrangement.spacedBy(space = dp10),
            verticalItemSpacing = dp10
        ) {
            items(
                items = images,
                key = { image -> image.filePath ?: image }
            ) {
                DynamicAsyncImageLoader(
                    modifier = Modifier
                        .width(width = dp200)
                        .aspectRatio(ratio = it.aspectRatio?.toFloat() ?: 1f)
                        .roundedCornerClickable(
                            onClick = {
                                index = images.indexOf(it)
                                isShowing = true
                            }, cornerRadius = dp10
                        ),
                    source = it.filePath ?: "",
                    contentDescription = "moviePoster"
                )
            }
        }
    }

    if (isShowing) {
        ModalBottomSheetDialog(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(POSTER_IMAGE_RATIO),
            state = modalBottomSheetState,
            scope = scope,
            index = index,
            imageList = images,
            onClickCancel = {
                scope.launch {
                    isShowing = false
                    modalBottomSheetState.hide()
                }
            }
        )
    }
}