package com.cheeke.surfy.favorite.impl

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
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.cheeke.surfy.analytics.api.LocalAnalyticsHelper
import com.cheeke.surfy.analytics.api.TrackScreenViewEvent
import com.cheeke.surfy.analytics.api.logFavorite
import com.cheeke.surfy.common.PEOPLE_IMAGE_RATIO
import com.cheeke.surfy.common.POSTER_IMAGE_RATIO
import com.cheeke.surfy.common.ScrollToTop
import com.cheeke.surfy.common.ScrollTopEvent
import com.cheeke.surfy.favorite.api.FavoriteContentType
import com.cheeke.surfy.feature.favorite.impl.R
import com.cheeke.surfy.firebase.LocalFirebaseLogHelper
import com.cheeke.surfy.model.Media
import com.cheeke.surfy.model.People
import com.cheeke.surfy.ui.components.CircularProgressComponent
import com.cheeke.surfy.ui.components.FavoriteButtonComponent
import com.cheeke.surfy.ui.components.ScrollToTopComponent
import com.cheeke.surfy.ui.image.DynamicAsyncImageLoader
import com.cheeke.surfy.ui.utils.bounceClick
import com.cheeke.surfy.ui.utils.dp10
import com.cheeke.surfy.ui.utils.dp16
import com.cheeke.surfy.ui.utils.dp4
import com.cheeke.surfy.ui.utils.dp5
import com.cheeke.surfy.ui.utils.dp6
import com.cheeke.surfy.ui.utils.dp999
import kotlinx.coroutines.launch

@Composable
fun FavoriteScreen(
    goToMovie: (Int) -> Unit,
    goToTv: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    viewModel: FavoriteVM = hiltViewModel()
) {
    LocalFirebaseLogHelper.current.sendLog("FavoriteScreen", "favorite screen init")
    TrackScreenViewEvent(screenName = "FavoriteScreen")

    val selectedTab by viewModel.currentTab.collectAsStateWithLifecycle()

    FavoriteScreen(
        tabList = viewModel.tabList,
        selectedTab = viewModel.repositories.getValue(key = selectedTab),
        favoritePagingItems = viewModel.currentPagingItems.collectAsLazyPagingItems(),
        onShowSnackbar = onShowSnackbar,
        updateTabKey = viewModel::updateTabKey,
        goTo = { favoriteTab, media ->
            when (favoriteTab) {
                is FavoriteMovieRepository -> goToMovie(media.id ?: -1)
                is FavoritePeopleRepository -> goToPeople(media.id ?: -1)
                is FavoriteTvRepository -> goToTv(media.id ?: -1)
            }
        }
    )
}

@Composable
fun FavoriteScreen(
    tabList: List<FavoriteTabUiModel>,
    selectedTab: FavoriteRepository,
    favoritePagingItems: LazyPagingItems<out Media>,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    updateTabKey: (FavoriteContentType) -> Unit,
    goTo: (FavoriteRepository, Media) -> Unit
) {
    val scope = rememberCoroutineScope()
    val removeFavoriteText = stringResource(id = R.string.remove_favorite)
    val analyticsHelper = LocalAnalyticsHelper.current
    val label = stringResource(id = tabList.first { it.type == selectedTab.key }.titleRes)

    Column(modifier = Modifier.fillMaxSize()) {
        SegmentedTabs(
            tabList = tabList,
            selected = selectedTab,
            onSelected = { selected -> updateTabKey(selected.type) }
        )

        if (favoritePagingItems.itemCount == 0) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier.semantics { contentDescription = "favoriteEmpty" },
                    text = when (selectedTab) {
                        is FavoriteMovieRepository -> stringResource(id = R.string.empty_favorite_movie)
                        is FavoritePeopleRepository -> stringResource(id = R.string.empty_favorite_people)
                        is FavoriteTvRepository -> stringResource(id = R.string.empty_favorite_tv)
                        else -> ""
                    },
                    style = MaterialTheme.typography.titleLarge
                )
            }
        } else {
            FavoriteListComponent(
                favoriteList = favoritePagingItems,
                spanCount = 3,
                content = { media ->
                    when (media) {
                        is People -> {
                            Column(
                                modifier = Modifier
                                    .wrapContentSize()
                                    .bounceClick { goTo(selectedTab, media) }
                            ) {
                                Box {
                                    DynamicAsyncImageLoader(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(ratio = PEOPLE_IMAGE_RATIO)
                                            .clip(shape = RoundedCornerShape(size = dp10)),
                                        source = media.posterPath.orEmpty(),
                                        contentDescription = "FavoriteImage"
                                    )
                                    FavoriteButtonComponent(
                                        modifier = Modifier
                                            .semantics { contentDescription = "favoriteButton" }
                                            .wrapContentSize()
                                            .padding(end = dp5, top = dp5)
                                            .align(Alignment.TopEnd),
                                        isFavorite = true,
                                        onClick = {
                                            scope.launch {
                                                selectedTab.delete(media = media)
                                                onShowSnackbar(removeFavoriteText, null)
                                            }
                                            analyticsHelper.logFavorite(isFavorite = false, contentType = "people", id = media.id ?: -1, title = media.title.orEmpty())
                                        }
                                    )
                                }
                                Text(
                                    modifier = Modifier
                                        .wrapContentWidth()
                                        .padding(top = dp5)
                                        .align(Alignment.CenterHorizontally),
                                    text = media.title.orEmpty(),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        else -> {
                            Box(
                                modifier = Modifier.bounceClick { goTo(selectedTab, media) }
                            ) {
                                DynamicAsyncImageLoader(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(ratio = POSTER_IMAGE_RATIO)
                                        .clip(shape = RoundedCornerShape(size = dp10)),
                                    source = media.posterPath.orEmpty(),
                                    contentDescription = "FavoriteImage"
                                )
                                FavoriteButtonComponent(
                                    modifier = Modifier
                                        .semantics { contentDescription = "favoriteButton" }
                                        .wrapContentSize()
                                        .padding(end = dp5, top = dp5)
                                        .align(Alignment.TopEnd),
                                    isFavorite = true,
                                    onClick = {
                                        scope.launch {
                                            selectedTab.delete(media = media)
                                            onShowSnackbar(removeFavoriteText, null)
                                        }
                                        analyticsHelper.logFavorite(isFavorite = false, contentType = label, id = media.id ?: -1, title = media.title.orEmpty())
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

@Composable
fun <T : Media> FavoriteListComponent(
    favoriteList: LazyPagingItems<T>,
    spanCount: Int,
    content: @Composable (T) -> Unit
) {
    val scope = rememberCoroutineScope()
    val lazyGridState = rememberLazyGridState()
    val visibleItemIndex by remember { derivedStateOf { lazyGridState.firstVisibleItemIndex } }

    lazyGridState.ScrollToTop(event = ScrollTopEvent.Favorite)

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
                count = favoriteList.itemCount,
                key = { index -> favoriteList[index]?.id ?: index }
            ) {
                content(favoriteList[it] ?: return@items)
            }

            if (favoriteList.loadState.append is LoadState.Loading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        CircularProgressComponent(
                            modifier = Modifier
                                .wrapContentSize()
                                .align(alignment = Alignment.Center)
                        )
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
    tabList: List<FavoriteTabUiModel>,
    selected: FavoriteRepository,
    onSelected: (FavoriteTabUiModel) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = dp16, end = dp16, bottom = dp10)
            .clip(shape = RoundedCornerShape(size = dp999))
            .background(color = MaterialTheme.colorScheme.surfaceVariant)
            .padding(all = dp4),
        horizontalArrangement = Arrangement.spacedBy(space = dp6)
    ) {
        tabList.forEach { tab ->
            val isSelected = tab.type == selected.key

            Box(
                modifier = Modifier
                    .weight(weight = 1f)
                    .clip(shape = RoundedCornerShape(size = dp999))
                    .background(
                        color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.22f) else Color.Transparent
                    )
                    .clickable { onSelected(tab) }
                    .padding(vertical = dp10),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = tab.titleRes),
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                )
            }
        }
    }
}