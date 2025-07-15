package com.bowoon.favorite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bowoon.common.Log
import com.bowoon.data.util.PEOPLE_IMAGE_RATIO
import com.bowoon.data.util.POSTER_IMAGE_RATIO
import com.bowoon.firebase.LocalFirebaseLogHelper
import com.bowoon.model.Favorite
import com.bowoon.movie.feature.favorite.R
import com.bowoon.ui.FavoriteButton
import com.bowoon.ui.components.ScrollToTopComponent
import com.bowoon.ui.components.TabComponent
import com.bowoon.ui.components.TitleComponent
import com.bowoon.ui.image.DynamicAsyncImageLoader
import com.bowoon.ui.utils.bounceClick
import com.bowoon.ui.utils.dp10
import com.bowoon.ui.utils.dp15
import com.bowoon.ui.utils.dp5
import kotlinx.coroutines.launch

@Composable
fun FavoriteScreen(
    goToMovie: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    viewModel: FavoriteVM = hiltViewModel()
) {
    LocalFirebaseLogHelper.current.sendLog("FavoriteScreen", "favorite screen init")

    val favoriteMovies by viewModel.favoriteMovies.collectAsStateWithLifecycle()
    val favoritePeoples by viewModel.favoritePeoples.collectAsStateWithLifecycle()

    FavoriteScreen(
        favoriteMovies = favoriteMovies,
        favoritePeoples = favoritePeoples,
        onShowSnackbar = onShowSnackbar,
        goToMovie = goToMovie,
        goToPeople = goToPeople,
        deleteFavoriteMovie = viewModel::deleteMovie,
        deleteFavoritePeople = viewModel::deletePeople
    )
}

@Composable
fun FavoriteScreen(
    favoriteMovies: List<Favorite>,
    favoritePeoples: List<Favorite>,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    goToMovie: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    deleteFavoriteMovie: (Favorite) -> Unit,
    deleteFavoritePeople: (Favorite) -> Unit
) {
    val favoriteTabs = listOf(
        stringResource(id = R.string.movie),
        stringResource(id = R.string.people)
    )
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { favoriteTabs.size })
    val scope = rememberCoroutineScope()
    val removeFavoriteText = stringResource(id = R.string.remove_favorite)
    val tabClickEvent: (Int, Int) -> Unit = { current, index ->
        scope.launch {
            Log.d("current > $current, index > $index")
            pagerState.animateScrollToPage(index)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TitleComponent(title = stringResource(id = R.string.feature_favorite_name))
        TabComponent(
            tabs = favoriteTabs,
            pagerState = pagerState,
            tabClickEvent = tabClickEvent
        ) { tabList ->
            HorizontalPager(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                state = pagerState,
                userScrollEnabled = false
            ) { index ->
                when (tabList[index]) {
                    FavoriteVM.FavoriteTabs.MOVIE.label -> {
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            if (favoriteMovies.isEmpty()) {
                                Text(
                                    modifier = Modifier.testTag(tag = "favoriteMovieEmpty").align(Alignment.Center),
                                    text = stringResource(id = R.string.empty_favorite_movie)
                                )
                            } else {
                                FavoriteListComponent(
                                    favoriteList = favoriteMovies,
                                    spanCount = 2,
                                    content = { movieDetail ->
                                        Box(
                                            modifier = Modifier.bounceClick { goToMovie(movieDetail.id ?: -1) }
                                        ) {
                                            DynamicAsyncImageLoader(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .aspectRatio(POSTER_IMAGE_RATIO),
                                                source = movieDetail.imagePath ?: "",
                                                contentDescription = "FavoriteMoviePoster"
                                            )
                                            FavoriteButton(
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
                    }
                    FavoriteVM.FavoriteTabs.PEOPLE.label -> {
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            if (favoritePeoples.isEmpty()) {
                                Text(
                                    modifier = Modifier.testTag(tag = "favoritePeopleEmpty").align(Alignment.Center),
                                    text = stringResource(id = R.string.empty_favorite_people)
                                )
                            } else {
                                FavoriteListComponent(
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
                                                        .aspectRatio(PEOPLE_IMAGE_RATIO)
                                                        .clip(RoundedCornerShape(dp10)),
                                                    source = peopleDetail.imagePath ?: "",
                                                    contentDescription = "FavoritePeopleProfileImage"
                                                )
                                                FavoriteButton(
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
}

@Composable
fun FavoriteListComponent(
    favoriteList: List<Favorite>,
    spanCount: Int,
    content: @Composable (Favorite) -> Unit
) {
    val scope = rememberCoroutineScope()
    val lazyGridState = rememberLazyGridState()
    val visibleItemIndex by remember { derivedStateOf { lazyGridState.firstVisibleItemIndex } }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            state = lazyGridState,
            columns = GridCells.Fixed(spanCount),
            contentPadding = PaddingValues(dp15),
            horizontalArrangement = Arrangement.spacedBy(dp10),
            verticalArrangement = Arrangement.spacedBy(dp10)
        ) {
            items(
                items = favoriteList
            ) { item -> content(item) }
        }

        if (visibleItemIndex >= spanCount) {
            ScrollToTopComponent(
                onClick = {
                    scope.launch { lazyGridState.scrollToItem(0) }
                }
            )
        }
    }
}