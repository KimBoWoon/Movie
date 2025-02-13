package com.bowoon.favorite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bowoon.common.Log
import com.bowoon.data.util.PEOPLE_IMAGE_RATIO
import com.bowoon.data.util.POSTER_IMAGE_RATIO
import com.bowoon.firebase.LocalFirebaseLogHelper
import com.bowoon.model.MovieDetail
import com.bowoon.model.PeopleDetail
import com.bowoon.ui.FavoriteButton
import com.bowoon.ui.Title
import com.bowoon.ui.bounceClick
import com.bowoon.ui.dp10
import com.bowoon.ui.dp120
import com.bowoon.ui.dp15
import com.bowoon.ui.dp200
import com.bowoon.ui.dp5
import com.bowoon.ui.image.DynamicAsyncImageLoader
import kotlinx.coroutines.launch

@Composable
fun FavoriteScreen(
    onMovieClick: (Int) -> Unit,
    onPeopleClick: (Int) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    viewModel: FavoriteVM = hiltViewModel()
) {
    LocalFirebaseLogHelper.current.sendLog("FavoriteScreen", "favorite screen init")

    val favoriteMoviesState by viewModel.favoriteMovies.collectAsStateWithLifecycle()
    val favoritePeoplesState by viewModel.favoritePeoples.collectAsStateWithLifecycle()

    FavoriteScreen(
        favoriteMoviesState = favoriteMoviesState,
        favoritePeoplesState = favoritePeoplesState,
        onMovieClick = onMovieClick,
        onPeopleClick = onPeopleClick,
        onShowSnackbar = onShowSnackbar,
        deleteFavoriteMovie = viewModel::deleteMovie,
        deleteFavoritePeople = viewModel::deletePeople
    )
}

@Composable
fun FavoriteScreen(
    favoriteMoviesState: FavoriteMoviesState,
    favoritePeoplesState: FavoritePeoplesState,
    onMovieClick: (Int) -> Unit,
    onPeopleClick: (Int) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    deleteFavoriteMovie: (MovieDetail) -> Unit,
    deleteFavoritePeople: (PeopleDetail) -> Unit
) {
    val isLoading = favoriteMoviesState is FavoriteMoviesState.Loading
    var favoriteMovies by remember { mutableStateOf<List<MovieDetail>>(emptyList()) }
    var favoritePeoples by remember { mutableStateOf<List<PeopleDetail>>(emptyList()) }
    val favoriteList = listOf("영화", "인물")
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { favoriteList.size })
    val scope = rememberCoroutineScope()

    when (favoriteMoviesState) {
        is FavoriteMoviesState.Loading -> Log.d("favorite movie list loading...")
        is FavoriteMoviesState.Success -> favoriteMovies = favoriteMoviesState.data
        is FavoriteMoviesState.Error -> Log.printStackTrace(favoriteMoviesState.throwable)
    }

    when (favoritePeoplesState) {
        is FavoritePeoplesState.Loading -> Log.d("favorite people list loading...")
        is FavoritePeoplesState.Success -> favoritePeoples = favoritePeoplesState.data
        is FavoritePeoplesState.Error -> Log.printStackTrace(favoritePeoplesState.throwable)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Title(title = "즐겨찾기")
            TabRow(
                modifier = Modifier.fillMaxWidth(),
                selectedTabIndex = pagerState.currentPage
            ) {
                favoriteList.forEachIndexed { index, label ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            Log.d("selected tab index > $index")
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = { Text(text = label) },
                        selectedContentColor = Color(0xFF7C86DF),
                        unselectedContentColor = Color.LightGray
                    )
                }
            }

            HorizontalPager(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                state = pagerState,
                userScrollEnabled = false
            ) { index ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (favoriteList[index]) {
                        "영화" -> FavoriteMovieList(
                            favoriteMovies = favoriteMovies,
                            onMovieClick = onMovieClick,
                            deleteFavoriteMovie = deleteFavoriteMovie,
                            onShowSnackbar = onShowSnackbar
                        )
                        "인물" -> FavoritePeopleList(
                            favoritePeoples = favoritePeoples,
                            onPeopleClick = onPeopleClick,
                            deleteFavoritePeople = deleteFavoritePeople,
                            onShowSnackbar = onShowSnackbar
                        )
                    }
                }
            }
        }

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun FavoriteMovieList(
    favoriteMovies: List<MovieDetail>,
    onMovieClick: (Int) -> Unit,
    deleteFavoriteMovie: (MovieDetail) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    val scope = rememberCoroutineScope()

    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(dp15),
        horizontalArrangement = Arrangement.spacedBy(dp10),
        verticalArrangement = Arrangement.spacedBy(dp10)
    ) {
        items(
            items = favoriteMovies
        ) { movieDetail ->
            Box(
                modifier = Modifier.bounceClick { onMovieClick(movieDetail.id ?: -1) }
            ) {
                DynamicAsyncImageLoader(
                    modifier = Modifier
                        .width(dp200)
                        .aspectRatio(POSTER_IMAGE_RATIO),
                    source = "${movieDetail.posterUrl}${movieDetail.posterPath}",
                    contentDescription = "BoxOfficePoster"
                )
                FavoriteButton(
                    modifier = Modifier
                        .wrapContentSize()
                        .align(Alignment.TopEnd),
                    isFavorite = favoriteMovies.contains(movieDetail),
                    onClick = {
                        deleteFavoriteMovie(movieDetail)
                        scope.launch {
                            onShowSnackbar("즐겨찾기에서 제거됐습니다.", null)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun FavoritePeopleList(
    favoritePeoples: List<PeopleDetail>,
    onPeopleClick: (Int) -> Unit,
    deleteFavoritePeople: (PeopleDetail) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    val scope = rememberCoroutineScope()

    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Adaptive(dp120),
        contentPadding = PaddingValues(dp10),
        verticalArrangement = Arrangement.spacedBy(dp10),
        horizontalArrangement = Arrangement.spacedBy(dp10)
    ) {
        items(
            items = favoritePeoples,
            key = { "${it.id}_${it.name}" }
        ) { people ->
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .bounceClick { onPeopleClick(people.id ?: -1) }
            ) {
                Box() {
                    DynamicAsyncImageLoader(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(PEOPLE_IMAGE_RATIO)
                            .clip(RoundedCornerShape(dp10)),
                        source = "${people.posterUrl}${people.profilePath}",
                        contentDescription = "BoxOfficePoster"
                    )
                    FavoriteButton(
                        modifier = Modifier
                            .wrapContentSize()
                            .align(Alignment.TopEnd),
                        isFavorite = favoritePeoples.contains(people),
                        onClick = {
                            deleteFavoritePeople(people)
                            scope.launch {
                                onShowSnackbar("즐겨찾기에서 제거됐습니다.", null)
                            }
                        }
                    )
                }
                Text(
                    modifier = Modifier.wrapContentWidth().padding(top = dp5).align(Alignment.CenterHorizontally),
                    text = people.name ?: "",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}