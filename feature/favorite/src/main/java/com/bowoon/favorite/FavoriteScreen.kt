package com.bowoon.favorite

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun FavoriteScreen(
    onMovieClick: (String, String, String) -> Unit,
    viewModel: FavoriteVM = hiltViewModel()
) {
    FavoriteScreen(
        a = "",
        onMovieClick = onMovieClick
    )
}

@Composable
fun FavoriteScreen(
    a: String,
    onMovieClick: (String, String, String) -> Unit
) {
    Text(text = "favorite screen")
}