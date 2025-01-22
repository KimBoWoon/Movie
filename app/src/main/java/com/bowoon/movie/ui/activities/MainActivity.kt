package com.bowoon.movie.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.bowoon.common.Log
import com.bowoon.data.util.NetworkMonitor
import com.bowoon.data.util.SyncManager
import com.bowoon.movie.rememberMovieAppState
import com.bowoon.movie.ui.MovieMainScreen
import com.bowoon.ui.theme.MovieTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainVM by viewModels()
    @Inject
    lateinit var networkMonitor: NetworkMonitor
    @Inject
    lateinit var syncManager: SyncManager

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        splashScreen.setKeepOnScreenCondition { viewModel.initDataLoad.value is InitDataState.Success }

        setContent {
            MovieTheme {
                LaunchedEffect("initDataLoad") {
                    lifecycleScope.launch {
                        syncManager.checkWork(
                            context = this@MainActivity,
                            onSuccess = {
                                Log.d("성공")
                                viewModel.initDataLoad.emit(InitDataState.Success)
                            },
                            onFailure = {
                                Log.d("실패")
                                viewModel.initDataLoad.emit(InitDataState.Error)
                            }
                        )
                    }
                }

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
    MovieTheme {
        Greeting("Android")
    }
}