package com.bowoon.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.bowoon.common.Log
import com.bowoon.ui.utils.dp0

@Composable
fun TabComponent(
    tabs: List<String>,
    pagerState: PagerState,
    tabClickEvent: ((Int, Int) -> Unit)? = null,
    content: @Composable (List<String>) -> Unit
) {
    if (tabs.size < 5) {
        TabRow(
            modifier = Modifier.semantics { contentDescription = "detailTabRow" }.fillMaxWidth(),
            selectedTabIndex = pagerState.currentPage
        ) {
            tabs.forEachIndexed { index, label ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        Log.d("selected tab index > $index")
                        tabClickEvent?.invoke(pagerState.currentPage, index)
                    },
                    text = { Text(text = label) },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.inverseSurface
                )
            }
        }
    } else {
        ScrollableTabRow(
            modifier = Modifier.semantics { contentDescription = "detailTabRow" }.fillMaxWidth(),
            selectedTabIndex = pagerState.currentPage,
            edgePadding = dp0
        ) {
            tabs.forEachIndexed { index, label ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        Log.d("selected tab index > $index")
                        tabClickEvent?.invoke(pagerState.currentPage, index)
                    },
                    text = { Text(text = label) },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.inverseSurface
                )
            }
        }
    }

    content(tabs)
}