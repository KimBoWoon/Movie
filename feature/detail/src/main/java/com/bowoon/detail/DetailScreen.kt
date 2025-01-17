package com.bowoon.detail

import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.background
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.bowoon.data.util.PEOPLE_IMAGE_RATIO
import com.bowoon.data.util.POSTER_IMAGE_RATIO
import com.bowoon.detail.navigation.navigateToDetail
import com.bowoon.model.MovieDetail
import com.bowoon.model.MovieDetailTab
import com.bowoon.model.tmdb.TMBDMovieDetailVideos
import com.bowoon.model.tmdb.TMDBMovieDetailCast
import com.bowoon.model.tmdb.TMDBMovieDetailCountry
import com.bowoon.model.tmdb.TMDBMovieDetailCrew
import com.bowoon.model.tmdb.TMDBMovieDetailReleases
import com.bowoon.model.tmdb.TMDBMovieDetailVideoResult
import com.bowoon.people.navigation.navigateToPeople
import com.bowoon.ui.ConfirmDialog
import com.bowoon.ui.FavoriteButton
import com.bowoon.ui.ModalBottomSheetDialog
import com.bowoon.ui.Title
import com.bowoon.ui.dp10
import com.bowoon.ui.dp100
import com.bowoon.ui.dp16
import com.bowoon.ui.dp200
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
        insertFavoriteMovie = viewModel::insertMovie,
        deleteFavoriteMovie = viewModel::deleteMovie,
        onShowSnackbar = onShowSnackbar,
        restart = viewModel::restart
    )
}

@Composable
fun DetailScreen(
    state: MovieDetailState,
    navController: NavController,
    insertFavoriteMovie: (MovieDetail) -> Unit,
    deleteFavoriteMovie: (MovieDetail) -> Unit,
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
                        if (it.isFavorite) {
                            deleteFavoriteMovie(it)
                        } else {
                            insertFavoriteMovie(it)
                        }
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
                    onPeopleClick = { id -> navController.navigateToPeople(id) },
                    favoriteMovies = it.favoriteMovies ?: emptyList(),
                    insertFavoriteMovie = insertFavoriteMovie,
                    deleteFavoriteMovie = deleteFavoriteMovie
                )
            }
        }
    }
}

@Composable
fun MovieDetail(
    movieDetail: MovieDetail,
    onMovieClick: (Int) -> Unit,
    onPeopleClick: (Int) -> Unit,
    favoriteMovies: List<MovieDetail>,
    insertFavoriteMovie: (MovieDetail) -> Unit,
    deleteFavoriteMovie: (MovieDetail) -> Unit
) {
    Column {
        VideosComponent(movieDetail)

        TabComponent(
            movie = movieDetail,
            onMovieClick = onMovieClick,
            onPeopleClick = onPeopleClick,
            favoriteMovies = favoriteMovies,
            insertFavoriteMovie = insertFavoriteMovie,
            deleteFavoriteMovie = deleteFavoriteMovie
        )
    }
}

@OptIn(UnstableApi::class)
@Composable
fun VideosComponent(movie: MovieDetail) {
    val vodList = movie.videos?.results?.mapNotNull { it.key } ?: emptyList()

    if (vodList.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(color = Color.Black)
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "영상이 없습니다.",
                color = Color.White,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    } else {
        val context = LocalContext.current
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
                                }
                            }
                        }
                    )
                    view
                }
            )
        }
    }
}

@Composable
fun TabComponent(
    movie: MovieDetail,
    onMovieClick: (Int) -> Unit,
    onPeopleClick: (Int) -> Unit,
    favoriteMovies: List<MovieDetail>,
    insertFavoriteMovie: (MovieDetail) -> Unit,
    deleteFavoriteMovie: (MovieDetail) -> Unit
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
            when (tabList[index].label) {
                MovieDetailTab.MOVIE_INFO.label -> MovieInfoComponent(movie = movie)
                MovieDetailTab.ACTOR_AND_CREW.label -> ActorAndCrewComponent(
                    movie = movie,
                    onPeopleClick = onPeopleClick
                )
                MovieDetailTab.POSTER.label -> ImageComponent(movie = movie)
                MovieDetailTab.SIMILAR.label -> SimilarMovieComponent(
                    movie = movie,
                    onMovieClick = onMovieClick,
                    favoriteMovies = favoriteMovies,
                    insertFavoriteMovie = insertFavoriteMovie,
                    deleteFavoriteMovie = deleteFavoriteMovie
                )
            }
        }
    }
}

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageComponent(
    movie: MovieDetail
) {
    var isShowing by remember { mutableStateOf(false) }
    var index by remember { mutableIntStateOf(0) }
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val items = (movie.images?.posters ?: emptyList()) + (movie.images?.backdrops ?: emptyList())

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(dp100),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = dp10),
        horizontalArrangement = Arrangement.spacedBy(dp10),
        verticalItemSpacing = dp10
    ) {
        items(
            items = items,
            key = { it }
        ) {
            DynamicAsyncImageLoader(
                modifier = Modifier
                    .width(dp200)
                    .aspectRatio(it.aspectRatio?.toFloat() ?: 1f)
                    .clickable {
                        index = items.indexOf(it)
                        isShowing = true
                    },
                source = "${it.filePath}",
                contentDescription = "moviePoster"
            )
        }
    }

    if (isShowing) {
        ModalBottomSheetDialog(
            state = modalBottomSheetState,
            scope = scope,
            index = index,
            imageList = items,
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
    movie: MovieDetail,
    onPeopleClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dp16),
                text = "배우",
                fontSize = sp20,
                textAlign = TextAlign.Center
            )
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                items(
                    items = movie.credits?.cast ?: emptyList()
                ) { cast ->
                    StaffComponent(tmdbMovieDetailCast = cast, onPeopleClick = onPeopleClick)
                }
            }
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dp16),
                text = "스태프",
                fontSize = sp20,
                textAlign = TextAlign.Center
            )
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                items(
                    items = movie.credits?.crew ?: emptyList()
                ) { cast ->
                    StaffComponent(tmdbMovieDetailCrew = cast, onPeopleClick = onPeopleClick)
                }
            }
        }
    }
}

@Composable
fun StaffComponent(
    tmdbMovieDetailCast: TMDBMovieDetailCast,
    onPeopleClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .width(dp200)
            .wrapContentHeight()
            .clickable { onPeopleClick(tmdbMovieDetailCast.id ?: -1) }
    ) {
        DynamicAsyncImageLoader(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(PEOPLE_IMAGE_RATIO),
            source = tmdbMovieDetailCast.profilePath ?: "",
            contentDescription = "ProfileImage"
        )
        Text(
            text = tmdbMovieDetailCast.character ?: "",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = tmdbMovieDetailCast.name ?: "",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = tmdbMovieDetailCast.originalName ?: "",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun StaffComponent(
    tmdbMovieDetailCrew: TMDBMovieDetailCrew,
    onPeopleClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .width(dp200)
            .wrapContentHeight()
            .clickable { onPeopleClick(tmdbMovieDetailCrew.id ?: -1) }
    ) {
        DynamicAsyncImageLoader(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(PEOPLE_IMAGE_RATIO),
            source = tmdbMovieDetailCrew.profilePath ?: "",
            contentDescription = "ProfileImage"
        )
        Text(
            text = tmdbMovieDetailCrew.department ?: "",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = tmdbMovieDetailCrew.name ?: "",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = tmdbMovieDetailCrew.originalName ?: "",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = tmdbMovieDetailCrew.job ?: "",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun SimilarMovieComponent(
    movie: MovieDetail,
    onMovieClick: (Int) -> Unit,
    favoriteMovies: List<MovieDetail>,
    insertFavoriteMovie: (MovieDetail) -> Unit,
    deleteFavoriteMovie: (MovieDetail) -> Unit
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
                modifier = Modifier
                    .width(dp200)
                    .wrapContentHeight()
                    .clickable { onMovieClick(detail.id ?: -1) }
            ) {
                DynamicAsyncImageLoader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(POSTER_IMAGE_RATIO),
                    source = detail.posterPath ?: "",
                    contentDescription = "BoxOfficePoster"
                )
                FavoriteButton(
                    modifier = Modifier
                        .wrapContentSize()
                        .align(Alignment.TopEnd),
                    isFavorite = favoriteMovies.find { it.id == detail.id } != null,
                    onClick = {
                        if (favoriteMovies.find { it.id == detail.id } != null) {
                            deleteFavoriteMovie(detail)
                        } else {
                            insertFavoriteMovie(detail)
                        }
                    }
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