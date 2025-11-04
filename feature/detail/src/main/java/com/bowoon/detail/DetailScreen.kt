package com.bowoon.detail

import android.widget.FrameLayout
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.bowoon.common.Log
import com.bowoon.data.util.PEOPLE_IMAGE_RATIO
import com.bowoon.data.util.POSTER_IMAGE_RATIO
import com.bowoon.data.util.VIDEO_RATIO
import com.bowoon.firebase.LocalFirebaseLogHelper
import com.bowoon.model.Cast
import com.bowoon.model.CreditInfo
import com.bowoon.model.Crew
import com.bowoon.model.Image
import com.bowoon.model.Movie
import com.bowoon.model.MovieDetailInfo
import com.bowoon.model.Series
import com.bowoon.movie.feature.detail.R
import com.bowoon.ui.components.CircularProgressComponent
import com.bowoon.ui.components.PagingAppendErrorComponent
import com.bowoon.ui.components.TabComponent
import com.bowoon.ui.components.TitleComponent
import com.bowoon.ui.components.movieSeriesListComponent
import com.bowoon.ui.components.seriesInfoComponent
import com.bowoon.ui.dialog.ConfirmDialog
import com.bowoon.ui.dialog.ModalBottomSheetDialog
import com.bowoon.ui.image.DynamicAsyncImageLoader
import com.bowoon.ui.utils.animateRotation
import com.bowoon.ui.utils.bounceClick
import com.bowoon.ui.utils.dp0
import com.bowoon.ui.utils.dp1
import com.bowoon.ui.utils.dp10
import com.bowoon.ui.utils.dp100
import com.bowoon.ui.utils.dp16
import com.bowoon.ui.utils.dp20
import com.bowoon.ui.utils.dp200
import com.bowoon.ui.utils.dp300
import com.bowoon.ui.utils.dp32
import com.bowoon.ui.utils.dp5
import com.bowoon.ui.utils.dp60
import com.bowoon.ui.utils.dp8
import com.bowoon.ui.utils.sp10
import com.bowoon.ui.utils.sp12
import com.bowoon.ui.utils.sp15
import com.bowoon.ui.utils.sp20
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.launch
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter

@Composable
fun DetailScreen(
    goToBack: () -> Unit,
    goToMovie: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    viewModel: DetailVM = hiltViewModel()
) {
    LocalFirebaseLogHelper.current.sendLog("DetailScreen", "detail screen start!")

    val detailState by viewModel.detail.collectAsStateWithLifecycle()
    val similarMovies = viewModel.similarMovies.collectAsLazyPagingItems()
    val movieReviews = viewModel.movieReviews.collectAsLazyPagingItems()

    DetailScreen(
        detailState = detailState,
        similarMovies = similarMovies,
        movieReviews = movieReviews,
        goToMovie = goToMovie,
        goToPeople = goToPeople,
        goToBack = goToBack,
        onShowSnackbar = onShowSnackbar,
        insertFavoriteMovie = viewModel::insertMovie,
        deleteFavoriteMovie = viewModel::deleteMovie,
        restart = viewModel::restart
    )
}

@Composable
fun DetailScreen(
    detailState: DetailState,
    similarMovies: LazyPagingItems<Movie>,
    movieReviews: LazyPagingItems<ReviewDataModel>,
    goToMovie: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    goToBack: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    insertFavoriteMovie: (Movie) -> Unit,
    deleteFavoriteMovie: (Movie) -> Unit,
    restart: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (detailState) {
            is DetailState.Loading -> {
                Log.d("loading...")
                LocalFirebaseLogHelper.current.sendLog(name = "DetailScreen", message = "loading...")

                CircularProgressComponent(
                    modifier = Modifier
                        .testTag(tag = "detailScreenLoading")
                        .align(Alignment.Center)
                )
            }
            is DetailState.Success -> {
                Log.d("${detailState.movieInfo.detail}")
                LocalFirebaseLogHelper.current.sendLog(name = "DetailScreen", message = "${detailState.movieInfo}")

                MovieDetailComponent(
                    movieInfo = detailState.movieInfo,
                    similarMovies = similarMovies,
                    movieReviews = movieReviews,
                    goToMovie = goToMovie,
                    goToPeople = goToPeople,
                    goToBack = goToBack,
                    onShowSnackbar = onShowSnackbar,
                    insertFavoriteMovie = insertFavoriteMovie,
                    deleteFavoriteMovie = deleteFavoriteMovie
                )
            }
            is DetailState.Error -> {
                Log.e("${detailState.throwable.message}")
                LocalFirebaseLogHelper.current.sendLog(name = "DetailScreen", message = "${detailState.throwable.message}")

                ConfirmDialog(
                    title = stringResource(id = com.bowoon.movie.core.network.R.string.network_failed),
                    message = "${detailState.throwable.message}",
                    confirmPair = stringResource(id = com.bowoon.movie.core.ui.R.string.retry_message) to { restart() },
                    dismissPair = stringResource(id = com.bowoon.movie.core.ui.R.string.back_message) to goToBack
                )
            }
        }
    }
}

@Composable
fun MovieDetailComponent(
    movieInfo: MovieDetailInfo,
    similarMovies: LazyPagingItems<Movie>,
    movieReviews: LazyPagingItems<ReviewDataModel>,
    goToMovie: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    goToBack: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    insertFavoriteMovie: (Movie) -> Unit,
    deleteFavoriteMovie: (Movie) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val tabList = mutableListOf(
        stringResource(id = R.string.movie_detail),
        stringResource(id = R.string.movie_series),
        stringResource(id = R.string.movie_reviews),
        stringResource(id = R.string.movie_actor_and_crew),
        stringResource(id = R.string.movie_images),
        stringResource(id = R.string.movie_similar_movies)
    )
    if (movieInfo.detail.belongsToCollection == null) {
        stringResource(id = R.string.movie_series)
    }
    if (movieReviews.itemCount == 0) {
        stringResource(id = R.string.movie_reviews)
    }
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { tabList.size }
    )
    val tabClickEvent: (Int, Int) -> Unit = { current, index ->
        scope.launch {
            pagerState.animateScrollToPage(page = index)
        }
    }
    val favoriteMessage = if (movieInfo.detail.isFavorite) stringResource(id = R.string.add_favorite_movie) else stringResource(id = R.string.remove_favorite_movie)

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TitleComponent(
            title = movieInfo.detail.title ?: "",
            isFavorite = movieInfo.detail.isFavorite,
            goToBack = goToBack,
            onFavoriteClick = {
                if (movieInfo.detail.isFavorite) {
                    deleteFavoriteMovie(movieInfo.detail)
                } else {
                    insertFavoriteMovie(movieInfo.detail)
                }
                scope.launch {
                    onShowSnackbar(favoriteMessage, null)
                }
            }
        )

        VideosComponent(
            vodList = movieInfo.detail.videos?.results?.mapNotNull { it.key } ?: emptyList(),
            autoPlayTrailer = movieInfo.autoPlayTrailer
        )

        TabComponent(
            tabs = tabList,
            pagerState = pagerState,
            tabClickEvent = tabClickEvent
        ) { tabs ->
            HorizontalPager(
                modifier = Modifier
//                    .semantics { contentDescription = "detailTabRow" }
                    .fillMaxWidth()
                    .wrapContentHeight(),
                state = pagerState,
                userScrollEnabled = false
            ) { index ->
                when (tabs[index]) {
                    tabList[0] -> MovieInfoComponent(movie = movieInfo.detail)
                    tabList[1] -> MovieSeriesComponent(
                        movieSeries = movieInfo.series,
                        goToMovie = goToMovie
                    )
                    tabList[2] -> MovieReviewComponent(
                        movieReviews = movieReviews,
                    )
                    tabList[3] -> ActorAndCrewComponent(
                        movie = movieInfo.detail,
                        goToPeople = goToPeople
                    )
                    tabList[4] -> {
                        val posters = movieInfo.detail.images?.posters ?: emptyList()
                        val backdrops = movieInfo.detail.images?.backdrops ?: emptyList()
                        ImageComponent(images = posters + backdrops)
                    }
                    tabList[5] -> SimilarMovieComponent(
                        similarMovies = similarMovies,
                        goToMovie = goToMovie
                    )
                }
            }
        }
    }
}

@Composable
fun VideosComponent(
    vodList: List<String>,
    autoPlayTrailer: Boolean?
) {
    if (vodList.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(ratio = VIDEO_RATIO)
                .background(color = Color.Black)
        ) {
            Text(
                modifier = Modifier.align(alignment = Alignment.Center),
                text = stringResource(id = R.string.trailer_video_not_found),
                color = Color.White,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    } else {
        val pagerState = rememberPagerState { vodList.size }

        HorizontalPager(
            state = pagerState
        ) { index ->
            val scope = rememberCoroutineScope()
            val listener = object : YouTubePlayerListener {
                override fun onApiChange(youTubePlayer: YouTubePlayer) {
                    Log.d("onApiChange")
                }

                override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                    Log.d("onCurrentSecond > $second")
                }

                override fun onError(
                    youTubePlayer: YouTubePlayer,
                    error: PlayerConstants.PlayerError
                ) {
                    when (error) {
                        PlayerConstants.PlayerError.UNKNOWN -> Log.e("UNKNOWN")
                        PlayerConstants.PlayerError.INVALID_PARAMETER_IN_REQUEST -> Log.e("INVALID_PARAMETER_IN_REQUEST")
                        PlayerConstants.PlayerError.HTML_5_PLAYER -> Log.e("HTML_5_PLAYER")
                        PlayerConstants.PlayerError.VIDEO_NOT_FOUND -> {
                            Log.e("VIDEO_NOT_FOUND")
                            scope.launch {
                                pagerState.scrollToPage(index + 1)
                            }
                        }
                        PlayerConstants.PlayerError.VIDEO_NOT_PLAYABLE_IN_EMBEDDED_PLAYER -> {
                            Log.e("VIDEO_NOT_PLAYABLE_IN_EMBEDDED_PLAYER")
                            scope.launch {
                                pagerState.scrollToPage(index + 1)
                            }
                        }
                        PlayerConstants.PlayerError.REQUEST_MISSING_HTTP_REFERER -> Log.e("REQUEST_MISSING_HTTP_REFERER")
                    }
                }

                override fun onPlaybackQualityChange(
                    youTubePlayer: YouTubePlayer,
                    playbackQuality: PlayerConstants.PlaybackQuality
                ) {
                    Log.d("onPlaybackQualityChange > $playbackQuality")
                }

                override fun onPlaybackRateChange(
                    youTubePlayer: YouTubePlayer,
                    playbackRate: PlayerConstants.PlaybackRate
                ) {
                    Log.d("onPlaybackRateChange > ${playbackRate.name}")
                }

                override fun onReady(youTubePlayer: YouTubePlayer) {
                    when (autoPlayTrailer) {
                        true -> youTubePlayer.loadVideo(videoId = vodList[index], startSeconds = 0f)
                        false -> youTubePlayer.cueVideo(videoId = vodList[index], startSeconds = 0f)
                        else -> youTubePlayer.cueVideo(videoId = vodList[index], startSeconds = 0f)
                    }
                }

                override fun onStateChange(
                    youTubePlayer: YouTubePlayer,
                    state: PlayerConstants.PlayerState
                ) {
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

                override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
                    Log.d("onVideoDuration > $duration")
                }

                override fun onVideoId(youTubePlayer: YouTubePlayer, videoId: String) {
                    Log.d("onVideoId > $videoId")
                }

                override fun onVideoLoadedFraction(
                    youTubePlayer: YouTubePlayer,
                    loadedFraction: Float
                ) {
                    Log.d("onVideoLoadedFraction > $loadedFraction")
                }
            }

            AndroidView(
                factory = { context ->
                    YouTubePlayerView(context = context).apply {
                        layoutParams = FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            (context.resources.displayMetrics.widthPixels / (VIDEO_RATIO)).toInt()
                        )
                        addYouTubePlayerListener(youTubePlayerListener = listener)
                    }
                },
                onRelease = { youTubePlayerView ->
                    youTubePlayerView.removeYouTubePlayerListener(youTubePlayerListener = listener)
                    youTubePlayerView.release()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageComponent(
    images: List<Image>
) {
    var isShowing by remember { mutableStateOf(value = false) }
    var index by remember { mutableIntStateOf(value = 0) }
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    if (images.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = stringResource(id = R.string.movie_image_not_found))
        }
    } else {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(minSize = dp100),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(all = dp10),
            horizontalArrangement = Arrangement.spacedBy(space = dp10),
            verticalItemSpacing = dp10
        ) {
            items(
                items = images,
                key = { image -> image.filePath ?: image }
            ) {
                DynamicAsyncImageLoader(
                    modifier = Modifier
                        .width(width = dp200)
                        .aspectRatio(ratio = it.aspectRatio?.toFloat() ?: 1f)
                        .clip(shape = RoundedCornerShape(size = dp10))
                        .bounceClick {
                            index = images.indexOf(it)
                            isShowing = true
                        },
                    source = it.filePath ?: "",
                    contentDescription = "moviePoster"
                )
            }
        }
    }

    if (isShowing) {
        ModalBottomSheetDialog(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(POSTER_IMAGE_RATIO),
            state = modalBottomSheetState,
            scope = scope,
            index = index,
            imageList = images,
            onClickCancel = {
                scope.launch {
                    isShowing = false
                    modalBottomSheetState.hide()
                }
            }
        )
    }
}

@Composable
fun MovieSeriesComponent(
    movieSeries: Series?,
    goToMovie: (Int) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .semantics { contentDescription = "seriesList" }
            .fillMaxSize(),
        contentPadding = PaddingValues(horizontal = dp16, vertical = dp10),
        verticalArrangement = Arrangement.spacedBy(dp10)
    ) {
        movieSeries?.let {
            seriesInfoComponent(series = movieSeries)
            movieSeriesListComponent(
                series = movieSeries.parts ?: emptyList(),
                goToMovie = goToMovie
            )
        }
    }
}

@Composable
fun MovieReviewComponent(
    movieReviews: LazyPagingItems<ReviewDataModel>
) {
    if (movieReviews.itemCount == 0 && movieReviews.loadState.refresh !is LoadState.Loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "리뷰가 없습니다.")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(space = dp10),
            contentPadding = PaddingValues(all = dp10)
        ) {
            items(count = movieReviews.itemCount) {
                when (movieReviews[it]) {
                    is ReviewDataModel.Separator -> {
                        Spacer(modifier = Modifier
                            .fillMaxWidth()
                            .height(height = dp1)
                            .padding(horizontal = dp20)
                            .background(color = MaterialTheme.colorScheme.onSurface))
                    }
                    is ReviewDataModel.Item -> {
                        val review = (movieReviews[it] as ReviewDataModel.Item).review

                        Column(
                            modifier = Modifier
                                .wrapContentSize()
//                        .border(width = dp1, color = MaterialTheme.colorScheme.onSurface, shape = RoundedCornerShape(size = dp20))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(space = dp5),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                DynamicAsyncImageLoader(
                                    source = review.authorDetails?.avatarPath ?: "",
                                    contentDescription = "reviewAuthorAvatarPath",
                                    modifier = Modifier
                                        .size(size = dp60)
                                        .clip(shape = CircleShape)
                                )
                                Column {
                                    Text(
                                        modifier = Modifier.padding(bottom = dp5),
                                        text = review.author ?: "",
                                        fontSize = sp15,
                                        style = TextStyle(
                                            platformStyle = PlatformTextStyle(includeFontPadding = false)
                                        )
                                    )
                                    val time = if (!review.updatedAt?.trim().isNullOrEmpty()) {
                                        "${Instant.from(DateTimeFormatter.ISO_INSTANT.parse(review.updatedAt)).let { instant ->
                                            LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss"))
                                        }} (수정 됨)"
                                    } else {
                                        Instant.from(DateTimeFormatter.ISO_INSTANT.parse(review.createdAt)).let { instant ->
                                            LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss"))
                                        }
                                    }
                                    Text(
                                        text = time,
                                        fontSize = sp10,
                                        style = TextStyle(
                                            platformStyle = PlatformTextStyle(includeFontPadding = false)
                                        )
                                    )
                                }
                            }
                            Text(text = review.content ?: "")
                        }
                    }
                    else -> {
                        LocalFirebaseLogHelper.current.sendLog("MovieReviewComponent", "ReviewDataModel not found...")
                        Log.d("ReviewDataModel not found...")
                    }
                }
            }
        }
    }
}

@Composable
fun MovieInfoComponent(
    movie: Movie
) {
    var expanded by remember { mutableStateOf(value = false) }
    val titles = movie.alternativeTitles?.titles?.fold(initial = "") { acc, title -> if (acc.isEmpty()) "${title.title}" else "$acc\n${title.title}" } ?: ""

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        // release date
        item {
            Row(
                modifier = Modifier
                    .padding(start = dp16, end = dp16, top = dp10)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                movie.releaseDate?.takeIf { it.isNotEmpty() }?.let {
                    Text(
                        text = it,
                        fontSize = sp10,
                        style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                    )
                }
                if (!movie.releaseDate.isNullOrEmpty() && !movie.certification.isNullOrEmpty()) {
                    Text(
                        modifier = Modifier.padding(horizontal = dp5),
                        text = stringResource(R.string.movie_info_separator),
                        fontSize = sp10,
                        style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                    )
                }
                movie.certification?.takeIf { it.isNotEmpty() }?.let {
                    Text(
                        text = it,
                        fontSize = sp10,
                        style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                    )
                }
            }
        }
        // movie title
        item {
            movie.tagline?.takeIf { it.isNotEmpty() }?.let {
                Text(
                    modifier = Modifier
                        .testTag(tag = "movieTagline")
                        .padding(
                            start = dp16,
                            end = dp16,
                            top = if (!movie.releaseDate.isNullOrEmpty() && !movie.certification.isNullOrEmpty()) dp10 else dp20
                        )
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    text = it,
                    fontSize = sp15,
                    textAlign = TextAlign.Center,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                )
            }
            movie.title?.takeIf { it.isNotEmpty() }?.let {
                Text(
                    modifier = Modifier
                        .testTag(tag = "movieTitle")
                        .padding(
                            start = dp16,
                            end = dp16,
                            top = if (!movie.tagline.isNullOrEmpty()) dp0 else dp20
                        )
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    text = it,
                    fontSize = sp20,
                    textAlign = TextAlign.Center
                )
            }
            movie.originalTitle?.takeIf { it.isNotEmpty() }?.let {
                Text(
                    modifier = Modifier
                        .padding(top = dp5, bottom = dp5, start = dp16, end = dp16)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    text = it,
                    fontSize = sp10,
                    textAlign = TextAlign.Center,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                )
            }
            movie.genres?.takeIf { it.isNotEmpty() }?.let {
                Text(
                    modifier = Modifier
                        .padding(horizontal = dp16)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    text = it.fold("") { acc, genre -> if (acc.isEmpty()) "${genre.name}" else "$acc, ${genre.name}" },
                    fontSize = sp10,
                    textAlign = TextAlign.Center,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                )
            }
            Row(
                modifier = Modifier
                    .padding(horizontal = dp16)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                movie.runtime?.let {
                    Text(
                        text = stringResource(id = R.string.movie_runtime, it),
                        fontSize = sp10,
                        textAlign = TextAlign.Center
                    )
                }
                if (movie.runtime != null && movie.voteAverage != null) {
                    Text(
                        modifier = Modifier.padding(horizontal = dp5),
                        text = stringResource(id = R.string.movie_info_separator),
                        fontSize = sp10
                    )
                }
                movie.voteAverage?.let {
                    Text(
                        text = stringResource(id = R.string.movie_vote_average, it),
                        fontSize = sp10,
                        textAlign = TextAlign.Center
                    )
                }
            }
            movie.overview?.takeIf { it.isNotEmpty() }?.let {
                Text(
                    modifier = Modifier
                        .padding(horizontal = dp16)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    text = it,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        item {
            movie.productionCompanies.takeIf { !it.isNullOrEmpty() }?.let { production ->
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = dp10, horizontal = dp16),
                    text = stringResource(id = R.string.movie_production_companies),
                    textAlign = TextAlign.Center,
                    fontSize = sp20,
                    fontWeight = FontWeight.Bold
                )
                HorizontalPager(
                    modifier = Modifier.fillMaxWidth(),
                    state = rememberPagerState { production.size },
                    contentPadding = PaddingValues(horizontal = dp32),
                    key = { index -> production[index].id ?: -1 }
                ) { index ->
                    Column(
                        modifier = Modifier.padding(horizontal = dp8)
                    ) {
                        production[index].logoPath?.let {
                            DynamicAsyncImageLoader(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(dp300)
                                    .clip(shape = RoundedCornerShape(size = dp10)),
                                contentScale = ContentScale.Fit,
                                source = it,
                                contentDescription = it
                            )
                        }
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = production[index].name ?: "",
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
        item {
            if (titles.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = dp16)
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .clickable { expanded = !expanded }
                ) {
                    Icon(
                        modifier = Modifier.animateRotation(expanded = expanded, startAngle = 0f, endAngle = 90f, animateMillis = 500),
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = "expandedArrow"
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        text = stringResource(id = R.string.movie_alternative_title),
                        fontSize = sp15
                    )
                }
                Text(
                    modifier = Modifier
                        .padding(top = dp5, start = dp16, end = dp16)
                        .animateContentSize()
                        .fillMaxWidth()
                        .height(height = if (expanded) Int.MAX_VALUE.dp else dp0),
                    text = titles,
                    fontSize = sp10,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun ActorAndCrewComponent(
    movie: Movie,
    goToPeople: (Int) -> Unit
) {
    if (movie.credits?.crew.isNullOrEmpty() && movie.credits?.cast.isNullOrEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = stringResource(id = R.string.movie_crew_not_found)
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .testTag(tag = "castAndCrew")
                .fillMaxSize()
        ) {
            item {
                movie.credits?.cast.takeIf { !it.isNullOrEmpty() }?.let { casts ->
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = dp16),
                        text = stringResource(id = R.string.movie_actor),
                        fontSize = sp20,
                        textAlign = TextAlign.Center
                    )
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        contentPadding = PaddingValues(horizontal = dp16),
                        horizontalArrangement = Arrangement.spacedBy(space = dp10)
                    ) {
                        items(
                            items = casts
                        ) { cast ->
                            CreditInfoComponent(creditInfo = cast, goToPeople = goToPeople)
                        }
                    }
                }
                movie.credits?.crew.takeIf { !it.isNullOrEmpty() }?.let { crews ->
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = dp16),
                        text = stringResource(id = R.string.movie_staff),
                        fontSize = sp20,
                        textAlign = TextAlign.Center
                    )
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        contentPadding = PaddingValues(horizontal = dp16),
                        horizontalArrangement = Arrangement.spacedBy(space = dp10)
                    ) {
                        items(
                            items = crews
                        ) { crew ->
                            CreditInfoComponent(creditInfo = crew, goToPeople = goToPeople)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreditInfoComponent(
    creditInfo: CreditInfo,
    goToPeople: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .width(width = dp200)
            .wrapContentHeight()
            .bounceClick { goToPeople(creditInfo.id ?: -1) }
    ) {
        when (creditInfo) {
            is Cast -> {
                DynamicAsyncImageLoader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(ratio = PEOPLE_IMAGE_RATIO)
                        .clip(shape = RoundedCornerShape(size = dp10)),
                    source = creditInfo.profilePath ?: "",
                    contentDescription = "ProfileImage"
                )
                Text(
                    text = creditInfo.character ?: "",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = sp12,
                )
                Text(
                    text = creditInfo.name ?: "",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = sp15
                )
                Text(
                    text = creditInfo.originalName ?: "",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = sp15
                )
            }
            is Crew -> {
                DynamicAsyncImageLoader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(ratio = PEOPLE_IMAGE_RATIO)
                        .clip(shape = RoundedCornerShape(size = dp10)),
                    source = creditInfo.profilePath ?: "",
                    contentDescription = "ProfileImage"
                )
                Text(
                    text = creditInfo.department ?: "",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = sp12
                )
                Text(
                    text = creditInfo.name ?: "",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = sp15
                )
                Text(
                    text = creditInfo.originalName ?: "",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = sp15
                )
                Text(
                    text = creditInfo.job ?: "",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = sp12
                )
            }
        }
    }
}

@Composable
fun SimilarMovieComponent(
    similarMovies: LazyPagingItems<Movie>,
    goToMovie: (Int) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (similarMovies.loadState.refresh is LoadState.Loading) {
            CircularProgressComponent(modifier = Modifier.align(alignment = Alignment.Center))
        } else if (similarMovies.loadState.refresh is LoadState.Error) {
            val message = (similarMovies.loadState.refresh as? LoadState.Error)?.error?.message
                ?: (similarMovies.loadState.append as? LoadState.Error)?.error?.message
                ?: stringResource(id = com.bowoon.movie.core.network.R.string.something_wrong)

            ConfirmDialog(
                title = stringResource(id = com.bowoon.movie.core.network.R.string.network_failed),
                message = message,
                confirmPair = stringResource(id = com.bowoon.movie.core.ui.R.string.retry_message) to { similarMovies.retry() },
                dismissPair = stringResource(id = com.bowoon.movie.core.ui.R.string.confirm_message) to {}
            )
        }

        MovieList(
            similarMovies = similarMovies,
            goToMovie = goToMovie
        )
    }
}

@Composable
fun BoxScope.MovieList(
    similarMovies: LazyPagingItems<Movie>,
    goToMovie: (Int) -> Unit
) {
    if (similarMovies.itemCount == 0 && similarMovies.loadState.refresh !is LoadState.Loading) {
        Text(
            modifier = Modifier.align(alignment = Alignment.Center),
            text = stringResource(id = R.string.similar_movie_not_found)
        )
    } else {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = dp100),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(all = dp10),
            horizontalArrangement = Arrangement.spacedBy(space = dp10),
            verticalArrangement = Arrangement.spacedBy(space = dp10)
        ) {
            items(
                count = similarMovies.itemCount
            ) { index ->
                Box(
                    modifier = Modifier
                        .width(dp200)
                        .wrapContentHeight()
                        .bounceClick { goToMovie(similarMovies[index]?.id ?: -1) }
                ) {
                    DynamicAsyncImageLoader(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(POSTER_IMAGE_RATIO)
                            .clip(shape = RoundedCornerShape(size = dp10)),
                        source = similarMovies[index]?.posterPath ?: "",
                        contentDescription = "SimilarMoviePoster"
                    )
                }
            }

            if (similarMovies.loadState.append is LoadState.Loading) {
                item(span = { GridItemSpan(currentLineSpan = maxLineSpan) }) {
                    CircularProgressComponent(
                        modifier = Modifier
                            .wrapContentSize()
                            .align(Alignment.Center)
                    )
                }
            }
            if (similarMovies.loadState.append is LoadState.Error) {
                item(span = { GridItemSpan(currentLineSpan = maxLineSpan) }) {
                    PagingAppendErrorComponent(retry = { similarMovies.retry() })
                }
            }
        }
    }
}