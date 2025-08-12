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
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItemColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import com.bowoon.firebase.LocalFirebaseLogHelper
import com.bowoon.movie.MovieAppState
import com.bowoon.movie.R
import com.bowoon.movie.navigation.MovieAppNavHost
import com.bowoon.ui.BottomNavigationBarItem
import com.bowoon.ui.BottomNavigationRailItem
import com.bowoon.ui.MovieNavigationDefaults
import com.bowoon.ui.utils.dp1
import com.bowoon.ui.utils.dp50
import com.bowoon.ui.utils.topLineBorder
import kotlin.reflect.KClass

@Composable
fun MovieMainScreen(
    appState: MovieAppState,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(modifier = Modifier.semantics { contentDescription = "snackbar" }, hostState = snackbarHostState) },
        bottomBar = { Navigation(appState = appState) }
    ) { innerPadding ->
        val isOffline by appState.isOffline.collectAsStateWithLifecycle()
        val notConnectedMessage = stringResource(id = R.string.not_connected)
        val firebaseLog = LocalFirebaseLogHelper.current

        LaunchedEffect(isOffline) {
            firebaseLog.sendLog("MovieMainScreen", "isOffline $isOffline")

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

//@Composable
//fun MovieMainScreen(
//    appState: MovieAppState,
//    snackbarHostState: SnackbarHostState
//) {
//    val context = LocalContext.current
//    val windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo()
//    val layoutType = NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(windowAdaptiveInfo)
//    val currentDestination = appState.currentDestination
//    val navigationSuiteItemColors = NavigationSuiteItemColors(
//        navigationBarItemColors = NavigationBarItemDefaults.colors(
//            selectedIconColor = MovieNavigationDefaults.navigationSelectedItemColor(),
//            unselectedIconColor = MovieNavigationDefaults.navigationContentColor(),
////            selectedTextColor = MovieNavigationDefaults.navigationSelectedItemColor(),
//            unselectedTextColor = MovieNavigationDefaults.navigationContentColor(),
//            indicatorColor = MovieNavigationDefaults.navigationIndicatorColor(),
//        ),
//        navigationRailItemColors = NavigationRailItemDefaults.colors(
//            selectedIconColor = MovieNavigationDefaults.navigationSelectedItemColor(),
//            unselectedIconColor = MovieNavigationDefaults.navigationContentColor(),
////            selectedTextColor = MovieNavigationDefaults.navigationSelectedItemColor(),
//            unselectedTextColor = MovieNavigationDefaults.navigationContentColor(),
//            indicatorColor = MovieNavigationDefaults.navigationIndicatorColor(),
//        ),
//        navigationDrawerItemColors = NavigationDrawerItemDefaults.colors(
//            selectedIconColor = MovieNavigationDefaults.navigationSelectedItemColor(),
//            unselectedIconColor = MovieNavigationDefaults.navigationContentColor(),
////            selectedTextColor = MovieNavigationDefaults.navigationSelectedItemColor(),
//            unselectedTextColor = MovieNavigationDefaults.navigationContentColor(),
//        ),
//    )
//
//    LocalFirebaseLogHelper.current.sendLog("Navigation", "layoutType -> $layoutType")
//
//    NavigationSuiteScaffold(
//        navigationSuiteItems = {
//            MovieNavigationSuiteScope(
//                navigationSuiteScope = this,
//                navigationSuiteItemColors = navigationSuiteItemColors,
//            ).run {
//                appState.topLevelDestinations.forEach { destination ->
//                    val selected = currentDestination.isRouteInHierarchy(destination.baseRoute)
//                    val label = context.getString(destination.titleTextId)
//
//                    item(
//                        selected = selected,
//                        onClick = { appState.navigateToTopLevelDestination(topLevelDestination = destination) },
//                        icon = {
//                            Icon(
//                                imageVector = destination.unselectedIcon,
//                                contentDescription = "unselected_$label",
//                            )
//                        },
//                        selectedIcon = {
//                            Icon(
//                                imageVector = destination.selectedIcon,
//                                contentDescription = "selected_$label",
//                            )
//                        },
//                        label = { Text(text = stringResource(destination.iconTextId)) },
//                        modifier = Modifier.semantics { contentDescription = label }
//                    )
//                }
//            }
//        },
//        layoutType = layoutType,
//        containerColor = Color.Transparent,
//        navigationSuiteColors = NavigationSuiteDefaults.colors(
//            navigationBarContentColor = MovieNavigationDefaults.navigationContentColor(),
//            navigationRailContainerColor = Color.Transparent,
//        ),
//        modifier = Modifier,
//    ) {
//        Scaffold(
//            modifier = Modifier.fillMaxSize(),
//            snackbarHost = { SnackbarHost(modifier = Modifier.semantics { contentDescription = "snackbar" }, hostState = snackbarHostState) },
//        ) { innerPadding ->
//            val isOffline by appState.isOffline.collectAsStateWithLifecycle()
//            val notConnectedMessage = stringResource(R.string.not_connected)
//            val firebaseLog = LocalFirebaseLogHelper.current
//
//            LaunchedEffect(isOffline) {
//                firebaseLog.sendLog("MovieMainScreen", "isOffline $isOffline")
//
//                if (isOffline) {
//                    snackbarHostState.showSnackbar(
//                        message = notConnectedMessage,
//                        duration = Indefinite,
//                    )
//                }
//            }
//
//            MovieAppNavHost(
//                modifier = Modifier.padding(innerPadding),
//                appState = appState,
//                onShowSnackbar = { message, action ->
//                    snackbarHostState.showSnackbar(
//                        message = message,
//                        actionLabel = action,
//                        duration = Short,
//                    ) == ActionPerformed
//                }
//            )
//        }
//    }
//}

@Composable
fun Navigation(
    appState: MovieAppState
) {
    val windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo()
    val layoutType = NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(windowAdaptiveInfo)
    val context = LocalContext.current
    val currentDestination = appState.currentDestination

    LocalFirebaseLogHelper.current.sendLog("Navigation", "layoutType -> $layoutType")

    when (layoutType) {
        NavigationSuiteType.NavigationBar -> {
            NavigationBar(
                modifier = Modifier.fillMaxWidth().height(height = dp50).topLineBorder(strokeWidth = dp1, color = MovieNavigationDefaults.navigationBorderColor()),
                containerColor = MovieNavigationDefaults.navigationContainerColor(),
                contentColor = MovieNavigationDefaults.navigationContentColor()
            ) {
//                MovieNavigation().run {
//                    appState.topLevelDestinations.forEach { destination ->
//                        val selected = currentDestination.isRouteInHierarchy(destination.baseRoute)
//                        val label = context.getString(destination.titleTextId)
//
//                        item(
//                            selected = selected,
//                            onClick = { appState.navigateToTopLevelDestination(topLevelDestination = destination) },
//                            icon = {
//                                if (selected) {
//                                    Icon(
//                                        imageVector = destination.selectedIcon,
//                                        contentDescription = "selected_$label",
//                                    )
//                                } else {
//                                    Icon(
//                                        imageVector = destination.unselectedIcon,
//                                        contentDescription = "unselected_$label",
//                                    )
//                                }
//                            },
//                            label = { Text(text = stringResource(destination.iconTextId)) },
//                            modifier = Modifier.semantics { contentDescription = label }
//                        )
//                    }
//                }
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
                modifier = Modifier.width(width = dp50).fillMaxHeight().border(width = dp1, color = MovieNavigationDefaults.navigationBorderColor()),
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

class MovieNavigationSuiteScope internal constructor(
    private val navigationSuiteScope: NavigationSuiteScope,
    private val navigationSuiteItemColors: NavigationSuiteItemColors,
) {
    fun item(
        selected: Boolean,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        icon: @Composable () -> Unit,
        selectedIcon: @Composable () -> Unit = icon,
        label: @Composable (() -> Unit)? = null,
    ) = navigationSuiteScope.item(
        selected = selected,
        onClick = onClick,
        icon = {
            if (selected) {
                selectedIcon()
            } else {
                icon()
            }
        },
        label = label,
        colors = navigationSuiteItemColors,
        modifier = modifier,
    )
}