package com.bowoon.movie.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bowoon.common.AppDoubleBackToExit
import com.bowoon.common.isSystemInDarkTheme
import com.bowoon.data.util.NetworkMonitor
import com.bowoon.firebase.LocalFirebaseLogHelper
import com.bowoon.movie.MovieFirebase
import com.bowoon.movie.R
import com.bowoon.movie.rememberMovieAppState
import com.bowoon.movie.ui.MovieMainScreen
import com.bowoon.movie.utils.isSystemInDarkTheme
import com.bowoon.ui.theme.MovieTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainVM by viewModels()
    @Inject
    lateinit var networkMonitor: NetworkMonitor
    @Inject
    lateinit var movieFirebase: MovieFirebase
    @Inject
    lateinit var appDoubleBackToExitFactory: AppDoubleBackToExit.AppDoubleBackToExitFactory
    private val appDoubleBackToExit: AppDoubleBackToExit by lazy {
        appDoubleBackToExitFactory.create(
            context = this@MainActivity,
            exitText = getString(R.string.double_back_message)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(enabled = true) {
            override fun handleOnBackPressed() {
                appDoubleBackToExit.onBackPressed(callback = { finish() })
            }
        })

        movieFirebase.sendLog(javaClass.simpleName, "create MainActivity")

        var darkTheme by mutableStateOf(resources.configuration.isSystemInDarkTheme)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                combine(
                    isSystemInDarkTheme(),
                    viewModel.movieAppData
                ) { systemDarkTheme, userdata ->
                    userdata.shouldUseDarkTheme(isSystemDarkTheme = systemDarkTheme)
                }.onEach { darkTheme = it }
                    .distinctUntilChanged()
                    .collect { darkTheme ->
                        enableEdgeToEdge(
                            statusBarStyle = SystemBarStyle.auto(
                                lightScrim = android.graphics.Color.TRANSPARENT,
                                darkScrim = android.graphics.Color.TRANSPARENT,
                            ) { darkTheme },
                            navigationBarStyle = SystemBarStyle.auto(
                                lightScrim = lightScrim,
                                darkScrim = darkScrim,
                            ) { darkTheme },
                        )
                    }
            }
        }

        splashScreen.setKeepOnScreenCondition { viewModel.movieAppData.value.shouldKeepSplashScreen() }

        setContent {
            CompositionLocalProvider(value = LocalFirebaseLogHelper provides movieFirebase) {
                LocalFirebaseLogHelper.current.sendLog(name = javaClass.simpleName, message = "compose start!")

                MovieTheme(darkTheme = darkTheme) {
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
}

/**
 * 안드로이드에서 기본적으로 제공되는 라이트 모드
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=35-38;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val lightScrim = android.graphics.Color.argb(0xe6, 0xFF, 0xFF, 0xFF)

/**
 * 안드로이드에서 기본적으로 제공되는 다크 모드
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=40-44;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val darkScrim = android.graphics.Color.argb(0x80, 0x1b, 0x1b, 0x1b)