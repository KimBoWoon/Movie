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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarDuration.Indefinite
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.bowoon.detail.movie.navigation.MovieNavKey
import com.bowoon.detail.movie.navigation.movieEntry
import com.bowoon.detail.movie.navigation.navigateToMovie
import com.bowoon.detail.people.navigation.navigateToPeople
import com.bowoon.detail.people.navigation.peopleEntry
import com.bowoon.detail.series.navigation.navigateToSeries
import com.bowoon.detail.series.navigation.seriesEntry
import com.bowoon.favorite.navigation.favoriteEntry
import com.bowoon.firebase.LocalFirebaseLogHelper
import com.bowoon.home.navigation.HomeNavKey
import com.bowoon.home.navigation.homeEntry
import com.bowoon.model.Movie
import com.bowoon.movie.MovieAppState
import com.bowoon.movie.R
import com.bowoon.movie.navigation.TOP_LEVEL_NAV_ITEMS
import com.bowoon.movie.utils.VerticalRollingAnimation
import com.bowoon.my.navigation.myEntry
import com.bowoon.navigation.Navigator
import com.bowoon.navigation.toEntries
import com.bowoon.search.navigation.SearchNavKey
import com.bowoon.search.navigation.searchEntry
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun MovieApp(
    appState: MovieAppState,
    snackbarHostState: SnackbarHostState,
    nextWeekReleaseMovies: List<Movie>,
    deeplinkBackstack: List<NavKey> = emptyList(),
    onDeeplinkProcessed: () -> Unit
) {
    val navigator = remember { Navigator(state = appState.navigationState) }
    val isTopLevelRoute = navigator.state.backStacks[navigator.state.topLevelRoute]?.last()?.javaClass in TOP_LEVEL_NAV_ITEMS.map { it.key.javaClass }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(modifier = Modifier.semantics { contentDescription = "snackbar" }, hostState = snackbarHostState) },
        topBar = {
            MovieSearchTopBar(
                navigator = navigator,
                isTopLevelRoute = isTopLevelRoute,
                nextWeekReleaseMovies = nextWeekReleaseMovies
            )
        },
        bottomBar = {
            MovieBottomBar(
                navigator = navigator,
                isTopLevelRoute = isTopLevelRoute
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

        val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()
        val entryProvider = entryProvider {
            movieEntry(
                goToBack = { navigator.goBack() },
                goToMovie = navigator::navigateToMovie,
                goToPeople = navigator::navigateToPeople,
                onShowSnackbar = { message, action ->
                    snackbarHostState.showSnackbar(
                        message = message,
                        actionLabel = action,
                        duration = SnackbarDuration.Short,
                    ) == SnackbarResult.ActionPerformed
                }
            )
            peopleEntry(
                goToBack = { navigator.goBack() },
                goToMovie = navigator::navigateToMovie,
                onShowSnackbar = { message, action ->
                    snackbarHostState.showSnackbar(
                        message = message,
                        actionLabel = action,
                        duration = SnackbarDuration.Short,
                    ) == SnackbarResult.ActionPerformed
                }
            )
            seriesEntry(
                goToBack = { navigator.goBack() },
                goToMovie = navigator::navigateToMovie
            )
            favoriteEntry(
                goToMovie = navigator::navigateToMovie,
                goToPeople = navigator::navigateToPeople,
                onShowSnackbar = { message, action ->
                    snackbarHostState.showSnackbar(
                        message = message,
                        actionLabel = action,
                        duration = SnackbarDuration.Short,
                    ) == SnackbarResult.ActionPerformed
                }
            )
            homeEntry(
                goToMovie = navigator::navigateToMovie
            )
            myEntry()
            searchEntry(
                goToMovie = navigator::navigateToMovie,
                goToPeople = navigator::navigateToPeople,
                goToSeries = navigator::navigateToSeries,
                onShowSnackbar = { message, action ->
                    snackbarHostState.showSnackbar(
                        message = message,
                        actionLabel = action,
                        duration = SnackbarDuration.Short,
                    ) == SnackbarResult.ActionPerformed
                }
            )
        }

        NavDisplay(
            modifier = Modifier.padding(paddingValues = innerPadding),
            entries = navigator.state.toEntries(entryProvider),
            sceneStrategy = listDetailStrategy,
            onBack = { navigator.goBack() },
        )
    }

    LaunchedEffect(key1 = deeplinkBackstack) {
        var deeplinkList = deeplinkBackstack

        if (deeplinkList.isNotEmpty()) {
            navigator.state.isFromDeeplink = true
        }

        while (deeplinkList.isNotEmpty()) {
            val firstRoute = deeplinkList.first()
            val targetTabKey = TOP_LEVEL_NAV_ITEMS.keys.firstOrNull { it.javaClass == firstRoute.javaClass } ?: HomeNavKey

            navigator.state.topLevelRoute = if (TOP_LEVEL_NAV_ITEMS.keys.firstOrNull { it.javaClass == firstRoute.javaClass } != null) firstRoute else HomeNavKey

            if (navigator.state.backStacks[targetTabKey] != null) {
                navigator.state.backStacks[targetTabKey]?.add(element = firstRoute)
//                if (navigator.state.backStacks[targetTabKey]?.get(0)?.javaClass == firstRoute.javaClass) {
//                    navigator.state.backStacks[targetTabKey]?.clear()
//                    navigator.state.backStacks[targetTabKey]?.add(element = firstRoute)
//                } else {
//                    navigator.state.backStacks[targetTabKey]?.clear()
//                    navigator.state.backStacks[targetTabKey]?.add(element = firstRoute)
//                }
            }

            deeplinkList = deeplinkList.drop(n = 1)
        }

        onDeeplinkProcessed()
    }
}

@Composable
fun MovieSearchTopBar(
    navigator: Navigator,
    isTopLevelRoute: Boolean,
    nextWeekReleaseMovies: List<Movie>
) {
    AnimatedVisibility(
        modifier = Modifier.statusBarsPadding(),
        visible = isTopLevelRoute,
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
                    .bounceClick(onClick = { navigator.navigate(route = SearchNavKey) }),
                contentAlignment = Alignment.Center
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
                            .clickable { navigator.navigate(route = SearchNavKey) },
                        imageVector = Icons.Default.Search,
                        contentDescription = "goToSearch",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    if (nextWeekReleaseMovies.isEmpty()) {
                        Text(
                            modifier = Modifier
                                .wrapContentWidth()
                                .padding(start = dp10),
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
                                    nextWeekReleaseMovie.id?.let { id ->
                                        navigator.navigate(route = MovieNavKey(id = id))
                                    }
                                },
                            text = stringResource(id = R.string.next_week_release_movie, nextWeekReleaseMovie.title ?: ""),
                            maxLines = 1
                        )
                    } else {
                        VerticalRollingAnimation(
                            modifier = Modifier.padding(start = dp10, end = dp20),
                            nextWeekReleaseMovies = nextWeekReleaseMovies,
                            goToMovie = { id ->
                                navigator.navigate(route = MovieNavKey(id = id))
                            }
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun MovieBottomBar(
    navigator: Navigator,
    isTopLevelRoute: Boolean
) {
    AnimatedVisibility(
        visible = isTopLevelRoute,
        label = "BottomNavigationAnimation",
        enter = expandVertically(),
        exit = shrinkVertically(),
        content = {
            MovieNavigation(
                navigator = navigator
            )
        }
    )
}

@Composable
fun MovieNavigation(
    navigator: Navigator
) {
    val context = LocalContext.current

    LocalFirebaseLogHelper.current.sendLog("Navigation", "create navigation bar")

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
        TOP_LEVEL_NAV_ITEMS.forEach { (navKey, navItem) ->
            BottomNavigationBarItem(
                selected = navKey == navigator.state.topLevelRoute,
                label = context.getString(navItem.titleTextId),
                selectedIcon = navItem.selectedIcon,
                unSelectedIcon = navItem.unselectedIcon,
                onClick = { navigator.navigate(route = navKey) }
            )
        }
    }
}
