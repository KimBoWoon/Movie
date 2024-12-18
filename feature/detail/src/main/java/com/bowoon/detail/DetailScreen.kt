package com.bowoon.detail

import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.bowoon.common.Log
import com.bowoon.model.MovieDetail
import com.bowoon.ui.ConfirmDialog
import com.bowoon.ui.dp10
import com.bowoon.ui.dp150
import com.bowoon.ui.dp200
import com.bowoon.ui.image.DynamicAsyncImageLoader
import kotlinx.coroutines.launch

@Composable
fun DetailScreen(
    viewModel: DetailVM = hiltViewModel()
) {
//    val kmdbMovieInfoState by viewModel.kmdbMovieInfo.collectAsStateWithLifecycle()
//    val kobisMovieInfoState by viewModel.kobisMovieInfo.collectAsStateWithLifecycle()
    val movieInfo by viewModel.movieInfo.collectAsStateWithLifecycle()

    DetailScreen(
        state = movieInfo
    )
}

@Composable
fun DetailScreen(
    state: MovieDetailState
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
            Log.d("${state.throwable.message}")
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
                movieDetail?.let { MovieDetail(it) }
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
            modifier = Modifier.verticalScroll(state = scrollState)
        ) {
            MovieInfoComponent(movieDetail)
            TabComponent(movieDetail)
//            PostersComponent(movieDetail)
//            StllComponent(movieDetail)
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun VideosComponent(movie: MovieDetail) {
    val context = LocalContext.current
    val vodList = movie.vods?.vod?.mapNotNull { it.vodUrl?.replace("trailerPlayPop?pFileNm=", "play/") } ?: emptyList()
    val pagerState = rememberPagerState { vodList.size }
    val scope = rememberCoroutineScope()

    HorizontalPager(
        state = pagerState
    ) {
        val screenWidth = context.resources.displayMetrics.widthPixels
        val exoPlayer = ExoPlayer.Builder(context).build()
        val dataSourceFactory = DefaultHttpDataSource.Factory()
        val mediaSource = remember(vodList[it]) {
            ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(vodList[it]))
        }

        exoPlayer.setMediaSource(mediaSource)
        exoPlayer.addListener(
            object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)

                    when (playbackState) {
                        Player.STATE_IDLE -> Log.i("exoplayer idle")
                        Player.STATE_BUFFERING -> Log.i("exoplayer buffering")
                        Player.STATE_READY -> Log.i("exoplayer ready")
                        Player.STATE_ENDED -> {
                            Log.i("exoplayer ended")
                            scope.launch {
                                if (it + 1 < vodList.size) {
                                    Log.i("go to next page")
                                    pagerState.scrollToPage(it + 1)
                                }
                            }
                        }
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    super.onPlayerError(error)
                    Log.e(error.message ?: "something wrong...")
                }
            }
        )
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true

        DisposableEffect(Unit) {
            onDispose {
                exoPlayer.release()
            }
        }

        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        (screenWidth / (16f / 9f)).toInt()
                    )
                    player = exoPlayer
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                }
            }
        )
    }
}

@Composable
fun TabComponent(
    movie: MovieDetail
) {
    val scope = rememberCoroutineScope()
    val tabList = listOf("포스터", "스틸컷")
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabList.size })

    ScrollableTabRow(
        selectedTabIndex = pagerState.currentPage,
        edgePadding = 0.dp
    ) {
        tabList.forEachIndexed { index, text ->
            Tab(
                selected = pagerState.currentPage == index,
                onClick = {
                    Log.d("selected tab index > $index")
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                text = { Text(text = text) },
                selectedContentColor = Color(0xFF7C86DF),
                unselectedContentColor = Color.LightGray
            )
        }
    }

    HorizontalPager(
        state = pagerState,
        userScrollEnabled = false
    ) { index ->
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (tabList[index] == "포스터") {
                ImagePagerComponent(
                    modifier = Modifier.height(dp200).fillMaxWidth(),
                    list = movie.posters ?: emptyList()
                )
            } else {
                ImagePagerComponent(
                    modifier = Modifier.height(dp200).fillMaxWidth(),
                    list = movie.stlls ?: emptyList()
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
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = dp10),
        horizontalArrangement = Arrangement.spacedBy(dp10)
    ) {
        items(
            items = list,
            key = { it }
        ) {
            DynamicAsyncImageLoader(
                modifier = Modifier.width(dp150).height(dp200),
                source = it,
                contentDescription = "moviePoster"
            )
        }
    }
}

@Composable
fun MovieInfoComponent(movie: MovieDetail) {
    Text(text = movie.title ?: "")
    Text(text = movie.titleEng ?: "")
    Text(text = movie.genre ?: "")
    Text(text = movie.rating ?: "")
    Text(text = movie.repRlsDate ?: "")
    if (!movie.audiAcc.isNullOrEmpty()) {
        Text(text = movie.audiAcc ?: "")
    }
    if (!movie.salesAcc.isNullOrEmpty()) {
        Text(text = movie.salesAcc ?: "")
    }
    Text(text = "${movie.runtime}분")
    Text(text = movie.plots?.plot?.find { it.plotLang == "한국어" }?.plotText ?: "")
}