package com.bowoon.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign

@Composable
fun BoxOfficeRank(
    modifier: Modifier = Modifier,
    rank: Int
) {
    Text(
        modifier = modifier,
        text = "$rank",
        color = Color.White,
        fontSize = sp10,
//        style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
        textAlign = TextAlign.Center
    )
}