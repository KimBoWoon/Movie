package com.cheeke.surfy.ui.root

import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cheeke.surfy.R
import com.cheeke.surfy.common.Log
import com.cheeke.surfy.data.util.POSTER_IMAGE_RATIO
import com.cheeke.surfy.firebase.LocalFirebaseLogHelper
import com.cheeke.surfy.model.Media
import com.cheeke.surfy.model.MediaType
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.model.Tv
import com.cheeke.surfy.navigation.FavoriteScreen
import com.cheeke.surfy.navigation.HomeScreen
import com.cheeke.surfy.navigation.LocalAppNavigator
import com.cheeke.surfy.navigation.RootScreen
import com.cheeke.surfy.navigation.TopLevelDestination
import com.cheeke.surfy.ui.BottomNavigationBarItem
import com.cheeke.surfy.ui.MovieNavigationDefaults
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
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.foundation.CircuitContent
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.retained.rememberRetainedStateHolder
import dagger.hilt.android.components.ActivityRetainedComponent

@CircuitInject(screen = RootScreen::class, scope = ActivityRetainedComponent::class)
@Composable
fun RootScreen(
    modifier: Modifier = Modifier,
    rootState: RootState
) {
    val retainedStateHolder = rememberRetainedStateHolder()
    var selectedTab by rememberRetained { mutableStateOf(value = RootTab.HOME) }
    val bottomDeeplink by rootState.bottomDeeplink.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = bottomDeeplink) {
        if (bottomDeeplink.isEmpty()) return@LaunchedEffect

        bottomDeeplink.forEach { route ->
            when (route) {
                is FavoriteScreen -> selectedTab = RootTab.FAVORITE
                is HomeScreen -> selectedTab = RootTab.HOME
            }
        }

        rootState.consumeBottomDeepLink()
    }

    Scaffold(
        topBar = {
            MovieSearchTopBar(
                nextWeekReleaseMovies = rootState.nextWeekReleaseMedia,
                goToMovie = rootState.goToMovie,
                goToTv = rootState.goToTv,
                goToSearch = rootState.openSearch,
                showSettingDialog = LocalAppNavigator.current::goToSetting
            )
        },
        bottomBar = {
            MovieBottomBar(
                currentTab = selectedTab,
                onTabSelected = { root -> selectedTab = root }
            )
        }
    ) { paddingValues ->
        retainedStateHolder.RetainedStateProvider(key = selectedTab.name) {
            when (selectedTab) {
                RootTab.HOME -> CircuitContent(
                    screen = HomeScreen,
                    modifier = Modifier.padding(paddingValues = paddingValues)
                )
                RootTab.FAVORITE -> CircuitContent(
                    screen = FavoriteScreen(),
                    modifier = Modifier.padding(paddingValues = paddingValues)
                )
            }
        }
    }

    BackHandler(enabled = selectedTab != RootTab.HOME) {
        selectedTab = RootTab.HOME
    }
}

@Composable
fun MovieSearchTopBar(
    nextWeekReleaseMovies: List<Media>,
    goToMovie: (Int) -> Unit,
    goToTv: (Int) -> Unit,
    goToSearch: () -> Unit,
    showSettingDialog: () -> Unit
) {
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
                .roundedCornerClickable(onClick = { goToSearch() }),
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
                        .clickable { goToSearch() },
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
                                when (nextWeekReleaseMovie) {
                                    is Movie -> goToMovie(nextWeekReleaseMovie.id ?: -1)
                                    is Tv -> goToTv(nextWeekReleaseMovie.id ?: -1)
                                }
                            },
                        text = stringResource(id = R.string.next_week_release_movie, nextWeekReleaseMovie.title ?: ""),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                } else {
                    VerticalRollingAnimation(
                        modifier = Modifier.padding(start = dp10, end = dp20),
                        nextWeekReleaseMovies = nextWeekReleaseMovies,
                        goToMovie = goToMovie,
                        gotoTv = goToTv
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

@Composable
fun MovieBottomBar(
    currentTab: RootTab,
    onTabSelected: (RootTab) -> Unit
) {
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
        TopLevelDestination.entries.forEach { navItem ->
            BottomNavigationBarItem(
                selected = currentTab == navItem.rootTab,
                label = stringResource(id = navItem.titleTextId),
                selectedIcon = navItem.selectedIcon,
                unSelectedIcon = navItem.unselectedIcon,
                onClick = { onTabSelected(navItem.rootTab) }
            )
        }
    }
}

@Composable
fun ReleaseMoviesDialog(
    updateShowNextReleaseMoviesDate: () -> Unit,
    releaseMovies: List<Media>,
    goToMovie: (Int) -> Unit,
    goToTv: (Int) -> Unit,
    dismissNextWeekReleaseDialog: () -> Unit
) {
    Dialog(
        onDismissRequest = { dismissNextWeekReleaseDialog() },
        properties = DialogProperties(
            windowTitle = "NextWeekReleaseMoviesNavKey",
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        ),
        content = {
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
                    onClick = { dismissNextWeekReleaseDialog() }
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(size = dp20)
                            ),
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
                        .clickable { updateShowNextReleaseMoviesDate() },
                    text = stringResource(id = R.string.no_show_today),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = sp15,
                    color = Color.Black
                )
            }
        }
    )
}