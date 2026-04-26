package com.cheeke.surfy

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.cheeke.surfy.data.util.NetworkMonitor
import com.cheeke.surfy.navigation.TopLevelDestination
import com.cheeke.surfy.ui.RootTab
import com.slack.circuit.backstack.SaveableBackStack
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.rememberCircuitNavigator
import com.slack.circuit.runtime.Navigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@Composable
fun rememberSurfyAppState(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    networkMonitor: NetworkMonitor
): SurfyAppState {
    val multipleBackStack: Map<RootTab, SaveableBackStack> = buildMap {
        TopLevelDestination.entries.forEach { topLevelDestination ->
            put(key = topLevelDestination.rootTab, value = rememberSaveableBackStack(root = topLevelDestination.screen))
        }
    }
    val currentBackStack: SaveableBackStack = multipleBackStack[TopLevelDestination.HOME.rootTab]!!
    val navigator = rememberCircuitNavigator(currentBackStack)

    return remember(key1 = coroutineScope, key2 = networkMonitor, key3 = navigator) {
        SurfyAppState(
            navigator = navigator,
            multipleBackStack = multipleBackStack,
            coroutineScope = coroutineScope,
            networkMonitor = networkMonitor
        )
    }
}

@Stable
class SurfyAppState(
    val navigator: Navigator,
    val multipleBackStack: Map<RootTab, SaveableBackStack>,
    val coroutineScope: CoroutineScope,
    val networkMonitor: NetworkMonitor
) {
    val isOffline = networkMonitor.isOnline
        .map(transform = Boolean::not)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = false
        )

    fun changeRootTab(rootTab: RootTab) {

    }
}