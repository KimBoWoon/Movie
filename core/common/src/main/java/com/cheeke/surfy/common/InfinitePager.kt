package com.cheeke.surfy.common

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

fun <T> buildLoopList(items: List<T>): List<T> {
    if (items.isEmpty()) return emptyList()
    if (items.size == 1) return items

    return buildList {
        add(items.last())
        addAll(items)
        add(items.first())
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T : Any> InfinitePager(
    contentPadding: PaddingValues,
    pageSpacing: Dp,
    modifier: Modifier = Modifier,
    isVisibleIndicator: Boolean = true,
    isAutoScroll: Boolean = false,
    items: List<T>,
    content: @Composable (T) -> Unit
) {
    if (items.isEmpty()) return

    val pagerItems = remember(items) {
        buildLoopList(items)
    }
    val pagerState = rememberPagerState(
        initialPage = if (items.size > 1) 1 else 0,
        pageCount = { pagerItems.size }
    )
    val realFirstPage = 1
    val realLastPage = pagerItems.size - 2
    val fakeLastPage = pagerItems.size - 1

    /**
     * fake page -> real page 순간 이동
     */
    LaunchedEffect(pagerState.settledPage) {
        if (items.size <= 1) return@LaunchedEffect

        when (pagerState.settledPage) {
            0 -> pagerState.scrollToPage(realLastPage)
            fakeLastPage -> pagerState.scrollToPage(realFirstPage)
        }
    }

    /**
     * Auto Scroll
     */
    if (isAutoScroll && items.size > 1) {
        LaunchedEffect(key1 = pagerState) {
            snapshotFlow { pagerState.settledPage }
                .collectLatest {
                    delay(timeMillis = 2000)
                    pagerState.animateScrollToPage(
                        page = pagerState.settledPage + 1,
                        animationSpec = tween(
                            durationMillis = 500,
                            easing = FastOutSlowInEasing
                        )
                    )
                }
        }
    }

    /**
     * indicator용 실제 page index
     */
    val indicatorPage = when (pagerState.settledPage) {
        0 -> items.lastIndex
        fakeLastPage -> 0
        else -> if (items.size > 1) pagerState.settledPage - 1 else 0
    }

    Column {
        HorizontalPager(
            state = pagerState,
            contentPadding = contentPadding,
            pageSpacing = pageSpacing,
            modifier = modifier
        ) { page ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                content(pagerItems[page])
            }
        }

        if (isVisibleIndicator && items.size > 1) {
            Spacer(
                modifier = Modifier.height(14.dp)
            )

            PagerIndicator(
                pageCount = items.size,
                currentPage = indicatorPage,
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun PagerIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(times = pageCount) { index ->
            val isSelected = index == currentPage

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.28f)
                    )
                    .size(
                        width = if (isSelected) 18.dp else 6.dp,
                        height = 6.dp
                    )
            )
        }
    }
}