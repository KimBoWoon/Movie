package com.bowoon.detail

import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.bowoon.common.Log
import com.bowoon.detail.navigation.navigateToDetail
import com.bowoon.model.MovieDetail
import com.bowoon.model.MovieDetailTab
import com.bowoon.model.TMBDMovieDetailVideos
import com.bowoon.model.TMDBMovieDetailCast
import com.bowoon.model.TMDBMovieDetailCountry
import com.bowoon.model.TMDBMovieDetailCrew
import com.bowoon.model.TMDBMovieDetailReleases
import com.bowoon.model.TMDBMovieDetailVideoResult
import com.bowoon.ui.ConfirmDialog
import com.bowoon.ui.FavoriteButton
import com.bowoon.ui.Title
import com.bowoon.ui.dp10
import com.bowoon.ui.dp100
import com.bowoon.ui.dp16
import com.bowoon.ui.dp200
import com.bowoon.ui.dp5
import com.bowoon.ui.image.DynamicAsyncImageLoader
import com.bowoon.ui.sp20
import com.bowoon.ui.theme.MovieTheme
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.launch
import java.text.DecimalFormat

@Composable
fun DetailScreen(
    navController: NavController,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    viewModel: DetailVM = hiltViewModel()
) {
    val movieInfo by viewModel.movieInfo.collectAsStateWithLifecycle()

    DetailScreen(
        state = movieInfo,
        navController = navController,
        updateFavoriteMovies = viewModel::updateFavoriteMovies,
        onShowSnackbar = onShowSnackbar,
        restart = viewModel::restart
    )
}

@Composable
fun DetailScreen(
    state: MovieDetailState,
    navController: NavController,
    updateFavoriteMovies: (MovieDetail) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    restart: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var movieDetail by remember { mutableStateOf<MovieDetail?>(null) }

    when (state) {
        is MovieDetailState.Loading -> {
            Log.d("loading...")
            isLoading = true
        }
        is MovieDetailState.Success -> {
            Log.d("${state.movieDetail}")
            isLoading = false
            movieDetail = state.movieDetail
        }
        is MovieDetailState.Error -> {
            Log.e("${state.throwable.message}")
            isLoading = false
            ConfirmDialog(
                title = "통신 실패",
                message = "${state.throwable.message}",
                confirmPair = "재시도" to { restart() },
                dismissPair = "돌아가기" to { navController.navigateUp() }
            )
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            movieDetail?.let {
                Title(
                    title = it.title ?: "",
                    isFavorite = it.isFavorite,
                    onBackClick = { navController.navigateUp() },
                    onFavoriteClick = {
                        updateFavoriteMovies(it)
                        scope.launch {
                            val isFavorite = it.favoriteMovies?.find { favoriteMovie -> favoriteMovie.id == it.id } != null
                            onShowSnackbar(
                                if (isFavorite) "보고 싶은 영화에서 제거했습니다." else "보고 싶은 영화에 추가했습니다.",
                                null
                            )
                        }
                    }
                )
                MovieDetail(
                    movieDetail = it,
                    onMovieClick = { id -> navController.navigateToDetail(id) },
                    favoriteMovies = it.favoriteMovies ?: emptyList(),
                    updateFavoriteMovies = updateFavoriteMovies
                )
            }
        }
    }
}

@Composable
fun MovieDetail(
    movieDetail: MovieDetail,
    onMovieClick: (Int) -> Unit,
    favoriteMovies: List<MovieDetail>,
    updateFavoriteMovies: (MovieDetail) -> Unit
) {
    Column {
        VideosComponent(movieDetail)

        TabComponent(
            movie = movieDetail,
            onMovieClick = onMovieClick,
            favoriteMovies = favoriteMovies,
            updateFavoriteMovies = updateFavoriteMovies
        )
    }
}

@OptIn(UnstableApi::class)
@Composable
fun VideosComponent(movie: MovieDetail) {
    val context = LocalContext.current
    val vodList = movie.videos?.results?.mapNotNull { it.key } ?: emptyList()
    val pagerState = rememberPagerState { vodList.size }

    HorizontalPager(
        state = pagerState
    ) { index ->
        val screenWidth = context.resources.displayMetrics.widthPixels
        val view = YouTubePlayerView(context)
        val scope = rememberCoroutineScope()

        DisposableEffect("youtube") {
            onDispose {
                view.release()
            }
        }

        AndroidView(
            factory = {
                view.layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    (screenWidth / (16f / 9f)).toInt()
                )
                view.addYouTubePlayerListener(
                    object : AbstractYouTubePlayerListener() {
                        override fun onReady(youTubePlayer: YouTubePlayer) {
                            super.onReady(youTubePlayer)
                            youTubePlayer.loadVideo(vodList[index], 0f)
                        }

                        override fun onStateChange(
                            youTubePlayer: YouTubePlayer,
                            state: PlayerConstants.PlayerState
                        ) {
                            super.onStateChange(youTubePlayer, state)
                            when (state) {
                                PlayerConstants.PlayerState.UNKNOWN -> Log.d("UNKNOWN")
                                PlayerConstants.PlayerState.UNSTARTED -> Log.d("UNSTARTED")
                                PlayerConstants.PlayerState.ENDED -> {
                                    Log.d("ENDED")
                                    scope.launch {
                                        pagerState.scrollToPage(index + 1)
                                    }
                                }
                                PlayerConstants.PlayerState.PLAYING -> Log.d("PLAYING")
                                PlayerConstants.PlayerState.PAUSED -> Log.d("PAUSED")
                                PlayerConstants.PlayerState.BUFFERING -> Log.d("BUFFERING")
                                PlayerConstants.PlayerState.VIDEO_CUED -> Log.d("VIDEO_CUED")
                            }
                        }
                    }
                )
                view
            }
        )
    }
}

@Composable
fun TabComponent(
    movie: MovieDetail,
    onMovieClick: (Int) -> Unit,
    favoriteMovies: List<MovieDetail>,
    updateFavoriteMovies: (MovieDetail) -> Unit
) {
    val scope = rememberCoroutineScope()
    val tabList = MovieDetailTab.entries
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabList.size })

//    ScrollableTabRow(
    TabRow(
        modifier = Modifier.fillMaxWidth(),
        selectedTabIndex = pagerState.currentPage,
//        edgePadding = dp0
    ) {
        tabList.forEachIndexed { index, moviePoster ->
            Tab(
                selected = pagerState.currentPage == index,
                onClick = {
                    Log.d("selected tab index > $index")
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                text = { Text(text = moviePoster.label) },
                selectedContentColor = Color(0xFF7C86DF),
                unselectedContentColor = Color.LightGray
            )
        }
    }

    HorizontalPager(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        state = pagerState,
        userScrollEnabled = false
    ) { index ->
        Column(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (tabList[index].label) {
                MovieDetailTab.MOVIE_INFO.label -> MovieInfoComponent(movie = movie)
                MovieDetailTab.ACTOR_AND_CREW.label -> ActorAndCrewComponent(movie = movie)
                MovieDetailTab.POSTER.label -> ImageComponent(movie = movie)
                MovieDetailTab.SIMILAR.label -> SimilarMovieComponent(
                    movie = movie,
                    onMovieClick = onMovieClick,
                    favoriteMovies = favoriteMovies,
                    updateFavoriteMovies = updateFavoriteMovies
                )
            }
        }
    }
}

@Composable
fun ImageComponent(
    movie: MovieDetail
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(dp100),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = dp10),
        horizontalArrangement = Arrangement.spacedBy(dp10),
        verticalItemSpacing = dp10
    ) {
        items(
            items = movie.images?.posters ?: emptyList(),
            key = { it }
        ) {
            DynamicAsyncImageLoader(
                modifier = Modifier.width(dp200).aspectRatio(it.aspectRatio?.toFloat() ?: 1f),
                source = it.filePath ?: "",
                contentDescription = "moviePoster"
            )
        }
    }
}

@Composable
fun MovieInfoComponent(
    movie: MovieDetail
) {
    val format = DecimalFormat("#,###")
    var overviewMaxLine by remember { mutableIntStateOf(3) }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(text = movie.title ?: "")
            Text(text = movie.originalTitle ?: "")
            Text(text = movie.genres ?: "")
            Text(text = movie.certification ?: "")
            Text(text = movie.releaseDate ?: "")
            Text(text = "평점 : ${movie.voteAverage.toString()}")
            Text(text = "수익 : ${format.format(movie.revenue)}")
            Text(text = "예산 : ${format.format(movie.budget)}")
            Text(text = "순수익 : ${format.format((movie.revenue ?: 0) + -(movie.budget ?: 0))}")
            Text(text = "${movie.runtime}분")
            Text(
                modifier = Modifier.clickable {
                    overviewMaxLine = if (overviewMaxLine == 3) Int.MAX_VALUE else 3
                },
                text = movie.overview ?: "",
                maxLines = overviewMaxLine,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ActorAndCrewComponent(
    movie: MovieDetail
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                modifier = Modifier.fillMaxWidth().padding(vertical = dp16),
                text = "배우",
                fontSize = sp20,
                textAlign = TextAlign.Center
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth().wrapContentHeight()
            ) {
                items(
                    items = movie.credits?.cast ?: emptyList()
                ) { cast ->
                    StaffComponent(cast)
                }
            }
            Text(
                modifier = Modifier.fillMaxWidth().padding(vertical = dp16),
                text = "스태프",
                fontSize = sp20,
                textAlign = TextAlign.Center
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth().wrapContentHeight()
            ) {
                items(
                    items = movie.credits?.crew ?: emptyList()
                ) { cast ->
                    StaffComponent(cast)
                }
            }
        }
    }
}

@Composable
fun StaffComponent(
    tmdbMovieDetailCast: TMDBMovieDetailCast
) {
    Column(
        modifier = Modifier.width(dp200).wrapContentHeight()
    ) {
        DynamicAsyncImageLoader(
            modifier = Modifier.fillMaxWidth().aspectRatio(4f / 3f),
            source = tmdbMovieDetailCast.profilePath ?: "",
            contentDescription = "ProfileImage"
        )
        Text(text = tmdbMovieDetailCast.character ?: "")
        Text(text = tmdbMovieDetailCast.name ?: "")
        Text(text = tmdbMovieDetailCast.originalName ?: "")
    }
}

@Composable
fun StaffComponent(
    tmdbMovieDetailCrew: TMDBMovieDetailCrew
) {
    Column(
        modifier = Modifier.width(dp200).wrapContentHeight()
    ) {
        DynamicAsyncImageLoader(
            modifier = Modifier.fillMaxWidth().aspectRatio(4f / 3f),
            source = tmdbMovieDetailCrew.profilePath ?: "",
            contentDescription = "ProfileImage"
        )
        Text(text = tmdbMovieDetailCrew.department ?: "")
        Text(text = tmdbMovieDetailCrew.name ?: "")
        Text(text = tmdbMovieDetailCrew.originalName ?: "")
        Text(text = tmdbMovieDetailCrew.job ?: "")
    }
}

@Composable
fun SimilarMovieComponent(
    movie: MovieDetail,
    onMovieClick: (Int) -> Unit,
    favoriteMovies: List<MovieDetail>,
    updateFavoriteMovies: (MovieDetail) -> Unit
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(dp100),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(dp10),
        horizontalArrangement = Arrangement.spacedBy(dp10),
        verticalItemSpacing = dp10
    ) {
        items(
            items = movie.similar?.results ?: emptyList()
        ) { similarMovie ->
            val detail = MovieDetail(
                id = similarMovie.id,
                posterPath = similarMovie.posterPath ?: "",
            )
            Box(
                modifier = Modifier.width(dp200).wrapContentHeight().clickable { onMovieClick(detail.id ?: -1) }
            ) {
                DynamicAsyncImageLoader(
                    modifier = Modifier.fillMaxWidth().aspectRatio(9f / 16f),
                    source = detail.posterPath ?: "",
                    contentDescription = "BoxOfficePoster"
                )
                FavoriteButton(
                    modifier = Modifier
                        .padding(top = dp5, end = dp5)
                        .wrapContentSize()
                        .align(Alignment.TopEnd),
                    isFavorite = favoriteMovies.find { it.id == detail.id } != null,
                    onClick = { updateFavoriteMovies(detail) }
                )
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun MovieInfoComponentPreview() {
    MovieTheme {
        Column {
            MovieInfoComponent(
                MovieDetail(
                    title = "asdf",
                    originalTitle = "qwer",
                    genres = "zxcv",
                    releases = TMDBMovieDetailReleases(listOf(TMDBMovieDetailCountry(certification = "ALL"))),
                    videos = TMBDMovieDetailVideos(listOf(TMDBMovieDetailVideoResult(key = "")))
                )
            )
        }
    }
}