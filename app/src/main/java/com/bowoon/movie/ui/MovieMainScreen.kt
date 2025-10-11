package com.bowoon.movie.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration.Indefinite
import androidx.compose.material3.SnackbarDuration.Short
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult.ActionPerformed
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.bowoon.detail.navigation.DetailRoute
import com.bowoon.favorite.navigation.FavoriteRoute
import com.bowoon.firebase.LocalFirebaseLogHelper
import com.bowoon.home.navigation.HomeRoute
import com.bowoon.movie.MovieAppState
import com.bowoon.movie.R
import com.bowoon.movie.navigation.MovieAppNavHost
import com.bowoon.my.navigation.MyRoute
import com.bowoon.people.navigation.PeopleRoute
import com.bowoon.search.navigation.SearchRoute
import com.bowoon.ui.BottomNavigationBarItem
import com.bowoon.ui.MovieNavigationDefaults
import com.bowoon.ui.utils.Line
import com.bowoon.ui.utils.border
import com.bowoon.ui.utils.bounceClick
import com.bowoon.ui.utils.dp1
import com.bowoon.ui.utils.dp10
import com.bowoon.ui.utils.dp16
import com.bowoon.ui.utils.dp40
import com.bowoon.ui.utils.dp50
import kotlin.reflect.KClass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieMainScreen(
    appState: MovieAppState,
    snackbarHostState: SnackbarHostState
) {
    var isBottomNavigationBarVisible by remember { mutableStateOf(value = true) }
    val currentBackStack by appState.navController.currentBackStackEntryAsState()

    isBottomNavigationBarVisible = when (currentBackStack?.destination?.route) {
        HomeRoute.javaClass.name,
        SearchRoute.javaClass.name,
        FavoriteRoute.javaClass.name,
        MyRoute.javaClass.name -> true
        else -> false
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(modifier = Modifier.semantics { contentDescription = "snackbar" }, hostState = snackbarHostState) },
//        topBar = {
//            CenterAlignedTopAppBar(
////                modifier = Modifier.height(height = dp40),
//                title = {
//                    Text(text = "ScaffoldTitle")
//                }
//            )
//        },
        bottomBar = {
            AnimatedVisibility(
                visible = isBottomNavigationBarVisible,
                label = "BottomNavigationAnimation",
                enter = expandVertically(),
                exit = shrinkVertically(),
//                enter = slideIn(animationSpec = tween(durationMillis = 500), initialOffset = { IntOffset(x = 0, y = it.height) }),
//                exit = slideOut(animationSpec = tween(durationMillis = 500), targetOffset = { IntOffset(x = 0, y = it.height) }),
                content = {
                    MovieNavigation(appState = appState)
                }
            )
        }
    ) { innerPadding ->
        val isOffline by appState.isOffline.collectAsStateWithLifecycle()
        val notConnectedMessage = stringResource(id = R.string.not_connected)
        val firebaseLog = LocalFirebaseLogHelper.current

        LaunchedEffect(key1 = isOffline) {
            firebaseLog.sendLog("MovieMainScreen", "isOffline $isOffline")

            if (isOffline) {
                snackbarHostState.showSnackbar(
                    message = notConnectedMessage,
                    duration = Indefinite,
                )
            }
        }

        Column(
            modifier = Modifier.padding(paddingValues = innerPadding)
        ) {
            val statusBarHeight = with (receiver = LocalDensity.current) {
                WindowInsets.Companion.statusBars.getTop(density = this)
            }
            AnimatedVisibility(
                visible = currentBackStack?.destination?.route != SearchRoute.javaClass.name &&
                        currentBackStack?.destination?.route != "${DetailRoute::class.java.name}/{id}" &&
                        currentBackStack?.destination?.route != "${PeopleRoute::class.java.name}/{id}",
                label = "TopSearchBarAnimation",
                enter = expandVertically(),
                exit = shrinkVertically(),
//                enter = slideIn(animationSpec = tween(durationMillis = 500), initialOffset = { IntOffset(x = 0, y = -it.height - statusBarHeight) }),
//                exit = slideOut(animationSpec = tween(durationMillis = 500), targetOffset = { IntOffset(x = 0, y = -it.height - statusBarHeight) }),
                content = {
                    Box(
                        modifier = Modifier
                            .padding(top = dp10, bottom = dp10, start = dp16, end = dp16)
                            .fillMaxWidth()
                            .height(height = dp40)
                            .clip(shape = RoundedCornerShape(percent = 50))
                            .background(color = MaterialTheme.colorScheme.inverseOnSurface)
                            .bounceClick(
                                onClick = { appState.navController.navigate(SearchRoute) }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "검색으로 넘어가기",
                            color = Color.White
                        )
                    }
                }
            )
            MovieAppNavHost(
                modifier = Modifier,
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
}

@Composable
fun MovieNavigation(appState: MovieAppState) {
    val windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo()
    val layoutType = NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(windowAdaptiveInfo)
    val context = LocalContext.current
    val currentDestination = appState.currentDestination

    LocalFirebaseLogHelper.current.sendLog("Navigation", "layoutType -> $layoutType")

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(height = dp50)
            .border(
                line = Line.TOP,
                strokeWidth = dp1,
                color = MovieNavigationDefaults.navigationBorderColor()
            ),
        containerColor = MovieNavigationDefaults.navigationContainerColor(),
        contentColor = MovieNavigationDefaults.navigationContentColor()
    ) {
        appState.topLevelDestinations.forEach { destination ->
            val selected = currentDestination.isRouteInHierarchy(route = destination.baseRoute)
            BottomNavigationBarItem(
                selected = selected,
                label = context.getString(destination.titleTextId),
                selectedIcon = destination.selectedIcon,
                unSelectedIcon = destination.unselectedIcon,
                onClick = { appState.navigateToTopLevelDestination(topLevelDestination = destination) }
            )
        }
    }
}

private fun NavDestination?.isRouteInHierarchy(route: KClass<*>) =
    this?.hierarchy?.any { it.hasRoute(route) } ?: false