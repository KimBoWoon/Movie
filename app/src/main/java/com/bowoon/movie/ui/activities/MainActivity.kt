package com.bowoon.movie.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.bowoon.data.util.NetworkMonitor
import com.bowoon.movie.rememberMovieAppState
import com.bowoon.movie.ui.MovieMainScreen
import com.bowoon.ui.theme.MovieTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainVM by viewModels()
    @Inject
    lateinit var networkMonitor: NetworkMonitor

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        splashScreen.setKeepOnScreenCondition { !viewModel.initDataLoad.value.shouldKeepSplashScreen() }

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