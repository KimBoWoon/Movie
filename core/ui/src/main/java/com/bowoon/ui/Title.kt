package com.bowoon.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bowoon.ui.theme.MovieTheme

@Composable
fun Title(
    title: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(dp53)
            .bottomLineBorder(strokeWidth = (0.5).dp, color = Color.LightGray),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.padding(horizontal = dp20),
            text = title,
            fontSize = sp20,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun Title(
    title: String,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(dp53)
            .bottomLineBorder(strokeWidth = (0.5).dp, color = Color.LightGray),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilledIconButton(
            modifier = Modifier.padding(dp5),
            onClick = { onBackClick() },
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                disabledContainerColor = Color.Transparent,
                disabledContentColor = Color.Transparent
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "onBackClick"
            )
        }
        Text(
            modifier = Modifier.weight(1f),
            text = title,
            fontSize = sp20,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun Title(
    title: String,
    isFavorite: Boolean,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(dp53)
            .bottomLineBorder(strokeWidth = (0.5).dp, color = Color.LightGray),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilledIconButton(
            modifier = Modifier.padding(dp5),
            onClick = { onBackClick() },
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                disabledContainerColor = if (isFavorite) {
                    MaterialTheme.colorScheme.onBackground.copy(
                        alpha = 0.12f
                    )
                } else {
                    Color.Transparent
                },
                disabledContentColor = Color.Transparent
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "onBackClick"
            )
        }
        Text(
            modifier = Modifier.weight(1f),
            text = title,
            fontSize = sp20,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        FavoriteButton(
            modifier = Modifier
                .padding(end = dp5)
                .wrapContentSize(),
            isFavorite = isFavorite,
            onClick = { onFavoriteClick() }
        )
    }
}

@Preview
@Composable
fun TitlePreview() {
    MovieTheme {
        Title(
            title = "Hello, World",
            isFavorite = true,
            onBackClick = {  },
            onFavoriteClick = {  }
        )
    }
}