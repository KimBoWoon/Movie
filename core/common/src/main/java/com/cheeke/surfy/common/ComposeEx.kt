package com.cheeke.surfy.common

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import com.slack.circuit.retained.rememberRetained

@Composable
fun retainedScrollState(
    initial: Int = 0
): ScrollState {
    var scrollState by rememberRetained { mutableIntStateOf(value = initial) }

    val listState = rememberScrollState(initial = scrollState)

    LaunchedEffect(key1 = listState) {
        snapshotFlow {
            listState.value
        }.collect { value ->
            scrollState = value
        }
    }

    return listState
}

@Composable
fun retainedLazyListState(
    initialFirstVisibleItemIndex: Int = 0,
    initialFirstVisibleItemScrollOffset: Int = 0
): LazyListState {
    var firstVisibleIndex by rememberRetained { mutableIntStateOf(value = initialFirstVisibleItemIndex) }
    var firstVisibleOffset by rememberRetained { mutableIntStateOf(value = initialFirstVisibleItemScrollOffset) }

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = firstVisibleIndex,
        initialFirstVisibleItemScrollOffset = firstVisibleOffset
    )

    LaunchedEffect(key1 = listState) {
        snapshotFlow {
            listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset
        }.collect { (index, offset) ->
            firstVisibleIndex = index
            firstVisibleOffset = offset
        }
    }

    return listState
}

@Composable
fun retainedLazyGridListState(
    initialFirstVisibleItemIndex: Int = 0,
    initialFirstVisibleItemScrollOffset: Int = 0
): LazyGridState {
    var firstVisibleIndex by rememberRetained { mutableIntStateOf(value = initialFirstVisibleItemIndex) }
    var firstVisibleOffset by rememberRetained { mutableIntStateOf(value = initialFirstVisibleItemScrollOffset) }

    val listState = rememberLazyGridState(
        initialFirstVisibleItemIndex = firstVisibleIndex,
        initialFirstVisibleItemScrollOffset = firstVisibleOffset
    )

    LaunchedEffect(key1 = listState) {
        snapshotFlow {
            listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset
        }.collect { (index, offset) ->
            firstVisibleIndex = index
            firstVisibleOffset = offset
        }
    }

    return listState
}