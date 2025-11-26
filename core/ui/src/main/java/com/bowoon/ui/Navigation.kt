package com.bowoon.ui

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
import androidx.compose.runtime.collection.mutableVectorOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import com.bowoon.ui.utils.bounceClick
import com.bowoon.ui.utils.dp24
import com.bowoon.ui.utils.dp5
import com.bowoon.ui.utils.sp12

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
            .semantics { contentDescription = label }
            .weight(1f)
            .bounceClick(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            modifier = Modifier
                .semantics {
                    contentDescription = if (selected) {
                        "selected_$label"
                    } else {
                        "unselected_$label"
                    }
                }
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
            .semantics {
                contentDescription = if (selected) {
                    "selected_$label"
                } else {
                    "unselected_$label"
                }
            }
            .weight(1f)
            .bounceClick(onClick = onClick),
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
    fun navigationBorderColor(): Color = Color.LightGray

    @Composable
    fun navigationContainerColor(): Color = MaterialTheme.colorScheme.background

    @Composable
    fun navigationContentColor(): Color = MaterialTheme.colorScheme.onSurfaceVariant

    @Composable
    fun navigationSelectedItemColor(): Color = MaterialTheme.colorScheme.onPrimaryContainer

    @Composable
    fun navigationIndicatorColor(): Color = MaterialTheme.colorScheme.primaryContainer
}

data class NavigationItem(
    val label: @Composable () -> Unit,
    val icon: @Composable () -> Unit,
    val onClick: () -> Unit,
    val selected: Boolean
)

class MovieNavigation {
    val navigationList = mutableVectorOf<NavigationItem>()

    fun item(
        label: @Composable () -> Unit,
        icon: @Composable () -> Unit,
        onClick: () -> Unit,
        selected: Boolean,
        modifier: Modifier = Modifier
    ) {
        navigationList.add(
            NavigationItem(
                label = label,
                icon = icon,
                onClick = onClick,
                selected = selected
            )
        )
    }
}