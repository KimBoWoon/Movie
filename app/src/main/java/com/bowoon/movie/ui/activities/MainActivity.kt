package com.bowoon.movie.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bowoon.data.util.NetworkMonitor
import com.bowoon.movie.rememberMovieAppState
import com.bowoon.movie.ui.MovieMainScreen
import com.bowoon.ui.theme.MovieTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var networkMonitor: NetworkMonitor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MovieTheme {
                val appState = rememberMovieAppState(networkMonitor = networkMonitor)
                val snackbarHostState = remember { SnackbarHostState() }

                MovieMainScreen(
                    appState = appState,
                    snackbarHostState = snackbarHostState
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    com.bowoon.ui.theme.MovieTheme {
        Greeting("Android")
    }
}