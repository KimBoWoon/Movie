package com.bowoon.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.bowoon.ui.theme.MovieTheme

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
            .height(dp53),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.padding(start = dp16).clickable { onBackClick() },
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "onBackClick"
        )
        Text(
            modifier = Modifier.weight(1f),
            text = title,
            fontSize = sp20,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        FavoriteButton(
            modifier = Modifier
                .padding(top = dp5, end = dp5)
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