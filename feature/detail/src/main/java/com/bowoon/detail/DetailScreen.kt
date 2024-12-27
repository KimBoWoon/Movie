package com.bowoon.detail

import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.bowoon.common.Log
import com.bowoon.model.MovieDetail
import com.bowoon.model.MoviePoster
import com.bowoon.model.TMBDMovieDetailVideos
import com.bowoon.model.TMDBMovieDetailCountry
import com.bowoon.model.TMDBMovieDetailReleases
import com.bowoon.model.TMDBMovieDetailVideoResult
import com.bowoon.ui.ConfirmDialog
import com.bowoon.ui.Title
import com.bowoon.ui.dp10
import com.bowoon.ui.image.DynamicAsyncImageLoader
import com.bowoon.ui.theme.MovieTheme
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.launch

@Composable
fun DetailScreen(
    navController: NavController,
    viewModel: DetailVM = hiltViewModel()
) {
    val movieInfo by viewModel.movieInfo.collectAsStateWithLifecycle()

    DetailScreen(
        state = movieInfo,
        navController = navController,
        updateFavoriteMovies = viewModel::updateFavoriteMovies
    )
}

@Composable
fun DetailScreen(
    state: MovieDetailState,
    navController: NavController,
    updateFavoriteMovies: (MovieDetail) -> Unit
) {
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
                message = "통신 실패",
                confirmPair = "확인" to {},
                dismissPair = "취소" to {}
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
        } else {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                movieDetail?.let {
                    Title(
                        title = it.title ?: "",
                        isFavorite = it.isFavorite,
                        onBackClick = { navController.navigateUp() },
                        onFavoriteClick = { updateFavoriteMovies(it) }
                    )
                    MovieDetail(movieDetail = it)
                }
            }
        }
    }
}

@Composable
fun MovieDetail(
    movieDetail: MovieDetail
) {
    val scrollState = rememberScrollState()

    Column {
        VideosComponent(movieDetail)

        Column(
            modifier = Modifier.fillMaxWidth().wrapContentHeight().verticalScroll(state = scrollState)
        ) {
            MovieInfoComponent(movie = movieDetail)
            TabComponent(movieDetail)
        }
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
    movie: MovieDetail
) {
    val scope = rememberCoroutineScope()
    val tabList = MoviePoster.entries
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabList.size })

    ScrollableTabRow(
        selectedTabIndex = pagerState.currentPage,
        edgePadding = 0.dp
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
            modifier = Modifier.wrapContentSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val width = 200
            val ratio = 9f / 16f

            if (tabList[index].label == MoviePoster.POSTER.label) {
                ImagePagerComponent(
                    modifier = Modifier.width(width.dp).aspectRatio(ratio),
                    list = movie.images?.posters?.map { "https://image.tmdb.org/t/p/original${it.filePath}" } ?: emptyList()
                )
            } else {
                ImagePagerComponent(
                    modifier = Modifier.width(width.dp).aspectRatio(ratio),
                    list = movie.images?.posters?.map { "https://image.tmdb.org/t/p/original${it.filePath}" } ?: emptyList()
                )
            }
        }
    }
}

@Composable
fun ImagePagerComponent(
    modifier: Modifier,
    list: List<String>
) {
    LazyRow(
        modifier = Modifier.wrapContentSize(),
        contentPadding = PaddingValues(horizontal = dp10),
        horizontalArrangement = Arrangement.spacedBy(dp10)
    ) {
        items(
            items = list,
            key = { it }
        ) {
            DynamicAsyncImageLoader(
                modifier = modifier,
                source = it,
                contentDescription = "moviePoster"
            )
        }
    }
}

@Composable
fun MovieInfoComponent(
    movie: MovieDetail
) {
    val certification = movie.releases?.countries?.find { it.iso31661.equals("KR", true) }?.certification ?: ""

    Text(text = movie.title ?: "")
    Text(text = movie.originalTitle ?: "")
    Text(text = movie.genres ?: "")
    Text(
        text = when (certification) {
            "ALL" -> "전체 이용가"
            else -> "${certification}세 이용가"
        }
    )
    Text(text = movie.releases?.countries?.find { it.iso31661.equals("KR", true) }?.releaseDate ?: "")
    Text(text = "평점 : ${movie.voteAverage.toString()}")
//    Text(text = movie.revenue.toString())
//    Text(text = movie.budget.toString())
    Text(text = "${movie.runtime}분")
    Text(text = movie.overview ?: "")
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