package com.bowoon.ui

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bowoon.model.DetailImage
import com.bowoon.ui.image.DynamicAsyncImageLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalBottomSheetDialog(
    modifier: Modifier,
    state: SheetState,
    scope: CoroutineScope,
    index: Int,
    imageList: List<DetailImage>,
    onClickCancel: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = {
            scope.launch {
                onClickCancel()
                state.hide()
            }
        },
        sheetState = state,
//        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        val pagerState = rememberPagerState(initialPage = index) { imageList.size }

        HorizontalPager(
            modifier = modifier,
            state = pagerState
        ) {
            DynamicAsyncImageLoader(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(imageList[it].aspectRatio?.toFloat() ?: 1f),
                source = imageList[it].filePath ?: "",
                contentDescription = "PosterView"
            )
        }
    }
}