package com.bowoon.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun FilterChipComponent(
    title: String,
    selectedFilter: Boolean,
    updateFilter: () -> Unit
) {
    FilterChip(
        onClick = { updateFilter() },
        label = { Text(text = title) },
        selected = selectedFilter,
        leadingIcon = if (selectedFilter) {
            {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Done icon",
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else {
            null
        },
    )
}