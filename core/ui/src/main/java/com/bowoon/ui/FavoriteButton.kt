package com.bowoon.ui

import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource

@Composable
fun FavoriteButton(
    modifier: Modifier = Modifier,
    isFavorite: Boolean,
    onClick: () -> Unit
) {
    FilledIconToggleButton(
        checked = isFavorite,
        onCheckedChange = { onClick() },
        modifier = modifier,
        enabled = true,
        colors = IconButtonDefaults.iconToggleButtonColors(
            checkedContainerColor = Color.Transparent,
            checkedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            disabledContainerColor = if (isFavorite) {
                MaterialTheme.colorScheme.onBackground.copy(
                    alpha = 0.12f
                )
            } else {
                Color.Transparent
            },
        )
    ) {
        if (isFavorite) {
            Icon(
                painter = painterResource(com.bowoon.ui.R.drawable.ic_like_on),
                contentDescription = "favorite",
            )
        } else {
            Icon(
                painter = painterResource(com.bowoon.ui.R.drawable.ic_like_off),
                contentDescription = "unFavorite",
            )
        }
    }
}