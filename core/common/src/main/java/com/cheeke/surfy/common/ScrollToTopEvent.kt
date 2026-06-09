package com.cheeke.surfy.common

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

private val _scrollTopEvent = MutableSharedFlow<ScrollTopEvent>(extraBufferCapacity = 1)
val scrollTopEvent = _scrollTopEvent.asSharedFlow()

sealed interface ScrollTopEvent {
    data object Home : ScrollTopEvent
    data object Favorite : ScrollTopEvent
}

fun scrollToTop(event: ScrollTopEvent) {
    _scrollTopEvent.tryEmit(value = event)
}

@Composable
fun ScrollState.ScrollToTop(event: ScrollTopEvent) {
    LaunchedEffect(key1 = Unit) {
        scrollTopEvent.collect { currentEvent ->
            if (event == currentEvent) {
                scrollTo(value = 0)
            }
        }
    }
}

@Composable
fun LazyListState.ScrollToTop(event: ScrollTopEvent) {
    LaunchedEffect(key1 = Unit) {
        scrollTopEvent.collect { currentEvent ->
            if (event == currentEvent) {
                scrollToItem(index = 0)
            }
        }
    }
}

@Composable
fun LazyGridState.ScrollToTop(event: ScrollTopEvent) {
    LaunchedEffect(key1 = Unit) {
        scrollTopEvent.collect { currentEvent ->
            if (event == currentEvent) {
                scrollToItem(index = 0)
            }
        }
    }
}