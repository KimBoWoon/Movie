package com.bowoon.search

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SearchScreen(
    viewModel: SearchVM = hiltViewModel()
) {
    SearchScreen()
}

@Composable
fun SearchScreen(
    onMovieClick: (String, String, String) -> Unit
) {
    Text(text = "search screen")
}