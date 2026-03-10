package com.cheeke.surfy.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.util.trace
import com.cheeke.surfy.core.ui.R
import com.cheeke.surfy.ui.utils.dp5

@Composable
fun FavoriteButtonComponent(
    modifier: Modifier = Modifier,
    isFavorite: Boolean,
    onClick: () -> Unit
) {
    if (isFavorite) {
        Icon(
            modifier = modifier.padding(all = dp5).clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { trace("UnFavoriteClicked") { onClick() } },
            painter = painterResource(id = R.drawable.ic_like_on),
            contentDescription = "favorite"
        )
    } else {
        Icon(
            modifier = modifier.padding(all = dp5).clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { trace("FavoriteClicked") { onClick() } },
            painter = painterResource(id = R.drawable.ic_like_off),
            contentDescription = "unFavorite"
        )
    }
}