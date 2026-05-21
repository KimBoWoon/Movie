package com.cheeke.surfy.favorite

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.cheeke.surfy.analytics.LocalAnalyticsHelper
import com.cheeke.surfy.analytics.TrackScreenViewEvent
import com.cheeke.surfy.analytics.logFavorite
import com.cheeke.surfy.common.retainedLazyGridListState
import com.cheeke.surfy.data.util.PEOPLE_IMAGE_RATIO
import com.cheeke.surfy.data.util.POSTER_IMAGE_RATIO
import com.cheeke.surfy.feature.favorite.R
import com.cheeke.surfy.firebase.LocalFirebaseLogHelper
import com.cheeke.surfy.model.Media
import com.cheeke.surfy.model.People
import com.cheeke.surfy.navigation.FavoriteScreen
import com.cheeke.surfy.ui.components.CircularProgressComponent
import com.cheeke.surfy.ui.components.FavoriteButtonComponent
import com.cheeke.surfy.ui.components.ScrollToTopComponent
import com.cheeke.surfy.ui.image.DynamicAsyncImageLoader
import com.cheeke.surfy.ui.utils.BOTTOM_NAVIGATION_HEIGHT
import com.cheeke.surfy.ui.utils.bounceClick
import com.cheeke.surfy.ui.utils.dp10
import com.cheeke.surfy.ui.utils.dp16
import com.cheeke.surfy.ui.utils.dp4
import com.cheeke.surfy.ui.utils.dp5
import com.cheeke.surfy.ui.utils.dp6
import com.cheeke.surfy.ui.utils.dp999
import com.slack.circuit.codegen.annotations.CircuitInject
import dagger.hilt.android.components.ActivityRetainedComponent
import kotlinx.coroutines.launch

@CircuitInject(screen = FavoriteScreen::class, scope = ActivityRetainedComponent::class)
@Composable
fun FavoriteScreen(
    modifier: Modifier,
    favoriteState: FavoriteState
) {
    LocalFirebaseLogHelper.current.sendLog("FavoriteScreen", "favorite screen init")
    TrackScreenViewEvent(screenName = "FavoriteScreen")

    val snackbarHostState = remember { SnackbarHostState() }
    val favoriteUiState = favoriteState.favoriteUiState
    val tabIndex = favoriteUiState.tabIndex
    val selectedTab = favoriteUiState.selectedTab
    val favoriteMap = favoriteUiState.favoriteMap
    val analyticsHelper = LocalAnalyticsHelper.current

    LaunchedEffect(key1 = Unit) {
        favoriteState.effect.collect { effect ->
            when (effect) {
                is FavoriteEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(message = effect.message)
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = modifier.fillMaxSize()
        ) {
            SegmentedTabs(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = dp16, end = dp16, bottom = dp10),
                selected = FavoriteTab.entries[tabIndex],
                onSelected = { favoriteTabs ->
                    val index = FavoriteTab.entries.indexOfFirst { it.stringId == favoriteTabs.stringId }
                    favoriteState.eventSink(FavoriteEvent.UpdateTabIndex(index))
                }
            )

            favoriteMap.get(key = FavoriteTab.entries[tabIndex])?.let { pagingItems ->
                if (pagingItems.itemCount == 0) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            modifier = Modifier.testTag(tag = "favoriteMovieEmpty"),
                            text = when (selectedTab) {
                                FavoriteTab.MOVIE -> stringResource(id = R.string.empty_favorite_movie)
                                FavoriteTab.PEOPLE -> stringResource(id = R.string.empty_favorite_people)
                                FavoriteTab.TV -> stringResource(id = R.string.empty_favorite_tv)
                            },
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                } else {
                    FavoriteListComponent(
                        favoriteList = pagingItems,
                        spanCount = 3,
                        content = { media ->
                            when (media) {
                                is People -> {
                                    Column(
                                        modifier = Modifier
                                            .wrapContentSize()
                                            .bounceClick { favoriteState.eventSink(FavoriteEvent.GoTo(favoriteTab = selectedTab, media = media)) }
                                    ) {
                                        Box {
                                            DynamicAsyncImageLoader(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .aspectRatio(ratio = PEOPLE_IMAGE_RATIO)
                                                    .clip(shape = RoundedCornerShape(size = dp10)),
                                                source = media.posterPath ?: "",
                                                contentDescription = "FavoriteImage"
                                            )
                                            FavoriteButtonComponent(
                                                modifier = Modifier
                                                    .wrapContentSize()
                                                    .padding(end = dp5, top = dp5)
                                                    .align(Alignment.TopEnd),
                                                isFavorite = true,
                                                onClick = {
                                                    favoriteState.eventSink(FavoriteEvent.DeleteFavorite(favoriteTab = selectedTab, media = media))
                                                    analyticsHelper.logFavorite(isFavorite = false, contentType = selectedTab.name, media = media)
                                                }
                                            )
                                        }
                                        Text(
                                            modifier = Modifier
                                                .wrapContentWidth()
                                                .padding(top = dp5)
                                                .align(Alignment.CenterHorizontally),
                                            text = media.title ?: "",
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                                else -> {
                                    Box(
                                        modifier = Modifier.bounceClick { favoriteState.eventSink(FavoriteEvent.GoTo(favoriteTab = selectedTab, media = media)) }
                                    ) {
                                        DynamicAsyncImageLoader(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .aspectRatio(ratio = POSTER_IMAGE_RATIO)
                                                .clip(shape = RoundedCornerShape(size = dp10)),
                                            source = media.posterPath ?: "",
                                            contentDescription = "FavoriteMoviePoster"
                                        )
                                        FavoriteButtonComponent(
                                            modifier = Modifier
                                                .wrapContentSize()
                                                .padding(end = dp5, top = dp5)
                                                .align(Alignment.TopEnd),
                                            isFavorite = true,
                                            onClick = {
                                                favoriteState.eventSink(FavoriteEvent.DeleteFavorite(favoriteTab = selectedTab, media = media))
                                                analyticsHelper.logFavorite(isFavorite = false, contentType = selectedTab.name, media = media)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = BOTTOM_NAVIGATION_HEIGHT)
        )
    }
}

@Composable
fun <T : Media> FavoriteListComponent(
    favoriteList: LazyPagingItems<out T>,
    spanCount: Int,
    content: @Composable (T) -> Unit
) {
    val scope = rememberCoroutineScope()
    val lazyGridState = retainedLazyGridListState()
    val visibleItemIndex by remember { derivedStateOf { lazyGridState.firstVisibleItemIndex } }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize()
                .semantics {
                    contentDescription = "favoriteList"
                },
            state = lazyGridState,
            columns = GridCells.Fixed(spanCount),
            contentPadding = PaddingValues(horizontal = dp16),
            horizontalArrangement = Arrangement.spacedBy(space = dp10),
            verticalArrangement = Arrangement.spacedBy(space = dp10)
        ) {
            items(
                count = favoriteList.itemCount
            ) { item ->
                favoriteList[item]?.let { item ->
                    content(item)
                }
            }

            if (favoriteList.loadState.append is LoadState.Loading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        CircularProgressComponent(modifier = Modifier.wrapContentSize().align(alignment = Alignment.Center))
                    }
                }
            }
        }

        if (visibleItemIndex >= spanCount) {
            ScrollToTopComponent(
                onClick = {
                    scope.launch { lazyGridState.scrollToItem(index = 0) }
                }
            )
        }
    }
}

@Composable
fun SegmentedTabs(
    selected: FavoriteTab,
    onSelected: (FavoriteTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = remember { FavoriteTab.entries }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(size = dp999))
            .background(color = MaterialTheme.colorScheme.surfaceVariant)
            .padding(all = dp4),
        horizontalArrangement = Arrangement.spacedBy(space = dp6)
    ) {
        tabs.forEach { tab ->
            val isSelected = tab == selected

            Box(
                modifier = Modifier
                    .weight(weight = 1f)
                    .clip(shape = RoundedCornerShape(size = dp999))
                    .background(
                        color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)
                        else Color.Transparent
                    )
                    .clickable { onSelected(tab) }
                    .padding(vertical = dp10),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = tab.stringId),
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                )
            }
        }
    }
}