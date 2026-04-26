package com.cheeke.surfy.ui

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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import com.cheeke.surfy.R
import com.cheeke.surfy.common.Log
import com.cheeke.surfy.data.util.POSTER_IMAGE_RATIO
import com.cheeke.surfy.firebase.LocalFirebaseLogHelper
import com.cheeke.surfy.model.Media
import com.cheeke.surfy.model.MediaType
import com.cheeke.surfy.navigation.FavoriteScreen
import com.cheeke.surfy.navigation.HomeScreen
import com.cheeke.surfy.navigation.LocalAppNavigator
import com.cheeke.surfy.navigation.RootScreen
import com.cheeke.surfy.navigation.TopLevelDestination
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
import com.slack.circuit.backstack.SaveableBackStack
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.foundation.CircuitContent
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.components.ActivityRetainedComponent

data class RootState(
    val currentTab: RootTab,
    val onTabSelected: (RootTab) -> Unit,
    val openSearch: () -> Unit,
    val goToMovie: (Int) -> Unit,
    val goToPeople: (Int) -> Unit,
    val goToTv: (Int) -> Unit
) : CircuitUiState

enum class RootTab {
    HOME, FAVORITE
}

@CircuitInject(screen = RootScreen::class, scope = ActivityRetainedComponent::class)
@Composable
fun RootScreen(
    modifier: Modifier = Modifier,
    rootState: RootState
) {
    var selectedTab by rememberRetained { mutableStateOf(value = RootTab.HOME) }

    Scaffold(
        topBar = {
            MovieSearchTopBar(
                goToSearch = rootState.openSearch,
                showSettingDialog = LocalAppNavigator.current::goToSetting
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == RootTab.HOME,
                    onClick = { selectedTab = RootTab.HOME },
                    icon = { Icon(imageVector = Icons.Default.Home, contentDescription = null) },
                    label = { Text("홈") }
                )
                NavigationBarItem(
                    selected = selectedTab == RootTab.FAVORITE,
                    onClick = { selectedTab = RootTab.FAVORITE },
                    icon = { Icon(imageVector = Icons.Default.Favorite, contentDescription = null) },
                    label = { Text("찜") }
                )
            }
        }
    ) { paddingValues ->
        CircuitContent(
            screen = HomeScreen,
            modifier = Modifier
                .padding(paddingValues)
                .alpha(if (selectedTab == RootTab.HOME) 1f else 0f)
                .zIndex(if (selectedTab == RootTab.HOME) 1f else 0f)
        )
        CircuitContent(
            screen = FavoriteScreen(),
            modifier = Modifier
                .padding(paddingValues)
                .alpha(if (selectedTab == RootTab.FAVORITE) 1f else 0f)
                .zIndex(if (selectedTab == RootTab.FAVORITE) 1f else 0f)
        )
    }
}

class RootPresenter @AssistedInject constructor(
) : Presenter<RootState> {
    @CircuitInject(screen = RootScreen::class, scope = ActivityRetainedComponent::class)
    @AssistedFactory
    interface Factory {
        fun create() : RootPresenter
    }

    @Composable
    override fun present(): RootState {
        val navigator = LocalAppNavigator.current
        var currentTab by rememberSaveable { mutableStateOf(value = RootTab.HOME) }

        return RootState(
            currentTab = currentTab,
            onTabSelected = { currentTab = it },
            openSearch = navigator::goToSearch,
            goToMovie = navigator::goToMovie,
            goToPeople = navigator::goToPeople,
            goToTv = navigator::goToTv
        )
    }
}

@Composable
fun MovieSearchTopBar(
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
//                if (nextWeekReleaseMovies.isEmpty()) {
//                    Text(
//                        modifier = Modifier
//                            .wrapContentWidth()
//                            .padding(start = dp10),
//                        text = stringResource(id = R.string.go_to_search),
//                        maxLines = 1,
//                        overflow = TextOverflow.Ellipsis
//                    )
//                } else if (nextWeekReleaseMovies.size == 1) {
//                    val nextWeekReleaseMovie = nextWeekReleaseMovies.first()
//
//                    Text(
//                        modifier = Modifier
//                            .wrapContentWidth()
//                            .clickable {
//                                nextWeekReleaseMovie.id?.let { id ->
//                                    navigator.goTo(screen = MovieScreen(id = id))
//                                }
//                            },
//                        text = stringResource(id = R.string.next_week_release_movie, nextWeekReleaseMovie.title ?: ""),
//                        maxLines = 1,
//                        overflow = TextOverflow.Ellipsis
//                    )
//                } else {
//                    VerticalRollingAnimation(
//                        modifier = Modifier.padding(start = dp10, end = dp20),
//                        nextWeekReleaseMovies = nextWeekReleaseMovies,
//                        goToMovie = { id ->
//                            navigator.goTo(screen = MovieScreen(id = id))
//                        }
//                    )
//                }
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
    onTabSelected: (RootTab) -> Unit,
    navigator: Navigator,
    backStack: SaveableBackStack
) {
    MovieNavigation(
        currentTab = currentTab,
        onTabSelected = onTabSelected,
        navigator = navigator,
        backStack = backStack,
    )
}

@Composable
fun MovieNavigation(
    currentTab: RootTab,
    onTabSelected: (RootTab) -> Unit,
    navigator: Navigator,
    backStack: SaveableBackStack
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
//                selected = navItem.screen.javaClass.simpleName == navigator.peek()?.javaClass?.simpleName,
                selected = currentTab == navItem.rootTab,
                label = stringResource(id = navItem.titleTextId),
                selectedIcon = navItem.selectedIcon,
                unSelectedIcon = navItem.unselectedIcon,
                onClick = {
                    onTabSelected(navItem.rootTab)
//                    val found = backStack.any { it.screen.javaClass.simpleName == navItem.screen.javaClass.simpleName }
//                    if (found) {
//                        backStack.popUntil { it.screen.javaClass.simpleName == navItem.screen.javaClass.simpleName }
//                    } else {
//                        navigator.goTo(screen = navItem.screen)
//                        navigator.resetRoot(newRoot = navItem.screen, saveState = true, restoreState = true)
//                    }
                }
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