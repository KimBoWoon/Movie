package com.bowoon.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun RowScope.BottomNavigationBarItem(
    selected: Boolean,
    label: String,
    selectedIcon: ImageVector,
    unSelectedIcon: ImageVector,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            modifier = Modifier
                .size(dp24)
                .padding(top = dp5),
            imageVector = when (selected) {
                true -> selectedIcon
                false -> unSelectedIcon
            },
            contentDescription = label
        )
        Text(
            modifier = Modifier.padding(vertical = dp5),
            text = label,
            fontSize = sp12,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            ),
            maxLines = 1
        )
    }
}

@Composable
fun ColumnScope.BottomNavigationRailItem(
    selected: Boolean,
    label: String,
    selectedIcon: ImageVector,
    unSelectedIcon: ImageVector,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier
                .size(dp24)
                .padding(top = dp5),
            imageVector = when (selected) {
                true -> selectedIcon
                false -> unSelectedIcon
            },
            contentDescription = label
        )
        Text(
            modifier = Modifier.padding(vertical = dp5),
            text = label,
            fontSize = sp12,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            ),
            maxLines = 1
        )
    }
}

object MovieNavigationDefaults {
    @Composable
    fun navigationContainerColor() = MaterialTheme.colorScheme.background

    @Composable
    fun navigationContentColor() = MaterialTheme.colorScheme.onSurfaceVariant

    @Composable
    fun navigationSelectedItemColor() = MaterialTheme.colorScheme.onPrimaryContainer

    @Composable
    fun navigationIndicatorColor() = MaterialTheme.colorScheme.primaryContainer
}
