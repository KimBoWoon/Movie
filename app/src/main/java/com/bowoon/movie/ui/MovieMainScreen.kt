package com.bowoon.movie.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration.Indefinite
import androidx.compose.material3.SnackbarDuration.Short
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult.ActionPerformed
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import com.bowoon.movie.MovieAppState
import com.bowoon.movie.R
import com.bowoon.movie.navigation.MovieAppNavHost
import com.bowoon.ui.BottomNavigationBarItem
import com.bowoon.ui.BottomNavigationRailItem
import com.bowoon.ui.MovieNavigationDefaults
import com.bowoon.ui.dp1
import com.bowoon.ui.dp50
import com.bowoon.ui.topLineBorder
import kotlin.reflect.KClass

@Composable
fun MovieMainScreen(
    appState: MovieAppState,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = { Navigation(appState = appState) }
    ) { innerPadding ->
        val isOffline by appState.isOffline.collectAsStateWithLifecycle()
        val notConnectedMessage = stringResource(R.string.not_connected)

        LaunchedEffect(isOffline) {
            if (isOffline) {
                snackbarHostState.showSnackbar(
                    message = notConnectedMessage,
                    duration = Indefinite,
                )
            }
        }

        MovieAppNavHost(
            modifier = Modifier.padding(innerPadding),
            appState = appState,
            onShowSnackbar = { message, action ->
                snackbarHostState.showSnackbar(
                    message = message,
                    actionLabel = action,
                    duration = Short,
                ) == ActionPerformed
            }
        )
    }
}

@Composable
fun Navigation(
    appState: MovieAppState
) {
    val windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo()
    val layoutType = NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(windowAdaptiveInfo)
    val context = LocalContext.current
    val currentDestination = appState.currentDestination

    when (layoutType) {
        NavigationSuiteType.NavigationBar -> {
            NavigationBar(
                modifier = Modifier.fillMaxWidth().height(dp50).topLineBorder(strokeWidth = dp1, color = MovieNavigationDefaults.navigationBorderColor()),
                containerColor = MovieNavigationDefaults.navigationContainerColor(),
                contentColor = MovieNavigationDefaults.navigationContentColor()
            ) {
                appState.topLevelDestinations.forEach { destination ->
                    val selected = currentDestination.isRouteInHierarchy(destination.baseRoute)
                    BottomNavigationBarItem(
                        selected = selected,
                        label = context.getString(destination.titleTextId),
                        selectedIcon = destination.selectedIcon,
                        unSelectedIcon = destination.unselectedIcon,
                        onClick = { appState.navigateToTopLevelDestination(destination) }
                    )
                }
            }
        }
        NavigationSuiteType.NavigationRail -> {
            NavigationRail(
                modifier = Modifier.width(dp50).fillMaxHeight().border(width = dp1, color = MovieNavigationDefaults.navigationBorderColor()),
                containerColor = MovieNavigationDefaults.navigationContainerColor(),
                contentColor = MovieNavigationDefaults.navigationContentColor()
            ) {
                appState.topLevelDestinations.forEach { destination ->
                    val selected = currentDestination.isRouteInHierarchy(destination.baseRoute)
                    BottomNavigationRailItem(
                        selected = selected,
                        label = context.getString(destination.titleTextId),
                        selectedIcon = destination.selectedIcon,
                        unSelectedIcon = destination.unselectedIcon,
                        onClick = { appState.navigateToTopLevelDestination(destination) }
                    )
                }
            }
        }
        NavigationSuiteType.NavigationDrawer -> {
            NavigationRail(
                modifier = Modifier.width(dp50).fillMaxHeight().border(width = dp1, color = MovieNavigationDefaults.navigationBorderColor()),
                containerColor = MovieNavigationDefaults.navigationContainerColor(),
                contentColor = MovieNavigationDefaults.navigationContentColor()
            ) {
                appState.topLevelDestinations.forEach { destination ->
                    val selected = currentDestination.isRouteInHierarchy(destination.baseRoute)
                    BottomNavigationRailItem(
                        selected = selected,
                        label = context.getString(destination.titleTextId),
                        selectedIcon = destination.selectedIcon,
                        unSelectedIcon = destination.unselectedIcon,
                        onClick = { appState.navigateToTopLevelDestination(destination) }
                    )
                }
            }
        }
        else -> {
            NavigationBar(
                modifier = Modifier.fillMaxWidth().height(dp50).topLineBorder(strokeWidth = dp1, color = MovieNavigationDefaults.navigationBorderColor()),
                containerColor = MovieNavigationDefaults.navigationContainerColor(),
                contentColor = MovieNavigationDefaults.navigationContentColor()
            ) {
                appState.topLevelDestinations.forEach { destination ->
                    val selected = currentDestination.isRouteInHierarchy(destination.baseRoute)
                    BottomNavigationBarItem(
                        selected = selected,
                        label = context.getString(destination.titleTextId),
                        selectedIcon = destination.selectedIcon,
                        unSelectedIcon = destination.unselectedIcon,
                        onClick = { appState.navigateToTopLevelDestination(destination) }
                    )
                }
            }
        }
    }
}

private fun NavDestination?.isRouteInHierarchy(route: KClass<*>) =
    this?.hierarchy?.any { it.hasRoute(route) } ?: false