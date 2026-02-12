package com.bowoon.favorite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bowoon.common.Log
import com.bowoon.data.util.PEOPLE_IMAGE_RATIO
import com.bowoon.data.util.POSTER_IMAGE_RATIO
import com.bowoon.firebase.LocalFirebaseLogHelper
import com.bowoon.model.Movie
import com.bowoon.model.People
import com.bowoon.model.Tv
import com.bowoon.movie.feature.favorite.R
import com.bowoon.ui.components.FavoriteButtonComponent
import com.bowoon.ui.components.ScrollToTopComponent
import com.bowoon.ui.components.TabComponent
import com.bowoon.ui.image.DynamicAsyncImageLoader
import com.bowoon.ui.utils.bounceClick
import com.bowoon.ui.utils.dp10
import com.bowoon.ui.utils.dp15
import com.bowoon.ui.utils.dp5
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

    val favoriteMovies by viewModel.favoriteMovies.collectAsStateWithLifecycle()
    val favoriteTvs by viewModel.favoriteTvs.collectAsStateWithLifecycle()
    val favoritePeoples by viewModel.favoritePeoples.collectAsStateWithLifecycle()
    val tabIndex by viewModel.tabIndex.collectAsStateWithLifecycle()

    FavoriteScreen(
        favoriteMovies = favoriteMovies,
        favoriteTvs = favoriteTvs,
        favoritePeoples = favoritePeoples,
        onShowSnackbar = onShowSnackbar,
        initialTab = tabIndex,
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
    favoriteMovies: List<Movie>,
    favoriteTvs: List<Tv>,
    favoritePeoples: List<People>,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    initialTab: Int = 0,
    goToMovie: (Int) -> Unit,
    goToTv: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    updateTabIndex: (Int) -> Unit,
    deleteFavoriteMovie: (Movie) -> Unit,
    deleteFavoriteTv: (Tv) -> Unit,
    deleteFavoritePeople: (People) -> Unit
) {
    val favoriteTabs = FavoriteTab.entries.map { favoriteTab ->
        stringResource(id = favoriteTab.stringId)
    }
    val pagerState = rememberPagerState(initialPage = initialTab, pageCount = { favoriteTabs.size })
    val scope = rememberCoroutineScope()
    val removeFavoriteText = stringResource(id = R.string.remove_favorite)
    val tabClickEvent: (Int, Int) -> Unit = { current, index ->
        scope.launch {
            Log.d("current > $current, index > $index")
            updateTabIndex(index)
            pagerState.animateScrollToPage(page = index)
        }
    }

    LaunchedEffect(key1 = initialTab) {
        if (pagerState.currentPage != initialTab) {
            pagerState.scrollToPage(page = initialTab)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TabComponent(
            tabs = favoriteTabs,
            pagerState = pagerState,
            tabClickEvent = tabClickEvent
        ) { tabList ->
            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = pagerState,
                userScrollEnabled = false
            ) { index ->
                when (tabList[index]) {
                    stringResource(id = R.string.movie) -> {
                        if (favoriteMovies.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    modifier = Modifier.testTag(tag = "favoriteMovieEmpty"),
                                    text = stringResource(id = R.string.empty_favorite_movie)
                                )
                            }
                        } else {
                            FavoriteListComponent<Movie>(
                                favoriteList = favoriteMovies,
                                spanCount = 2,
                                content = { movieDetail ->
                                    Box(
                                        modifier = Modifier.bounceClick { goToMovie(movieDetail.id ?: -1) }
                                    ) {
                                        DynamicAsyncImageLoader(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .aspectRatio(ratio = POSTER_IMAGE_RATIO)
                                                .clip(shape = RoundedCornerShape(size = dp10)),
                                            source = movieDetail.posterPath ?: "",
                                            contentDescription = "FavoriteMoviePoster"
                                        )
                                        FavoriteButtonComponent(
                                            modifier = Modifier
                                                .wrapContentSize()
                                                .align(Alignment.TopEnd),
                                            isFavorite = true,
                                            onClick = {
                                                deleteFavoriteMovie(movieDetail)
                                                scope.launch {
                                                    onShowSnackbar(removeFavoriteText, null)
                                                }
                                            }
                                        )
                                    }
                                }
                            )
                        }
                    }
                    stringResource(id = R.string.tv) -> {
                        if (favoriteMovies.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    modifier = Modifier.testTag(tag = "favoriteMovieEmpty"),
                                    text = stringResource(id = R.string.empty_favorite_movie)
                                )
                            }
                        } else {
                            FavoriteListComponent<Tv>(
                                favoriteList = favoriteTvs,
                                spanCount = 2,
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
                                                .align(Alignment.TopEnd),
                                            isFavorite = true,
                                            onClick = {
                                                deleteFavoriteTv(tv)
                                                scope.launch {
                                                    onShowSnackbar(removeFavoriteText, null)
                                                }
                                            }
                                        )
                                    }
                                }
                            )
                        }
                    }
                    stringResource(id = R.string.people) -> {
                        if (favoritePeoples.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    modifier = Modifier.testTag(tag = "favoriteMovieEmpty"),
                                    text = stringResource(id = R.string.empty_favorite_people)
                                )
                            }
                        } else {
                            FavoriteListComponent<People>(
                                favoriteList = favoritePeoples,
                                spanCount = 3,
                                content = { peopleDetail ->
                                    Column(
                                        modifier = Modifier
                                            .wrapContentSize()
                                            .bounceClick { goToPeople(peopleDetail.id ?: -1) }
                                    ) {
                                        Box {
                                            DynamicAsyncImageLoader(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .aspectRatio(ratio = PEOPLE_IMAGE_RATIO)
                                                    .clip(shape = RoundedCornerShape(size = dp10)),
                                                source = peopleDetail.posterPath ?: "",
                                                contentDescription = "FavoritePeopleProfileImage"
                                            )
                                            FavoriteButtonComponent(
                                                modifier = Modifier
                                                    .wrapContentSize()
                                                    .align(Alignment.TopEnd),
                                                isFavorite = true,
                                                onClick = {
                                                    deleteFavoritePeople(peopleDetail)
                                                    scope.launch {
                                                        onShowSnackbar(removeFavoriteText, null)
                                                    }
                                                }
                                            )
                                        }
                                        Text(
                                            modifier = Modifier.wrapContentWidth().padding(top = dp5).align(Alignment.CenterHorizontally),
                                            text = peopleDetail.title ?: "",
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
    }
}

@Composable
fun <T> FavoriteListComponent(
    favoriteList: List<T>,
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
            modifier = Modifier.fillMaxSize().semantics {
                contentDescription = "favoriteList"
            },
            state = lazyGridState,
            columns = GridCells.Fixed(spanCount),
            contentPadding = PaddingValues(all = dp15),
            horizontalArrangement = Arrangement.spacedBy(space = dp10),
            verticalArrangement = Arrangement.spacedBy(space = dp10)
        ) {
            items(
                items = favoriteList
            ) { item -> content(item) }
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