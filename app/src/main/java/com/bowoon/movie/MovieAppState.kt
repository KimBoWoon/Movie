package com.bowoon.movie

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.bowoon.data.util.NetworkMonitor
import com.bowoon.movie.navigation.TopLevelDestination
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.screen.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@Composable
fun rememberMovieAppState(
    navigator: Navigator,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    networkMonitor: NetworkMonitor
): MovieAppState = remember(navigator, coroutineScope, networkMonitor) {
    MovieAppState(
        navigator = navigator,
        coroutineScope = coroutineScope,
        networkMonitor = networkMonitor
    )
}

@Stable
class MovieAppState(
    val navigator: Navigator,
    val coroutineScope: CoroutineScope,
    val networkMonitor: NetworkMonitor
) {
    val currentDestination: Screen?
        @Composable get() = navigator.peek()
    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries
    val isOffline = networkMonitor.isOnline
        .map(Boolean::not)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        navigator.goTo(screen = topLevelDestination.screen)
    }
}