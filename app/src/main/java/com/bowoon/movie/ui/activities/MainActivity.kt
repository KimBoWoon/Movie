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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.bowoon.common.Log
import com.bowoon.data.util.NetworkMonitor
import com.bowoon.data.util.SyncManager
import com.bowoon.movie.rememberMovieAppState
import com.bowoon.movie.ui.MovieMainScreen
import com.bowoon.ui.ConfirmDialog
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

        splashScreen.setKeepOnScreenCondition { viewModel.initDataLoad.value !is InitDataState.Success }

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

                val initialize = viewModel.initDataLoad.collectAsStateWithLifecycle()
                val appState = rememberMovieAppState(networkMonitor = networkMonitor)
                val snackbarHostState = remember { SnackbarHostState() }

                when (initialize.value) {
                    is InitDataState.Load -> Log.d("my data initialize...")
                    is InitDataState.Success -> Log.d("my data initialize success")
                    is InitDataState.Error -> {
                        Log.d("my data initialize error")
                        ConfirmDialog(
                            title = "초기 데이터 셋팅 실패",
                            message = "데이터를 정상적으로 가져오지 못했기 때문에 앱을 사용할 수 없습니다.",
                            confirmPair = "재시도" to {
                                viewModel.initDataLoad.value = InitDataState.Load
                                syncManager.initialize()
                            },
                            dismissPair = "앱종료" to { finish() }
                        )
                    }
                }

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