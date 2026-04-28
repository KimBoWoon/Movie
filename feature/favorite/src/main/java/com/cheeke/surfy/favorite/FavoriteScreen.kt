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
import androidx.compose.ui.platform.testTag
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
import com.cheeke.surfy.analytics.LocalAnalyticsHelper
import com.cheeke.surfy.analytics.TrackScreenViewEvent
import com.cheeke.surfy.analytics.logFavorite
import com.cheeke.surfy.data.util.PEOPLE_IMAGE_RATIO
import com.cheeke.surfy.data.util.POSTER_IMAGE_RATIO
import com.cheeke.surfy.feature.favorite.R
import com.cheeke.surfy.firebase.LocalFirebaseLogHelper
import com.cheeke.surfy.model.Media
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.model.People
import com.cheeke.surfy.model.Tv
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

    val favoriteMovies = viewModel.favoriteMovies.collectAsLazyPagingItems()
    val favoriteTvs = viewModel.favoriteTvs.collectAsLazyPagingItems()
    val favoritePeoples = viewModel.favoritePeoples.collectAsLazyPagingItems()
    val tabIndex by viewModel.tabIndex.collectAsStateWithLifecycle()

    FavoriteScreen(
        favoriteMovies = favoriteMovies,
        favoriteTvs = favoriteTvs,
        favoritePeoples = favoritePeoples,
        onShowSnackbar = onShowSnackbar,
        tabIndex = tabIndex,
        goToMovie = goToMovie,
        goToTv = goToTv,
        goToPeople = goToPeople,
        updateTabIndex = viewModel::updateTabIndex,
        deleteFavoriteMovie = viewModel::deleteMovie,
        deleteFavoriteTv = viewModel::deleteTv,
        deleteFavoritePeople = viewModel::deletePeople
    )
}

@Composable
fun FavoriteScreen(
    favoriteMovies: LazyPagingItems<Movie>,
    favoriteTvs: LazyPagingItems<Tv>,
    favoritePeoples: LazyPagingItems<People>,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    tabIndex: Int = 0,
    goToMovie: (Int) -> Unit,
    goToTv: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    updateTabIndex: (Int) -> Unit,
    deleteFavoriteMovie: (Movie) -> Unit,
    deleteFavoriteTv: (Tv) -> Unit,
    deleteFavoritePeople: (People) -> Unit
) {
    val scope = rememberCoroutineScope()
    val removeFavoriteText = stringResource(id = R.string.remove_favorite)
    val analyticsHelper = LocalAnalyticsHelper.current

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SegmentedTabs(
            modifier = Modifier.fillMaxWidth().padding(start = dp16, end = dp16, bottom = dp10),
            selected = FavoriteTab.entries[tabIndex],
            onSelected = { favoriteTabs ->
                val index = FavoriteTab.entries.indexOfFirst { it.stringId == favoriteTabs.stringId }
                updateTabIndex(index)
            }
        )

        when (FavoriteTab.entries[tabIndex]) {
            FavoriteTab.MOVIE -> {
                if (favoriteMovies.itemCount == 0) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            modifier = Modifier.testTag(tag = "favoriteMovieEmpty"),
                            text = stringResource(id = R.string.empty_favorite_movie),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                } else {
                    FavoriteListComponent<Movie>(
                        favoriteList = favoriteMovies,
                        spanCount = 3,
                        content = { movie ->
                            Box(
                                modifier = Modifier.bounceClick { goToMovie(movie.id ?: -1) }
                            ) {
                                DynamicAsyncImageLoader(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(ratio = POSTER_IMAGE_RATIO)
                                        .clip(shape = RoundedCornerShape(size = dp10)),
                                    source = movie.posterPath ?: "",
                                    contentDescription = "FavoriteMoviePoster"
                                )
                                FavoriteButtonComponent(
                                    modifier = Modifier
                                        .wrapContentSize()
                                        .padding(end = dp5, top = dp5)
                                        .align(Alignment.TopEnd),
                                    isFavorite = true,
                                    onClick = {
                                        deleteFavoriteMovie(movie)
                                        scope.launch {
                                            onShowSnackbar(removeFavoriteText, null)
                                        }
                                        analyticsHelper.logFavorite(isFavorite = false, contentType = "movie", media = movie)
                                    }
                                )
                            }
                        }
                    )
                }
            }
            FavoriteTab.TV -> {
                if (favoriteTvs.itemCount == 0) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            modifier = Modifier.testTag(tag = "favoriteTvEmpty"),
                            text = stringResource(id = R.string.empty_favorite_tv),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                } else {
                    FavoriteListComponent<Tv>(
                        favoriteList = favoriteTvs,
                        spanCount = 3,
                        content = { tv ->
                            Box(
                                modifier = Modifier.bounceClick { goToTv(tv.id ?: -1) }
                            ) {
                                DynamicAsyncImageLoader(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(ratio = POSTER_IMAGE_RATIO)
                                        .clip(shape = RoundedCornerShape(size = dp10)),
                                    source = tv.posterPath ?: "",
                                    contentDescription = "FavoriteTvPoster"
                                )
                                FavoriteButtonComponent(
                                    modifier = Modifier
                                        .wrapContentSize()
                                        .padding(end = dp5, top = dp5)
                                        .align(Alignment.TopEnd),
                                    isFavorite = true,
                                    onClick = {
                                        deleteFavoriteTv(tv)
                                        scope.launch {
                                            onShowSnackbar(removeFavoriteText, null)
                                        }
                                        analyticsHelper.logFavorite(isFavorite = false, contentType = "tv", media = tv)
                                    }
                                )
                            }
                        }
                    )
                }
            }
            FavoriteTab.PEOPLE -> {
                if (favoritePeoples.itemCount == 0) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            modifier = Modifier.testTag(tag = "favoritePeopleEmpty"),
                            text = stringResource(id = R.string.empty_favorite_people),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                } else {
                    FavoriteListComponent<People>(
                        favoriteList = favoritePeoples,
                        spanCount = 3,
                        content = { people ->
                            Column(
                                modifier = Modifier
                                    .wrapContentSize()
                                    .bounceClick { goToPeople(people.id ?: -1) }
                            ) {
                                Box {
                                    DynamicAsyncImageLoader(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(ratio = PEOPLE_IMAGE_RATIO)
                                            .clip(shape = RoundedCornerShape(size = dp10)),
                                        source = people.posterPath ?: "",
                                        contentDescription = "FavoritePeopleProfileImage"
                                    )
                                    FavoriteButtonComponent(
                                        modifier = Modifier
                                            .wrapContentSize()
                                            .padding(end = dp5, top = dp5)
                                            .align(Alignment.TopEnd),
                                        isFavorite = true,
                                        onClick = {
                                            deleteFavoritePeople(people)
                                            scope.launch {
                                                onShowSnackbar(removeFavoriteText, null)
                                            }
                                            analyticsHelper.logFavorite(isFavorite = false, contentType = "people", media = people)
                                        }
                                    )
                                }
                                Text(
                                    modifier = Modifier
                                        .wrapContentWidth()
                                        .padding(top = dp5)
                                        .align(Alignment.CenterHorizontally),
                                    text = people.title ?: "",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    )
                }
            }
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
            ) {
                content(favoriteList[it] ?: return@items)
            }

            if (favoriteList.loadState.append is LoadState.Loading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        CircularProgressComponent(
                            modifier = Modifier.wrapContentSize().align(alignment = Alignment.Center)
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