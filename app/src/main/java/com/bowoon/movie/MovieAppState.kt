package com.bowoon.movie

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.bowoon.data.util.NetworkMonitor
import com.bowoon.movie.navigation.Screen
import com.bowoon.movie.navigation.TopLevelDestination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@Composable
fun rememberMovieAppState(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    backstack: NavBackStack<NavKey>,
    networkMonitor: NetworkMonitor
): MovieAppState = remember(key1 = coroutineScope, key2 = networkMonitor) {
    MovieAppState(
        coroutineScope = coroutineScope,
        networkMonitor = networkMonitor,
        backstack = backstack
    )
}

@Stable
class MovieAppState(
    val coroutineScope: CoroutineScope,
    val networkMonitor: NetworkMonitor,
    val backstack: NavBackStack<NavKey>
) {
    val start = Screen.Home
    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries
    val isOffline = networkMonitor.isOnline
        .map(transform = Boolean::not)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    fun navigateToTopLevelDestination(navKey: NavKey) {
        if (backstack.last() == navKey) {
            return
        }
        if (start == navKey) {
            backstack.clear()
            backstack.add(navKey)
            return
        }
        if (backstack.contains(element = navKey)) {
            backstack.removeAll { it == navKey }
        }
        backstack.add(navKey)
    }
}