package com.cheeke.surfy.ui

import androidx.annotation.Keep
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.cheeke.surfy.R
import com.cheeke.surfy.SurfyAppState
import com.cheeke.surfy.common.Log
import com.cheeke.surfy.data.util.POSTER_IMAGE_RATIO
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
import com.cheeke.surfy.model.MediaType
import com.cheeke.surfy.navigation.Navigator
import com.cheeke.surfy.navigation.TOP_LEVEL_NAV_ITEMS
import com.cheeke.surfy.navigation.toEntries
import com.cheeke.surfy.search.navigation.SearchNavKey
import com.cheeke.surfy.search.navigation.searchEntry
import com.cheeke.surfy.ui.dialog.Indexer
import com.cheeke.surfy.ui.image.DynamicAsyncImageLoader
import com.cheeke.surfy.ui.utils.Line
import com.cheeke.surfy.ui.utils.border
import com.cheeke.surfy.ui.utils.bounceClick
import com.cheeke.surfy.ui.utils.dp1
import com.cheeke.surfy.ui.utils.dp10
import com.cheeke.surfy.ui.utils.dp16
import com.cheeke.surfy.ui.utils.dp20
import com.cheeke.surfy.ui.utils.dp300
import com.cheeke.surfy.ui.utils.dp40
import com.cheeke.surfy.ui.utils.dp5
import com.cheeke.surfy.ui.utils.dp50
import com.cheeke.surfy.ui.utils.roundedCornerClickable
import com.cheeke.surfy.ui.utils.sp15
import com.cheeke.surfy.ui.utils.sp20
import com.cheeke.surfy.utils.VerticalRollingAnimation
import kotlinx.serialization.Serializable

@Composable
fun SurfyApp(
    appState: SurfyAppState,
    snackbarHostState: SnackbarHostState,
    nextWeekReleaseMovies: List<Media>,
    dismissNextWeekReleaseDialog: () -> Unit,
    dontShowNextWeekReleaseDialogToday: () -> Unit,
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

        val dialogStrategy = remember { DialogSceneStrategy<NavKey>() }
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
            nextWeekReleaseMoviesEntry(
                metadata = DialogSceneStrategy.dialog(
                    DialogProperties(
                        windowTitle = "NextWeekReleaseMoviesNavKey",
                        dismissOnBackPress = true,
                        dismissOnClickOutside = false
                    )
                ),
                releaseMovies = nextWeekReleaseMovies,
                onDismiss = navigator::goBack,
                goToMovie = navigator::navigateToMovie,
                goToTv = navigator::navigateToTv,
                dismissNextWeekReleaseDialog = dismissNextWeekReleaseDialog,
                updateShowNextReleaseMoviesDate = dontShowNextWeekReleaseDialogToday
            )
        }

        NavDisplay(
            modifier = Modifier.padding(paddingValues = innerPadding),
            entries = navigator.state.toEntries(entryProvider),
            sceneStrategy = dialogStrategy,
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

@Serializable
@Keep
data object NextWeekReleaseMoviesNavKey : NavKey

fun EntryProviderScope<NavKey>.nextWeekReleaseMoviesEntry(
    metadata: Map<String, Any>,
    releaseMovies: List<Media>,
    onDismiss: () -> Unit,
    goToMovie: (Int) -> Unit,
    goToTv: (Int) -> Unit,
    updateShowNextReleaseMoviesDate: () -> Unit,
    dismissNextWeekReleaseDialog: () -> Unit
) {
    entry<NextWeekReleaseMoviesNavKey>(
        metadata = metadata
    ) {
        ReleaseMoviesDialog(
            dismissNextWeekReleaseDialog = dismissNextWeekReleaseDialog,
            updateShowNextReleaseMoviesDate = updateShowNextReleaseMoviesDate,
            onDismiss = onDismiss,
            releaseMovies = releaseMovies,
            goToMovie = goToMovie,
            goToTv = goToTv
        )
    }
}

@Composable
fun ReleaseMoviesDialog(
    updateShowNextReleaseMoviesDate: () -> Unit,
    onDismiss: () -> Unit,
    releaseMovies: List<Media>,
    goToMovie: (Int) -> Unit,
    goToTv: (Int) -> Unit,
    dismissNextWeekReleaseDialog: () -> Unit
) {
    val pagerState = rememberPagerState(initialPage = 0) { releaseMovies.size }

    Column(
        modifier = Modifier
            .width(width = dp300)
            .background(color = Color.White, shape = RoundedCornerShape(size = dp10))
            .verticalScroll(state = rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        HorizontalPager(
            modifier = Modifier.fillMaxWidth(),
            state = pagerState,
        ) { index ->
            Log.d("NextWeekReleaseMovies Index -> $index")
            Box {
                DynamicAsyncImageLoader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onDismiss()
                            when (releaseMovies[index].mediaType) {
                                MediaType.MOVIE -> goToMovie(releaseMovies[index].id ?: -1)
                                MediaType.TV -> goToTv(releaseMovies[index].id ?: -1)
                                else -> Log.d("mediatype not found...")
                            }
                            dismissNextWeekReleaseDialog()
                        }
                        .aspectRatio(ratio = POSTER_IMAGE_RATIO)
                        .clip(shape = RoundedCornerShape(topStart = dp10, topEnd = dp10)),
                    source = "${releaseMovies[index].posterPath}",
                    contentDescription = "ReleaseMovieImage"
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(color = Color(color = 0x33000000)),
                    text = stringResource(id = com.cheeke.surfy.feature.home.R.string.release_movie, releaseMovies[pagerState.currentPage].releaseDate ?: ""),
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
                Indexer(
                    modifier = Modifier
                        .padding(top = dp10, end = dp10)
                        .wrapContentSize()
                        .background(
                            color = Color(color = 0x33000000),
                            shape = RoundedCornerShape(size = dp20)
                        )
                        .align(Alignment.TopEnd),
                    current = pagerState.currentPage + 1,
                    size = pagerState.pageCount
                )
            }
        }
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = stringResource(id = R.string.coming_soon_movie),
            color = Color.Black
        )
        Button(
            modifier = Modifier.padding(horizontal = dp16, vertical = dp10),
            onClick = { onDismiss() }
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(size = dp20)),
                text = stringResource(id = R.string.close),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = sp20,
                color = Color.White
            )
        }
        Text(
            modifier = Modifier
                .padding(bottom = dp10)
                .fillMaxWidth()
                .wrapContentHeight()
                .clickable {
                    updateShowNextReleaseMoviesDate()
                    onDismiss()
                },
            text = stringResource(id = R.string.no_show_today),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = sp15,
            color = Color.Black
        )
    }
}