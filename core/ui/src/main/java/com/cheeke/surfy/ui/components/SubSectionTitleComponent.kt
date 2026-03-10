package com.cheeke.surfy.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.cheeke.surfy.ui.utils.dp16

@Composable
fun SubSectionTitleComponent(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(horizontal = dp16),
        style = MaterialTheme.typography.titleSmall,
        color = Color.Gray
    )
}