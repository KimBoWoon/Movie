package com.cheeke.surfy.common

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