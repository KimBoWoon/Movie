package com.cheeke.surfy

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.cheeke.surfy.data.util.NetworkMonitor
import com.cheeke.surfy.home.navigation.HomeScreen
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
    val backStack = rememberSaveableBackStack(root = HomeScreen)
    val navigator = rememberCircuitNavigator(backStack)

    return remember(key1 = coroutineScope, key2 = networkMonitor, key3 = navigator) {
        SurfyAppState(
            navigator = navigator,
            backStack = backStack,
            coroutineScope = coroutineScope,
            networkMonitor = networkMonitor
        )
    }
}

@Stable
class SurfyAppState(
    val navigator: Navigator,
    val backStack: SaveableBackStack,
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
}