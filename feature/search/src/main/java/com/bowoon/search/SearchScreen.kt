package com.bowoon.search

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SearchScreen(
    onMovieClick: (Int) -> Unit,
    viewModel: SearchVM = hiltViewModel()
) {
    SearchScreen(
        onMovieClick = onMovieClick
    )
}

@Composable
fun SearchScreen(
    onMovieClick: (Int) -> Unit
) {
    Text(text = "search screen")
}