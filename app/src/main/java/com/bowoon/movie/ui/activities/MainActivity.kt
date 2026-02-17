package com.bowoon.movie.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation3.runtime.NavKey
import com.bowoon.common.AppDoubleBackToExit
import com.bowoon.common.Log
import com.bowoon.common.isSystemInDarkTheme
import com.bowoon.data.util.NetworkMonitor
import com.bowoon.firebase.LocalFirebaseLogHelper
import com.bowoon.movie.MovieAppState
import com.bowoon.movie.MovieFirebase
import com.bowoon.movie.R
import com.bowoon.movie.deeplink.parseDeeplink
import com.bowoon.movie.navigation.TOP_LEVEL_NAV_ITEMS
import com.bowoon.movie.rememberMovieAppState
import com.bowoon.movie.ui.MovieApp
import com.bowoon.movie.ui.NextWeekReleaseMoviesNavKey
import com.bowoon.movie.utils.isSystemInDarkTheme
import com.bowoon.my.SettingScreen
import com.bowoon.my.SettingVM
import com.bowoon.my.SettingsAction
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
    private val settingVM: SettingVM by viewModels()
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
    private var deeplinkBackstack by mutableStateOf<List<NavKey>>(value = emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        intent?.let {
            deeplinkBackstack = parseDeeplink(uri = intent.data)
        }

        onBackPressedDispatcher.addCallback(
            onBackPressedCallback = object : OnBackPressedCallback(enabled = true) {
                override fun handleOnBackPressed() {
                    appDoubleBackToExit.onBackPressed(callback = { finish() })
                }
            }
        )

        movieFirebase.sendLog(javaClass.simpleName, "create MainActivity")

        var darkTheme by mutableStateOf(value = resources.configuration.isSystemInDarkTheme)

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

        // true -> 스플래쉬 화면 노출, false -> 스플래쉬 화면 미노출
        splashScreen.setKeepOnScreenCondition { viewModel.movieAppData.value.shouldKeepSplashScreen() }

        setContent {
            CompositionLocalProvider(value = LocalFirebaseLogHelper provides movieFirebase) {
                LocalFirebaseLogHelper.current.sendLog(name = javaClass.simpleName, message = "compose start!")

                val nextWeekReleaseMedia by viewModel.nextWeekReleaseMedia.collectAsStateWithLifecycle(initialValue = Pair(first = emptyList(), second = false))

                MovieTheme(darkTheme = darkTheme) {
                    val appState = rememberMovieAppState(networkMonitor = networkMonitor)
                    val snackbarHostState = remember { SnackbarHostState() }

                    LaunchedEffect(key1 = deeplinkBackstack) {
                        if (deeplinkBackstack.isNotEmpty()) {
                            navigationSetting(appState = appState)
                        }
                    }

                    LaunchedEffect(key1 = nextWeekReleaseMedia) {
                        if (nextWeekReleaseMedia.second) {
                            appState.navigationState.backStacks[appState.navigationState.startRoute]?.add(element = NextWeekReleaseMoviesNavKey)
                        }
                    }

                    MovieApp(
                        appState = appState,
                        snackbarHostState = snackbarHostState,
                        nextWeekReleaseMovies = nextWeekReleaseMedia.first,
                        updateShowNextReleaseMoviesDate = viewModel::updateShowNextReleaseMoviesDate,
                        showSettingDialog = { settingVM.onAction(action = SettingsAction.OpenMain) }
                    )

                    SettingScreen(viewModel = settingVM)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent = intent)
        Log.d("onNewIntent")
        setIntent(intent)
        deeplinkBackstack = parseDeeplink(uri = intent.data)
    }

    fun navigationSetting(appState: MovieAppState) {
        // 딥링크로 진입시 백스택 초기화
        appState.navigationState.backStacks.entries.forEach { (_, value) ->
            while (value.size > 1) {
                value.removeAt(index = value.lastIndex)
            }
        }

        appState.navigationState.topLevelRoute = appState.navigationState.startRoute

        deeplinkBackstack.forEach { navKey ->
            val targetTabKey = TOP_LEVEL_NAV_ITEMS.keys.firstOrNull { it.javaClass == navKey.javaClass }?.let { topLevelNavKey ->
                appState.navigationState.topLevelRoute = topLevelNavKey
                topLevelNavKey
            } ?: appState.navigationState.startRoute

            if (appState.navigationState.backStacks[targetTabKey] != null) {
                appState.navigationState.backStacks[targetTabKey]?.add(element = navKey)
            }
        }

        deeplinkBackstack = emptyList()
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