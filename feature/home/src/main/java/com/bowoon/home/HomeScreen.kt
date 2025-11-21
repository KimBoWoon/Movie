package com.bowoon.home

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bowoon.common.Log
import com.bowoon.data.util.POSTER_IMAGE_RATIO
import com.bowoon.firebase.LocalFirebaseLogHelper
import com.bowoon.model.MainMenu
import com.bowoon.model.Movie
import com.bowoon.movie.feature.home.R
import com.bowoon.ui.components.CircularProgressComponent
import com.bowoon.ui.dialog.Indexer
import com.bowoon.ui.image.DynamicAsyncImageLoader
import com.bowoon.ui.utils.bounceClick
import com.bowoon.ui.utils.dp10
import com.bowoon.ui.utils.dp150
import com.bowoon.ui.utils.dp16
import com.bowoon.ui.utils.dp20
import com.bowoon.ui.utils.dp300
import com.bowoon.ui.utils.sp10
import com.bowoon.ui.utils.sp20
import com.bowoon.ui.utils.sp8

@Composable
fun HomeScreen(
    goToMovie: (Int) -> Unit,
    viewModel: HomeVM = hiltViewModel()
) {
    LocalFirebaseLogHelper.current.sendLog("HomeScreen", "init screen")

    val homeUiState by viewModel.mainMenu.collectAsStateWithLifecycle()
    val isShowNextWeekReleaseMovie = remember { viewModel.isShowNextWeekReleaseMovie }

    HomeScreen(
        mainMenuState = homeUiState,
        isShowNextWeekReleaseMovie = isShowNextWeekReleaseMovie,
        goToMovie = goToMovie,
        onNoShowToday = viewModel::onNoShowToday
    )
}

@Composable
fun HomeScreen(
    mainMenuState: MainMenuState,
    isShowNextWeekReleaseMovie: MutableState<Boolean>,
    goToMovie: (Int) -> Unit,
    onNoShowToday: () -> Unit
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
                Log.d("${mainMenuState.mainMenu}")

                MainComponent(
                    mainMenu = mainMenuState.mainMenu,
                    goToMovie = goToMovie
                )

                if (!isShowNextWeekReleaseMovie.value) {
                    ReleaseMoviesDialog(
                        onNoShowToday = onNoShowToday,
                        onDismiss = { isShowNextWeekReleaseMovie.value = true },
                        releaseMovies = mainMenuState.mainMenu.nextWeekReleaseMovies,
                        goToMovie = goToMovie
                    )
                }
            }
            is MainMenuState.Error -> {
                LocalFirebaseLogHelper.current.sendLog("HomeScreen", "data load Error > ${mainMenuState.throwable.message}")
                Log.e("${mainMenuState.throwable.message}")
            }
        }
    }
}

@Composable
fun MainComponent(
    mainMenu: MainMenu,
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
            if (mainMenu.nowPlayingMovies.isNotEmpty()) {
                horizontalMovieListComponent(
                    title = nowPlayingMoviesTitle,
                    movies = mainMenu.nowPlayingMovies,
                    goToMovie = goToMovie
                )
            }
            if (mainMenu.upComingMovies.isNotEmpty()) {
                horizontalMovieListComponent(
                    title = upcomingMoviesTitle,
                    movies = mainMenu.upComingMovies,
                    goToMovie = goToMovie
                )
            }
        }
    }
}

fun LazyListScope.horizontalMovieListComponent(
    title: String,
    movies: List<Movie>,
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
            modifier = Modifier.wrapContentSize(),
            contentPadding = PaddingValues(horizontal = dp16),
            horizontalArrangement = Arrangement.spacedBy(space = dp16)
        ) {
            items(
                count = movies.size,
                key = { index -> "${movies[index].id}_${index}_${movies[index].title}" }
            ) { index ->
                MainMovieItem(
                    movie = movies[index],
                    goToMovie = goToMovie
                )
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
            .width(dp150)
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

@Composable
fun ReleaseMoviesDialog(
    onNoShowToday: () -> Unit,
    onDismiss: () -> Unit,
    releaseMovies: List<Movie>,
    goToMovie: (Int) -> Unit
) {
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        val pagerState = rememberPagerState(initialPage = 0) { releaseMovies.size }

        Column(
            modifier = Modifier
                .width(width = dp300)
                .background(color = Color.LightGray, shape = RoundedCornerShape(size = dp10)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            HorizontalPager(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        goToMovie(releaseMovies[pagerState.currentPage].id ?: -1)
                        onDismiss()
                    },
                state = pagerState,
            ) {
                Log.d("NextWeekReleaseMovies Index -> ${pagerState.currentPage}")
                Box {
                    DynamicAsyncImageLoader(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(ratio = POSTER_IMAGE_RATIO)
                            .clip(shape = RoundedCornerShape(topStart = dp10, topEnd = dp10)),
                        source = "${releaseMovies[pagerState.currentPage].posterPath}",
                        contentDescription = "ReleaseMovieImage"
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .background(color = Color(color = 0x33000000)),
                        text = stringResource(id = R.string.release_movie, releaseMovies[pagerState.currentPage].releaseDate ?: ""),
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier
                        .weight(weight = 1f)
                        .wrapContentHeight()
                        .clickable {
                            onNoShowToday()
                            onDismiss()
                        },
                    text = stringResource(id = R.string.no_show_today),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = sp20,
                    color = Color.Black
                )
                Text(
                    modifier = Modifier
                        .weight(weight = 1f)
                        .wrapContentHeight()
                        .clickable { onDismiss() },
                    text = stringResource(id = R.string.close),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = sp20,
                    color = Color.Black
                )
            }
        }
    }
}