package com.cheeke.surfy.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.cheeke.surfy.ui.utils.dp16
import com.cheeke.surfy.ui.utils.dp5

@Composable
fun OverviewComponent(overview: String) {
    var expanded by remember { mutableStateOf(value = false) }
    var showMore by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        SectionHeader(title = "Overview")

        Text(
            modifier = Modifier.padding(horizontal = dp16),
            text = overview,
            maxLines = if (expanded) Int.MAX_VALUE else 4,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium,
            onTextLayout = { textLayoutResult ->
                if (!expanded) {
                    showMore = textLayoutResult.hasVisualOverflow
                }
            }
        )

        if (showMore) {
            Text(
                modifier = Modifier
                    .padding(start = dp16, end = dp16, top = dp5)
                    .clickable { expanded = !expanded },
                text = if (expanded) "접기" else "더보기",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}