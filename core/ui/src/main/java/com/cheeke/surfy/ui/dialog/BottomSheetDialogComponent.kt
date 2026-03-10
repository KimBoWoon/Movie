package com.cheeke.surfy.ui.dialog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
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
import com.cheeke.surfy.ui.utils.dp5
import com.cheeke.surfy.ui.utils.sp10
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalBottomSheetDialog(
    state: SheetState,
    scope: CoroutineScope,
    onClickCancel: () -> Unit,
    content: @Composable () -> Unit
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
        content()
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