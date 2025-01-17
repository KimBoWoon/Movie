package com.bowoon.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign

@Composable
fun BoxOfficeRank(
    modifier: Modifier = Modifier,
    rank: Int
) {
    Box(
        modifier = modifier
    ) {
        Text(
            modifier = Modifier.wrapContentSize().align(Alignment.Center),
            text = "$rank",
            color = Color.White,
            fontSize = sp15,
            textAlign = TextAlign.Center
        )
    }
}