package com.cheeke.surfy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.cheeke.surfy.core.ui.R
import com.cheeke.surfy.ui.utils.dp16
import com.cheeke.surfy.ui.utils.dp30

@Composable
fun OverviewComponent(overview: String) {
    var expanded by remember { mutableStateOf(value = false) }
    var showMore by remember { mutableStateOf(value = false) }

    Box(
        modifier = Modifier.fillMaxWidth().padding(horizontal = dp16)
    ) {
        Text(
            text = overview,
            maxLines = if (expanded) Int.MAX_VALUE else 4,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium,
            onTextLayout = {
                if (!expanded) {
                    showMore = it.hasVisualOverflow
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        if (!expanded && showMore) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
                                MaterialTheme.colorScheme.background.copy(alpha = 0.9f),
                                MaterialTheme.colorScheme.background.copy(alpha = 0.98f),
                                MaterialTheme.colorScheme.background
                            )
                        )
                    ).padding(start = dp30),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.overview_show_more),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.clickable { expanded = true }
                )
            }
        }
    }
}