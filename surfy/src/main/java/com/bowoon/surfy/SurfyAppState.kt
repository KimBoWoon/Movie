package com.bowoon.surfy

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.bowoon.data.util.NetworkMonitor
import com.bowoon.home.navigation.HomeNavKey
import com.bowoon.surfy.navigation.TOP_LEVEL_NAV_ITEMS
import com.bowoon.navigation.NavigationState
import com.bowoon.navigation.rememberNavigationState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@Composable
fun rememberSurfyAppState(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    networkMonitor: NetworkMonitor
): SurfyAppState {
    val navigationState = rememberNavigationState(startRoute = HomeNavKey, topLevelRoutes = TOP_LEVEL_NAV_ITEMS.keys)

    return remember(key1 = coroutineScope, key2 = networkMonitor, key3 = navigationState) {
        SurfyAppState(
            navigationState = navigationState,
            coroutineScope = coroutineScope,
            networkMonitor = networkMonitor
        )
    }
}

@Stable
class SurfyAppState(
    val navigationState: NavigationState,
    val coroutineScope: CoroutineScope,
    val networkMonitor: NetworkMonitor
) {
    val isOffline = networkMonitor.isOnline
        .map(transform = Boolean::not)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )
}