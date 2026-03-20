package com.cheeke.surfy.ui

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Settings
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.cheeke.surfy.R
import com.cheeke.surfy.SurfyAppState
import com.cheeke.surfy.detail.movie.navigation.MovieNavKey
import com.cheeke.surfy.detail.movie.navigation.movieEntry
import com.cheeke.surfy.detail.movie.navigation.navigateToMovie
import com.cheeke.surfy.detail.people.navigation.navigateToPeople
import com.cheeke.surfy.detail.people.navigation.peopleEntry
import com.cheeke.surfy.detail.series.navigation.navigateToSeries
import com.cheeke.surfy.detail.series.navigation.seriesEntry
import com.cheeke.surfy.detail.tv.navigation.navigateToTv
import com.cheeke.surfy.detail.tv.navigation.tvEntry
import com.cheeke.surfy.favorite.navigation.favoriteEntry
import com.cheeke.surfy.firebase.LocalFirebaseLogHelper
import com.cheeke.surfy.home.navigation.homeEntry
import com.cheeke.surfy.model.Media
import com.cheeke.surfy.navigation.Navigator
import com.cheeke.surfy.navigation.TOP_LEVEL_NAV_ITEMS
import com.cheeke.surfy.navigation.toEntries
import com.cheeke.surfy.search.navigation.SearchNavKey
import com.cheeke.surfy.search.navigation.searchEntry
import com.cheeke.surfy.ui.utils.Line
import com.cheeke.surfy.ui.utils.border
import com.cheeke.surfy.ui.utils.bounceClick
import com.cheeke.surfy.ui.utils.dp1
import com.cheeke.surfy.ui.utils.dp10
import com.cheeke.surfy.ui.utils.dp16
import com.cheeke.surfy.ui.utils.dp20
import com.cheeke.surfy.ui.utils.dp40
import com.cheeke.surfy.ui.utils.dp5
import com.cheeke.surfy.ui.utils.dp50
import com.cheeke.surfy.ui.utils.roundedCornerClickable
import com.cheeke.surfy.utils.VerticalRollingAnimation

@Composable
fun SurfyApp(
    appState: SurfyAppState,
    snackbarHostState: SnackbarHostState,
    nextWeekReleaseMovies: List<Media>,
    showSettingDialog: () -> Unit
) {
    val navigator = remember { Navigator(state = appState.navigationState) }
    val isTopLevelRoute by remember {
        derivedStateOf {
            navigator.state.backStacks[navigator.state.topLevelRoute]
                ?.lastOrNull()
                ?.javaClass in TOP_LEVEL_NAV_ITEMS.map { it.key.javaClass }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(modifier = Modifier.semantics { contentDescription = "snackbar" }, hostState = snackbarHostState) },
        topBar = {
            MovieSearchTopBar(
                navigator = navigator,
                isTopLevelRoute = isTopLevelRoute,
                nextWeekReleaseMovies = nextWeekReleaseMovies,
                showSettingDialog = showSettingDialog
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

//        val dialogStrategy = remember { DialogSceneStrategy<NavKey>() }
        val entryProvider = entryProvider {
            movieEntry(
                goToBack = { navigator.goBack() },
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
            peopleEntry(
                goToBack = { navigator.goBack() },
                goToMovie = navigator::navigateToMovie,
                goToTv = navigator::navigateToTv,
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
            tvEntry(
                goToBack = { navigator.goBack() },
                goToTv = navigator::navigateToTv,
                goToPeople = navigator::navigateToPeople,
                onShowSnackbar = { message, action ->
                    snackbarHostState.showSnackbar(
                        message = message,
                        actionLabel = action,
                        duration = SnackbarDuration.Short,
                    ) == SnackbarResult.ActionPerformed
                }
            )
            favoriteEntry(
                goToMovie = navigator::navigateToMovie,
                goToTv = navigator::navigateToTv,
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
                goToMovie = navigator::navigateToMovie,
                goToPeople = navigator::navigateToPeople,
                goToTv = navigator::navigateToTv
            )
            searchEntry(
                goToMovie = navigator::navigateToMovie,
                goToTv = navigator::navigateToTv,
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
//            sceneStrategy = dialogStrategy,
            onBack = { navigator.goBack() },
        )
    }
}

@Composable
fun MovieSearchTopBar(
    navigator: Navigator,
    isTopLevelRoute: Boolean,
    nextWeekReleaseMovies: List<Media>,
    showSettingDialog: () -> Unit
) {
    AnimatedVisibility(
        modifier = Modifier.statusBarsPadding(),
        visible = isTopLevelRoute,
        label = "TopSearchBarAnimation",
        enter = expandVertically(),
        exit = shrinkVertically(),
        content = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = dp10, bottom = dp10, start = dp16)
                        .weight(weight = 1f)
                        .height(height = dp40)
                        .clip(shape = RoundedCornerShape(percent = 50))
                        .background(color = MaterialTheme.colorScheme.inverseOnSurface)
                        .roundedCornerClickable(onClick = { navigator.navigate(route = SearchNavKey()) }),
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
                                .clickable { navigator.navigate(route = SearchNavKey()) },
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

                Icon(
                    modifier = Modifier
                        .size(size = 48.dp)
                        .padding(start = dp5, end = dp16)
                        .bounceClick(onClick = { showSettingDialog() }),
                    imageVector = Icons.Rounded.Settings,
                    contentDescription = "SettingIcon"
                )
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