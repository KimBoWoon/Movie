package com.bowoon.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
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
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        val pagerState = rememberPagerState(initialPage = index) { imageList.size }

        HorizontalPager(
            modifier = modifier,
            state = pagerState
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                DynamicAsyncImageLoader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(imageList[it].aspectRatio?.toFloat() ?: 1f),
                    source = imageList[it].filePath ?: "",
                    contentDescription = "PosterView"
                )
                Indexer(current = it + 1, size = imageList.size)
            }
        }
    }
}

@Composable
fun BoxScope.Indexer(
    current: Int,
    size: Int
) {
    Box(
        modifier = Modifier
            .padding(end = dp20)
            .wrapContentSize()
            .background(color = Color(0x33000000), shape = RoundedCornerShape(dp20))
            .align(Alignment.TopEnd)
    ) {
        Text(
            modifier = Modifier
                .padding(all = dp5)
                .wrapContentSize()
                .align(Alignment.Center),
            text = "$current / $size",
            color = Color.White,
            fontSize = sp10,
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
            textAlign = TextAlign.Center
        )
    }
}