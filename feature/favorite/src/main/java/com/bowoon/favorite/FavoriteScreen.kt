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
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bowoon.common.Log
import com.bowoon.data.repository.LocalInitDataComposition
import com.bowoon.data.util.PEOPLE_IMAGE_RATIO
import com.bowoon.data.util.POSTER_IMAGE_RATIO
import com.bowoon.firebase.LocalFirebaseLogHelper
import com.bowoon.model.MovieDetail
import com.bowoon.model.PeopleDetail
import com.bowoon.ui.FavoriteButton
import com.bowoon.ui.Title
import com.bowoon.ui.bounceClick
import com.bowoon.ui.components.TabComponent
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
    val favoriteList = listOf("영화", "인물")
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { favoriteList.size })
    val isDarkMode = LocalInitDataComposition.current.isDarkMode()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Title(title = "찜")
            TabComponent(
                isDarkMode = isDarkMode,
                tabs = favoriteList,
                pagerState = pagerState
            ) {
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
                        when (it[index]) {
                            it[0] -> FavoriteMovieList(
                                favoriteMoviesState = favoriteMoviesState,
                                onMovieClick = onMovieClick,
                                deleteFavoriteMovie = deleteFavoriteMovie,
                                onShowSnackbar = onShowSnackbar
                            )
                            it[1] -> FavoritePeopleList(
                                favoritePeoplesState = favoritePeoplesState,
                                onPeopleClick = onPeopleClick,
                                deleteFavoritePeople = deleteFavoritePeople,
                                onShowSnackbar = onShowSnackbar
                            )
                        }
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
    favoriteMoviesState: FavoriteMoviesState,
    onMovieClick: (Int) -> Unit,
    deleteFavoriteMovie: (MovieDetail) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    val posterUrl = LocalInitDataComposition.current.getImageUrl()
    val scope = rememberCoroutineScope()
    var favoriteMovies by remember { mutableStateOf<List<MovieDetail>>(emptyList()) }

    when (favoriteMoviesState) {
        is FavoriteMoviesState.Loading -> Log.d("favorite movie list loading...")
        is FavoriteMoviesState.Success -> favoriteMovies = favoriteMoviesState.data
        is FavoriteMoviesState.Error -> Log.printStackTrace(favoriteMoviesState.throwable)
    }

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
                    source = "$posterUrl${movieDetail.posterPath}",
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
                            onShowSnackbar("찜에서 제거됐습니다.", null)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun FavoritePeopleList(
    favoritePeoplesState: FavoritePeoplesState,
    onPeopleClick: (Int) -> Unit,
    deleteFavoritePeople: (PeopleDetail) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    val posterUrl = LocalInitDataComposition.current.getImageUrl()
    val scope = rememberCoroutineScope()
    var favoritePeoples by remember { mutableStateOf<List<PeopleDetail>>(emptyList()) }

    when (favoritePeoplesState) {
        is FavoritePeoplesState.Loading -> Log.d("favorite people list loading...")
        is FavoritePeoplesState.Success -> favoritePeoples = favoritePeoplesState.data
        is FavoritePeoplesState.Error -> Log.printStackTrace(favoritePeoplesState.throwable)
    }

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
                        source = "$posterUrl${people.profilePath}",
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
                                onShowSnackbar("찜에서 제거됐습니다.", null)
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