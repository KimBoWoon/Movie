package com.bowoon.my

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun MyScreen(
    viewModel: MyVM = hiltViewModel()
) {
    MyScreen()
}

@Composable
fun MyScreen(

) {
    Text(text = "my screen")
}