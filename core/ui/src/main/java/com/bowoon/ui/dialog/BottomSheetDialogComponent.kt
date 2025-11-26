package com.bowoon.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import com.bowoon.model.Image
import com.bowoon.ui.image.DynamicAsyncImageLoader
import com.bowoon.ui.utils.dp10
import com.bowoon.ui.utils.dp20
import com.bowoon.ui.utils.dp5
import com.bowoon.ui.utils.sp10
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalBottomSheetDialog(
    modifier: Modifier,
    state: SheetState,
    scope: CoroutineScope,
    index: Int,
    imageList: List<Image>,
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
        var currentIndex by remember { mutableIntStateOf(value = index + 1) }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(ratio = imageList.minOf { it.aspectRatio?.toFloat() ?: 1f }),
            contentAlignment = Alignment.Center
        ) {
            HorizontalPager(
                modifier = modifier,
                state = pagerState
            ) { index ->
                currentIndex = pagerState.currentPage + 1

                DynamicAsyncImageLoader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(ratio = imageList[index].aspectRatio?.toFloat() ?: 1f),
                    source = imageList[index].filePath ?: "",
                    contentDescription = "PosterView"
                )
            }

            Indexer(
                modifier = Modifier
                    .padding(top = dp10, end = dp20)
                    .wrapContentSize()
                    .background(color = Color(0x33000000), shape = RoundedCornerShape(dp20))
                    .align(Alignment.TopEnd),
                current = currentIndex,
                size = imageList.size
            )
        }
    }
}

@Composable
fun Indexer(
    modifier: Modifier,
    current: Int,
    size: Int
) {
    Box(
        modifier = modifier,
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