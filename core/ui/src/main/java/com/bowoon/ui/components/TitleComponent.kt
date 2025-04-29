package com.bowoon.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bowoon.ui.FavoriteButton
import com.bowoon.ui.utils.bottomLineBorder
import com.bowoon.ui.utils.dp16
import com.bowoon.ui.utils.dp24
import com.bowoon.ui.utils.dp5
import com.bowoon.ui.utils.dp53
import com.bowoon.ui.utils.sp20

@Composable
fun TitleComponent(
    title: String,
    isFavorite: Boolean? = null,
    goToBack: (() -> Unit)? = null,
    onFavoriteClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(dp53)
            .bottomLineBorder(strokeWidth = (0.5).dp, color = Color.LightGray),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        goToBack?.let { goToBack ->
            FilledIconButton(
                modifier = Modifier.testTag(tag = "backButton").padding(dp5),
                onClick = { goToBack() },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = Color.Transparent
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "goToBackClick"
                )
            }
        } ?: Spacer(modifier = Modifier.size(dp24).padding(start = dp16).background(color = Color.Transparent))

        Text(
            modifier = Modifier.testTag(tag = "titleComponent").weight(1f),
            text = title,
            fontSize = sp20,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        onFavoriteClick?.let { onFavorite ->
            FavoriteButton(
                modifier = Modifier
                    .padding(end = dp16)
                    .wrapContentSize(),
                isFavorite = isFavorite ?: false,
                onClick = { onFavorite() }
            )
        } ?: Spacer(modifier = Modifier.padding(end = dp16).size(dp24).background(color = Color.Transparent))
    }
}