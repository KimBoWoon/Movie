package com.cheeke.surfy.ui.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import com.cheeke.surfy.R
import com.cheeke.surfy.SurfyAppState
import com.cheeke.surfy.SurfyFirebase
import com.cheeke.surfy.analytics.AnalyticsHelper
import com.cheeke.surfy.analytics.LocalAnalyticsHelper
import com.cheeke.surfy.common.AppDoubleBackToExit
import com.cheeke.surfy.common.Log
import com.cheeke.surfy.common.isSystemInDarkTheme
import com.cheeke.surfy.data.util.NetworkMonitor
import com.cheeke.surfy.deeplink.parseDeeplink
import com.cheeke.surfy.firebase.LocalFirebaseLogHelper
import com.cheeke.surfy.navigation.TOP_LEVEL_NAV_ITEMS
import com.cheeke.surfy.rememberSurfyAppState
import com.cheeke.surfy.setting.SettingScreen
import com.cheeke.surfy.setting.SettingVM
import com.cheeke.surfy.setting.SettingsAction
import com.cheeke.surfy.ui.NextWeekReleaseMoviesNavKey
import com.cheeke.surfy.ui.SurfyApp
import com.cheeke.surfy.ui.theme.SurfyTheme
import com.cheeke.surfy.utils.isSystemInDarkTheme
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
    lateinit var surfyFirebase: SurfyFirebase
    @Inject
    lateinit var analyticsHelper: AnalyticsHelper
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

        surfyFirebase.sendLog(javaClass.simpleName, "create MainActivity")

        var darkTheme by mutableStateOf(value = resources.configuration.isSystemInDarkTheme)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                combine(
                    isSystemInDarkTheme(),
                    viewModel.surfyAppData
                ) { systemDarkTheme, userdata ->
                    userdata.shouldUseDarkTheme(isSystemDarkTheme = systemDarkTheme)
                }.onEach { darkTheme = it }
                    .distinctUntilChanged()
                    .collect { darkTheme ->
                        enableEdgeToEdge(
                            statusBarStyle = SystemBarStyle.auto(
                                lightScrim = Color.TRANSPARENT,
                                darkScrim = Color.TRANSPARENT,
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
        splashScreen.setKeepOnScreenCondition { viewModel.surfyAppData.value.shouldKeepSplashScreen() }

        setContent {
            CompositionLocalProvider(
                LocalFirebaseLogHelper provides surfyFirebase,
                LocalAnalyticsHelper provides analyticsHelper
            ) {
                LocalFirebaseLogHelper.current.sendLog(name = javaClass.simpleName, message = "compose start!")

                val shouldShowNextWeekReleaseDialog by viewModel.shouldShowNextWeekReleaseDialog.collectAsStateWithLifecycle()
                val nextWeekReleaseDialogItems by viewModel.nextWeekReleaseDialogItems.collectAsStateWithLifecycle()

                SurfyTheme(darkTheme = darkTheme) {
                    val appState = rememberSurfyAppState(networkMonitor = networkMonitor)
                    val snackbarHostState = remember { SnackbarHostState() }

                    LaunchedEffect(key1 = deeplinkBackstack) {
                        if (deeplinkBackstack.isNotEmpty()) {
                            navigationSetting(appState = appState)
                        }
                    }

                    LaunchedEffect(key1 = shouldShowNextWeekReleaseDialog) {
                        if (shouldShowNextWeekReleaseDialog) {
                            appState.navigationState.backStacks[appState.navigationState.startRoute]?.add(element = NextWeekReleaseMoviesNavKey)
                        }
                    }

                    SurfyApp(
                        appState = appState,
                        snackbarHostState = snackbarHostState,
                        nextWeekReleaseMovies = nextWeekReleaseDialogItems,
                        dismissNextWeekReleaseDialog = viewModel::dismissNextWeekReleaseDialog,
                        dontShowNextWeekReleaseDialogToday = viewModel::dontShowNextWeekReleaseDialogToday,
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

    fun navigationSetting(appState: SurfyAppState) {
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
private val lightScrim = Color.argb(0xe6, 0xFF, 0xFF, 0xFF)

/**
 * 안드로이드에서 기본적으로 제공되는 다크 모드
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=40-44;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val darkScrim = Color.argb(0x80, 0x1b, 0x1b, 0x1b)
