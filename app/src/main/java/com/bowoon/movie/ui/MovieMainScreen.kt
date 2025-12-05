package com.bowoon.movie.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration.Indefinite
import androidx.compose.material3.SnackbarDuration.Short
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult.ActionPerformed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.bowoon.detail.navigation.DetailRoute
import com.bowoon.firebase.LocalFirebaseLogHelper
import com.bowoon.model.Movie
import com.bowoon.movie.MovieAppState
import com.bowoon.movie.R
import com.bowoon.movie.navigation.MovieAppNavHost
import com.bowoon.movie.navigation.TopLevelDestination
import com.bowoon.movie.utils.VerticalRollingAnimation
import com.bowoon.search.navigation.SearchRoute
import com.bowoon.ui.BottomNavigationBarItem
import com.bowoon.ui.MovieNavigationDefaults
import com.bowoon.ui.utils.Line
import com.bowoon.ui.utils.border
import com.bowoon.ui.utils.bounceClick
import com.bowoon.ui.utils.dp1
import com.bowoon.ui.utils.dp10
import com.bowoon.ui.utils.dp16
import com.bowoon.ui.utils.dp20
import com.bowoon.ui.utils.dp40
import com.bowoon.ui.utils.dp50
import kotlin.reflect.KClass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieMainScreen(
    appState: MovieAppState,
    snackbarHostState: SnackbarHostState,
    nextWeekReleaseMovies: List<Movie>
) {
    val currentBackStack by appState.navController.currentBackStackEntryAsState()
    val topHierarchy = currentBackStack?.destination?.route in TopLevelDestination.entries.map { it.route.qualifiedName }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(modifier = Modifier.semantics { contentDescription = "snackbar" }, hostState = snackbarHostState) },
        topBar = {
            AnimatedVisibility(
                modifier = Modifier.statusBarsPadding(),
                visible = topHierarchy,
                label = "TopSearchBarAnimation",
                enter = expandVertically(),
                exit = shrinkVertically(),
                content = {
                    Box(
                        modifier = Modifier
                            .padding(top = dp10, bottom = dp10, start = dp16, end = dp16)
                            .fillMaxWidth()
                            .height(height = dp40)
                            .clip(shape = RoundedCornerShape(percent = 50))
                            .background(color = MaterialTheme.colorScheme.inverseOnSurface)
                            .bounceClick(onClick = { appState.navController.navigate(SearchRoute) })
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier
                                    .wrapContentSize()
                                    .padding(start = dp20)
                                    .align(Alignment.CenterVertically)
                                    .clickable { appState.navController.navigate(route = SearchRoute) },
                                imageVector = Icons.Default.Search,
                                contentDescription = "goToSearch",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                            if (nextWeekReleaseMovies.isEmpty()) {
                                Text(
                                    modifier = Modifier.wrapContentWidth(),
                                    text = stringResource(id = R.string.go_to_search),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            } else if (nextWeekReleaseMovies.size == 1) {
                                val nextWeekReleaseMovie = nextWeekReleaseMovies.first()

                                Text(
                                    modifier = Modifier
                                        .wrapContentWidth()
                                        .clickable {
                                            nextWeekReleaseMovie.id?.let {
                                                appState.navController.navigate(route = DetailRoute(id = it))
                                            }
                                        },
                                    text = "${nextWeekReleaseMovie.title} 곧 개봉합니다!",
                                    maxLines = 1
                                )
                            } else {
                                VerticalRollingAnimation(
                                    modifier = Modifier.padding(start = dp10, end = dp20),
                                    appState = appState,
                                    nextWeekReleaseMovies = nextWeekReleaseMovies
                                )
                            }
                        }
                    }
                }
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = topHierarchy,
                label = "BottomNavigationAnimation",
                enter = expandVertically(),
                exit = shrinkVertically(),
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

        MovieAppNavHost(
            modifier = Modifier.padding(paddingValues = innerPadding),
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
fun MovieNavigation(appState: MovieAppState) {
    val context = LocalContext.current
    val currentDestination = appState.currentDestination

    LocalFirebaseLogHelper.current.sendLog("Navigation", "create bottom navigation")

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
            val selected = currentDestination.isRouteInHierarchy(route = destination.route)
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

private fun NavDestination?.isRouteInHierarchy(route: KClass<*>): Boolean =
    this?.hierarchy?.any { it.hasRoute(route) } ?: false