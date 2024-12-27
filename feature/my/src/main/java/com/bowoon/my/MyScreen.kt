package com.bowoon.my

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bowoon.common.Log

@Composable
fun MyScreen(
    viewModel: MyVM = hiltViewModel()
) {
    val myState by viewModel.configuration.collectAsStateWithLifecycle()

    MyScreen(
        state = myState
    )
}

@Composable
fun MyScreen(
    state: ConfigurationState
) {
    when (state) {
        is ConfigurationState.Loading -> Log.d("configuration loading...")
        is ConfigurationState.Success -> {
            Log.d("${state.configuration}")
            Text(text = "${state.configuration}")
        }
        is ConfigurationState.Error -> Log.e(state.throwable.message ?: "something wrong...")
    }
}