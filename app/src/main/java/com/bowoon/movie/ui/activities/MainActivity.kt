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
import com.bowoon.common.Log
import com.bowoon.common.isSystemInDarkTheme
import com.bowoon.data.util.NetworkMonitor
import com.bowoon.data.util.SyncManager
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainVM by viewModels()
    @Inject
    lateinit var networkMonitor: NetworkMonitor
    @Inject
    lateinit var syncManager: SyncManager
    @Inject
    lateinit var movieFirebase: MovieFirebase
    @Inject
    lateinit var appDoubleBackToExitFactory: AppDoubleBackToExit.AppDoubleBackToExitFactory
    lateinit var appDoubleBackToExit: AppDoubleBackToExit

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        appDoubleBackToExit = appDoubleBackToExitFactory.create(
            this@MainActivity,
            getString(R.string.double_back_message)
        )

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                appDoubleBackToExit.onBackPressed({ finish() })
            }
        })

        movieFirebase.sendLog(javaClass.simpleName, "create MainActivity")

        var darkTheme by mutableStateOf(
            ThemeSettings(
                darkTheme = resources.configuration.isSystemInDarkTheme
            )
        )

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(
                    isSystemInDarkTheme(),
                    viewModel.myData
                ) { systemDarkTheme, userdata ->
                    ThemeSettings(
                        darkTheme = userdata.shouldUseDarkTheme(systemDarkTheme)
                    )
                }.onEach { darkTheme = it }
                    .map { it.darkTheme }
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

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.myData.collect {
                    when (it) {
                        is UserdataState.Loading -> Log.d("main activity > my data loading...")
                        is UserdataState.Success -> {
                            Log.d("main activity > my data success > ${it.data}")
                            syncManager.syncMain()
                        }
                        is UserdataState.Error -> Log.e("main activity > my data error > ${it.throwable.message}")
                    }
                }
            }
        }

        splashScreen.setKeepOnScreenCondition { viewModel.myData.value.shouldKeepSplashScreen() }

        setContent {
            CompositionLocalProvider(
                LocalFirebaseLogHelper provides movieFirebase
            ) {
                LocalFirebaseLogHelper.current.sendLog(javaClass.simpleName, "compose start!")

                MovieTheme(
                    darkTheme = darkTheme.darkTheme
                ) {
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
 * The default light scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=35-38;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val lightScrim = android.graphics.Color.argb(0xe6, 0xFF, 0xFF, 0xFF)

/**
 * The default dark scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=40-44;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val darkScrim = android.graphics.Color.argb(0x80, 0x1b, 0x1b, 0x1b)

data class ThemeSettings(
    val darkTheme: Boolean
)