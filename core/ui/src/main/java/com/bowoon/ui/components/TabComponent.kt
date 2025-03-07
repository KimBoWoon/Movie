package com.bowoon.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.bowoon.common.Log

@Composable
fun TabComponent(
    isDarkMode: Boolean,
    tabs: List<String>,
    pagerState: PagerState,
    tabClickEvent: ((Int, Int) -> Unit)? = null,
    content: @Composable (List<String>) -> Unit
) {
    val selectedContentColor = when (isDarkMode) {
        true -> Color(0xFF7C86DF)
        false -> Color.Black
    }
    val unSelectedContentColor = when (isDarkMode) {
        true -> Color.LightGray
        false -> Color.Gray
    }

    TabRow(
        modifier = Modifier.fillMaxWidth(),
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
                selectedContentColor = selectedContentColor,
                unselectedContentColor = unSelectedContentColor
            )
        }
    }

    content(tabs)
}