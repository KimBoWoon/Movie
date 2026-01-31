package com.bowoon.home

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.bowoon.common.Log
import com.bowoon.data.util.POSTER_IMAGE_RATIO
import com.bowoon.firebase.LocalFirebaseLogHelper
import com.bowoon.model.Movie
import com.bowoon.movie.feature.home.R
import com.bowoon.ui.components.CircularProgressComponent
import com.bowoon.ui.image.DynamicAsyncImageLoader
import com.bowoon.ui.utils.bounceClick
import com.bowoon.ui.utils.dp10
import com.bowoon.ui.utils.dp150
import com.bowoon.ui.utils.dp16
import com.bowoon.ui.utils.sp10
import com.bowoon.ui.utils.sp8

@Composable
fun HomeScreen(
    goToMovie: (Int) -> Unit,
    viewModel: HomeVM = hiltViewModel()
) {
    LocalFirebaseLogHelper.current.sendLog("HomeScreen", "init screen")

    val mainMenuState by viewModel.mainMenu.collectAsStateWithLifecycle()

    HomeScreen(
        mainMenuState = mainMenuState,
        goToMovie = goToMovie,
    )
}

@Composable
fun HomeScreen(
    mainMenuState: MainMenuState,
    goToMovie: (Int) -> Unit,
) {
    LocalFirebaseLogHelper.current.sendLog("HomeScreen", "init screen")

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (mainMenuState) {
            is MainMenuState.Loading -> {
                Log.d("loading...")
                LocalFirebaseLogHelper.current.sendLog("HomeScreen", "data loading...")

                CircularProgressComponent(
                    modifier = Modifier
                        .semantics { contentDescription = "homeLoading" }
                        .align(Alignment.Center)
                )
            }
            is MainMenuState.Success -> {
                LocalFirebaseLogHelper.current.sendLog("HomeScreen", "data load success")
                Log.d("${mainMenuState.nowPlayingMoviePager}, ${mainMenuState.upComingMoviePager}")

                MainComponent(
                    nowPlayingMovies = mainMenuState.nowPlayingMoviePager.collectAsLazyPagingItems(),
                    upComingMovies = mainMenuState.upComingMoviePager.collectAsLazyPagingItems(),
                    goToMovie = goToMovie
                )
            }
            is MainMenuState.Error -> {
                LocalFirebaseLogHelper.current.sendLog("HomeScreen", "data load Error > ${mainMenuState.throwable.message}")
                Log.e("${mainMenuState.throwable.message}")
                Text(
                    modifier = Modifier.fillMaxSize(),
                    text = mainMenuState.throwable.message ?: stringResource(id = com.bowoon.movie.core.network.R.string.something_wrong)
                )
            }
        }
    }
}

@Composable
fun MainComponent(
    nowPlayingMovies: LazyPagingItems<Movie>,
    upComingMovies: LazyPagingItems<Movie>,
    goToMovie: (Int) -> Unit
) {
    val lazyListState = rememberLazyListState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        val nowPlayingMoviesTitle = stringResource(id = R.string.now_playing_movies)
        val upcomingMoviesTitle = stringResource(id = R.string.upcoming_movies)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lazyListState
        ) {
            if (nowPlayingMovies.itemCount != 0) {
                horizontalMovieListComponent(
                    title = nowPlayingMoviesTitle,
                    pager = nowPlayingMovies,
                    goToMovie = goToMovie
                )
            }
            if (upComingMovies.itemCount != 0) {
                horizontalMovieListComponent(
                    title = upcomingMoviesTitle,
                    pager = upComingMovies,
                    goToMovie = goToMovie
                )
            }
        }
    }
}

fun LazyListScope.horizontalMovieListComponent(
    title: String,
    pager: LazyPagingItems<Movie>,
    goToMovie: (Int) -> Unit
) {
    item {
        Text(
            modifier = Modifier
                .padding(all = dp16)
                .fillMaxWidth(),
            text = title
        )
        LazyRow(
            modifier = Modifier
                .semantics {
                    contentDescription = if (title == "상영중인 영화") "nowPlayingMovies" else "upComingMovies"
                }.wrapContentSize(),
            contentPadding = PaddingValues(horizontal = dp16),
            horizontalArrangement = Arrangement.spacedBy(space = dp16)
        ) {
            items(
                count = pager.itemCount,
                key = { index -> "${pager.peek(index)?.id}_${index}_${pager.peek(index)?.title}" }
            ) { index ->
                pager[index]?.let {
                    MainMovieItem(
                        movie = it,
                        goToMovie = goToMovie
                    )
                }
            }
        }
    }
}

@Composable
fun MainMovieItem(
    movie: Movie,
    goToMovie: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .width(width = dp150)
            .wrapContentHeight()
            .bounceClick { goToMovie(movie.id ?: -1) }
    ) {
        Box(
            modifier = Modifier.wrapContentSize()
        ) {
            DynamicAsyncImageLoader(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(POSTER_IMAGE_RATIO)
                    .clip(shape = RoundedCornerShape(size = dp10)),
                source = movie.posterPath ?: "",
                contentDescription = "BoxOfficePoster"
            )
        }
        Text(
            text = movie.title ?: "",
            fontSize = sp10,
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = movie.releaseDate ?: "",
            fontSize = sp8,
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}