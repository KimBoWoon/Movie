package com.bowoon.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.bowoon.movie.core.ui.R
import com.bowoon.ui.utils.dp5

@Composable
fun FavoriteButton(
    modifier: Modifier = Modifier,
    isFavorite: Boolean,
    onClick: () -> Unit
) {
    if (isFavorite) {
        Icon(
            modifier = modifier.padding(all = dp5).clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { onClick() },
            painter = painterResource(id = R.drawable.ic_like_on),
            contentDescription = "favorite"
        )
    } else {
        Icon(
            modifier = modifier.padding(all = dp5).clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { onClick() },
            painter = painterResource(id = R.drawable.ic_like_off),
            contentDescription = "unFavorite"
        )
    }

//    FilledIconToggleButton(
//        modifier = modifier,
//        checked = isFavorite,
//        onCheckedChange = { onClick() },
//        enabled = true,
//        colors = IconButtonDefaults.iconToggleButtonColors(
//            checkedContainerColor = Color.Transparent,
//            checkedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
//            disabledContainerColor = if (isFavorite) {
//                MaterialTheme.colorScheme.onBackground.copy(
//                    alpha = 0.12f
//                )
//            } else {
//                Color.Transparent
//            },
//        )
//    ) {
//        if (isFavorite) {
//            Icon(
//                painter = painterResource(R.drawable.ic_like_on),
//                contentDescription = "favorite",
//            )
//        } else {
//            Icon(
//                painter = painterResource(R.drawable.ic_like_off),
//                contentDescription = "unFavorite",
//            )
//        }
//    }
}