package com.cheeke.surfy.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.util.trace
import com.cheeke.surfy.core.ui.R

@Composable
fun FavoriteButtonComponent(
    modifier: Modifier = Modifier,
    isFavorite: Boolean,
    onClick: () -> Unit
) {
    if (isFavorite) {
        Icon(
            modifier = modifier.clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { trace(sectionName = "UnFavoriteClicked") { onClick() } },
            painter = painterResource(id = R.drawable.ic_like_on),
            contentDescription = "favorite",
            tint = MaterialTheme.colorScheme.onSurface
        )
    } else {
        Icon(
            modifier = modifier.clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { trace(sectionName = "FavoriteClicked") { onClick() } },
            painter = painterResource(id = R.drawable.ic_like_off),
            contentDescription = "unFavorite",
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}