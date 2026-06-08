package com.cheeke.surfy.home

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.cheeke.surfy.analytics.TrackScreenViewEvent
import com.cheeke.surfy.common.InfinitePager
import com.cheeke.surfy.common.Log
import com.cheeke.surfy.common.ScrollTopEvent
import com.cheeke.surfy.common.scrollTopEvent
import com.cheeke.surfy.data.util.PEOPLE_IMAGE_RATIO
import com.cheeke.surfy.data.util.POSTER_IMAGE_RATIO
import com.cheeke.surfy.feature.home.R
import com.cheeke.surfy.firebase.LocalFirebaseLogHelper
import com.cheeke.surfy.model.Media
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.model.TrendingMediaResult
import com.cheeke.surfy.ui.components.CircularProgressComponent
import com.cheeke.surfy.ui.image.DynamicAsyncImageLoader
import com.cheeke.surfy.ui.utils.bounceClick
import com.cheeke.surfy.ui.utils.dp0
import com.cheeke.surfy.ui.utils.dp1
import com.cheeke.surfy.ui.utils.dp10
import com.cheeke.surfy.ui.utils.dp110
import com.cheeke.surfy.ui.utils.dp12
import com.cheeke.surfy.ui.utils.dp150
import com.cheeke.surfy.ui.utils.dp16
import com.cheeke.surfy.ui.utils.dp20
import com.cheeke.surfy.ui.utils.dp220
import com.cheeke.surfy.ui.utils.dp230
import com.cheeke.surfy.ui.utils.dp28
import com.cheeke.surfy.ui.utils.dp30
import com.cheeke.surfy.ui.utils.dp5
import com.cheeke.surfy.ui.utils.dp60
import com.cheeke.surfy.ui.utils.dp8
import com.cheeke.surfy.ui.utils.sp10
import com.cheeke.surfy.ui.utils.sp8

@Composable
fun HomeScreen(
    goToMovie: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    goToTv: (Int) -> Unit,
    viewModel: HomeVM = hiltViewModel()
) {
    LocalFirebaseLogHelper.current.sendLog("HomeScreen", "init screen")
    TrackScreenViewEvent(screenName = "HomeScreen")

    val homeState by viewModel.homeUiState.collectAsStateWithLifecycle()
    val nowPlayingMovies = viewModel.nowPlayingMoviePaging.collectAsLazyPagingItems()
    val upComingMovies = viewModel.upComingMoviePaging.collectAsLazyPagingItems()
    val trendingMovies = viewModel.trendingMoviePaging.collectAsLazyPagingItems()
    val trendingPeoples = viewModel.trendingPeoplePaging.collectAsLazyPagingItems()
    val trendingTvs = viewModel.trendingTvPaging.collectAsLazyPagingItems()
    val trendingMovieTimeWindow by viewModel.trendingMovieTimeWindow.collectAsStateWithLifecycle()
    val trendingPeopleTimeWindow by viewModel.trendingPeopleTimeWindow.collectAsStateWithLifecycle()
    val trendingTvTimeWindow by viewModel.trendingTvTimeWindow.collectAsStateWithLifecycle()

    HomeScreen(
        homeUiState = homeState,
        nowPlayingMovies = nowPlayingMovies,
        upComingMovies = upComingMovies,
        trendingMovies = trendingMovies,
        trendingPeoples = trendingPeoples,
        trendingTvs = trendingTvs,
        trendingMovieTimeWindow = trendingMovieTimeWindow,
        updateTrendingMovieTimeWindow = viewModel::updateTrendingMovieTimeWindow,
        trendingPeopleTimeWindow = trendingPeopleTimeWindow,
        updateTrendingPeopleTimeWindow = viewModel::updateTrendingPeopleTimeWindow,
        trendingTvTimeWindow = trendingTvTimeWindow,
        updateTrendingTvTimeWindow = viewModel::updateTrendingTvTimeWindow,
        goToMovie = goToMovie,
        goToPeople = goToPeople,
        goToTv = goToTv
    )
}

@Composable
fun HomeScreen(
    homeUiState: HomeState,
    nowPlayingMovies: LazyPagingItems<Movie>,
    upComingMovies: LazyPagingItems<Movie>,
    trendingMovies: LazyPagingItems<TrendingMediaResult>,
    trendingPeoples: LazyPagingItems<TrendingMediaResult>,
    trendingTvs: LazyPagingItems<TrendingMediaResult>,
    trendingMovieTimeWindow: TimeWindow,
    updateTrendingMovieTimeWindow: (TimeWindow) -> Unit,
    trendingPeopleTimeWindow: TimeWindow,
    updateTrendingPeopleTimeWindow: (TimeWindow) -> Unit,
    trendingTvTimeWindow: TimeWindow,
    updateTrendingTvTimeWindow: (TimeWindow) -> Unit,
    goToMovie: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    goToTv: (Int) -> Unit
) {
    LocalFirebaseLogHelper.current.sendLog("HomeScreen", "init screen")

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (homeUiState) {
            is HomeState.Loading -> {
                Log.d("loading...")
                LocalFirebaseLogHelper.current.sendLog("HomeScreen", "data loading...")

                CircularProgressComponent(
                    modifier = Modifier
                        .semantics { contentDescription = "homeLoading" }
                        .align(Alignment.Center)
                )
            }
            is HomeState.Success -> {
                LocalFirebaseLogHelper.current.sendLog("HomeScreen", "data load success")
                Log.d("$nowPlayingMovies, $upComingMovies, $trendingMovies, $trendingPeoples, $trendingTvs")

                HomeComponent(
                    popularMovies = homeUiState.homeUiState.popularMovies,
                    nowPlayingMovies = nowPlayingMovies,
                    upComingMovies = upComingMovies,
                    trendingMovie = trendingMovies,
                    trendingMovieTimeWindow = trendingMovieTimeWindow,
                    updateTrendingMovieTimeWindow = updateTrendingMovieTimeWindow,
                    trendingPeople = trendingPeoples,
                    trendingPeopleTimeWindow = trendingPeopleTimeWindow,
                    updateTrendingPeopleTimeWindow = updateTrendingPeopleTimeWindow,
                    trendingTv = trendingTvs,
                    trendingTvTimeWindow = trendingTvTimeWindow,
                    updateTrendingTvTimeWindow = updateTrendingTvTimeWindow,
                    goToMovie = goToMovie,
                    goToPeople = goToPeople,
                    goToTv = goToTv
                )
            }
            is HomeState.Error -> {
                LocalFirebaseLogHelper.current.sendLog("HomeScreen", "data load Error > ${homeUiState.throwable.message}")
                Log.e("${homeUiState.throwable.message}")
                Text(
                    modifier = Modifier.fillMaxSize(),
                    text = homeUiState.throwable.message ?: stringResource(id = com.cheeke.surfy.core.network.R.string.something_wrong)
                )
            }
        }
    }
}

@Composable
fun HomeComponent(
    popularMovies: List<Movie>,
    nowPlayingMovies: LazyPagingItems<Movie>,
    upComingMovies: LazyPagingItems<Movie>,
    trendingMovie: LazyPagingItems<TrendingMediaResult>,
    trendingMovieTimeWindow: TimeWindow,
    updateTrendingMovieTimeWindow: (TimeWindow) -> Unit,
    trendingPeople: LazyPagingItems<TrendingMediaResult>,
    trendingPeopleTimeWindow: TimeWindow,
    updateTrendingPeopleTimeWindow: (TimeWindow) -> Unit,
    trendingTv: LazyPagingItems<TrendingMediaResult>,
    trendingTvTimeWindow: TimeWindow,
    updateTrendingTvTimeWindow: (TimeWindow) -> Unit,
    goToMovie: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    goToTv: (Int) -> Unit,
) {
    val scrollState = rememberScrollState()
    val nowPlayingMoviesTitle = stringResource(id = R.string.now_playing_movies)
    val upcomingMoviesTitle = stringResource(id = R.string.upcoming_movies)
    val trendingMovieTitle = stringResource(id = R.string.trending_movie)
    val trendingPeopleTitle = stringResource(id = R.string.trending_people)
    val trendingTvTitle = stringResource(id = R.string.trending_tv)

    LaunchedEffect(key1 = Unit) {
        scrollTopEvent.collect { event ->
            if (event is ScrollTopEvent.Home) {
                scrollState.scrollTo(value = 0)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = scrollState)
    ) {
        if (popularMovies.isNotEmpty()) {
            TodayRecommendMovieComponent(
                movies = popularMovies,
                goToMovie = goToMovie
            )
        }

        if (nowPlayingMovies.itemCount != 0) {
            HorizontalMovieListComponent(
                title = nowPlayingMoviesTitle,
                pager = nowPlayingMovies,
                goToMovie = goToMovie
            )
        }
        if (upComingMovies.itemCount != 0) {
            HorizontalMovieListComponent(
                title = upcomingMoviesTitle,
                pager = upComingMovies,
                goToMovie = goToMovie
            )
        }
        TrendingList(
            title = trendingMovieTitle,
            timeWindow = trendingMovieTimeWindow,
            onChangeTimeWindow = { updateTrendingMovieTimeWindow(it) },
            goToDestination = goToMovie,
            trending = trendingMovie
        )
        TrendingList(
            title = trendingPeopleTitle,
            timeWindow = trendingPeopleTimeWindow,
            onChangeTimeWindow = { updateTrendingPeopleTimeWindow(it) },
            goToDestination = goToPeople,
            trending = trendingPeople
        )
        TrendingList(
            title = trendingTvTitle,
            timeWindow = trendingTvTimeWindow,
            onChangeTimeWindow = { updateTrendingTvTimeWindow(it) },
            goToDestination = goToTv,
            trending = trendingTv
        )
    }
}

@Composable
fun TimeWindowSwitch(
    timeWindow: TimeWindow,
    onChangeTimeWindow: (TimeWindow) -> Unit
) {
    var width by remember { mutableStateOf(value = dp0) }
    val timeWindowAnimation by animateDpAsState(
        targetValue = if (timeWindow == TimeWindow.DAY) dp0 else width,
        animationSpec = tween(durationMillis = 200),
        label = "TimeWindowAnimation",
    )
    val isDarkMode = isSystemInDarkTheme()
    val density = LocalDensity.current

    Box {
        Box(
            modifier = Modifier
                .offset { IntOffset(x = timeWindowAnimation.value.toInt(), y = 0) }
                .size(size = dp30)
                .border(
                    width = dp1,
                    color = if (isDarkMode) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.inverseSurface,
                    shape = RoundedCornerShape(size = dp10)
                )
                .background(
                    color = if (isDarkMode) MaterialTheme.colorScheme.inverseSurface else MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(size = dp10)
                )
        )
        Row(
            modifier = Modifier
                .width(width = dp60)
                .height(height = dp30)
                .clickable(interactionSource = null, indication = null) {
                    onChangeTimeWindow(if (timeWindow == TimeWindow.DAY) TimeWindow.WEEK else TimeWindow.DAY)
                }
        ) {
            Box(
                modifier = Modifier
                    .weight(weight = 1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.time_window_day),
                    fontSize = sp10,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                    textAlign = TextAlign.Center,
                    color = when (isDarkMode) {
                        true -> if (timeWindow == TimeWindow.DAY) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.inverseSurface
                        false -> if (timeWindow == TimeWindow.DAY) MaterialTheme.colorScheme.inverseSurface else MaterialTheme.colorScheme.surface
                    }
                )
            }
            Box(
                modifier = Modifier
                    .weight(weight = 1f)
                    .fillMaxHeight()
                    .onSizeChanged {
                        width = if (timeWindow == TimeWindow.DAY) 0.dp else with(receiver = density) { it.width.dp }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.time_window_week),
                    fontSize = sp10,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                    textAlign = TextAlign.Center,
                    color = when (isDarkMode) {
                        true -> if (timeWindow == TimeWindow.WEEK) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.inverseSurface
                        false -> if (timeWindow == TimeWindow.WEEK) MaterialTheme.colorScheme.inverseSurface else MaterialTheme.colorScheme.surface
                    }
                )
            }
        }
    }
}

@Composable
fun TrendingList(
    title: String,
    timeWindow: TimeWindow,
    onChangeTimeWindow: (TimeWindow) -> Unit,
    goToDestination: (Int) -> Unit,
    trending: LazyPagingItems<out Media>
) {
    Column(
        modifier = Modifier.wrapContentSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dp16),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(vertical = dp16),
                text = title
            )
            TimeWindowSwitch(
                timeWindow = timeWindow,
                onChangeTimeWindow = { onChangeTimeWindow(it) }
            )
        }
        if (trending.itemCount != 0) {
            LazyRow(
                modifier = Modifier.semantics { contentDescription = title }.wrapContentSize(),
                contentPadding = PaddingValues(horizontal = dp16),
                horizontalArrangement = Arrangement.spacedBy(space = dp16)
            ) {
                items(
                    count = trending.itemCount,
                    key = { index -> "${trending.peek(index)?.id}_${index}_${trending.peek(index)?.title}" }
                ) { index ->
                    trending[index]?.let { trendingTv ->
                        Column(
                            modifier = Modifier
                                .width(width = dp150)
                                .height(height = dp230)
                                .bounceClick { goToDestination(trendingTv.id ?: -1) }
                        ) {
                            Box(
                                modifier = Modifier.wrapContentSize()
                            ) {
                                DynamicAsyncImageLoader(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(PEOPLE_IMAGE_RATIO)
                                        .clip(shape = RoundedCornerShape(size = dp10)),
                                    source = trendingTv.posterPath ?: "",
                                    contentDescription = "BoxOfficePoster"
                                )
                            }
                            Text(
                                text = trendingTv.title ?: "",
                                fontSize = sp10,
                                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = trendingTv.originalTitle ?: "",
                                fontSize = sp8,
                                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height = dp230),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressComponent()
            }
        }
    }
}

@Composable
fun HorizontalMovieListComponent(
    title: String,
    pager: LazyPagingItems<Movie>,
    goToMovie: (Int) -> Unit
) {
    Text(
        modifier = Modifier
            .padding(all = dp16)
            .fillMaxWidth(),
        text = title
    )
    LazyRow(
        modifier = Modifier
            .semantics {
                contentDescription =
                    if (title == "상영중인 영화") "nowPlayingMovies" else "upComingMovies"
            }
            .wrapContentSize(),
        contentPadding = PaddingValues(horizontal = dp16),
        horizontalArrangement = Arrangement.spacedBy(space = dp16)
    ) {
        items(
            count = pager.itemCount,
            key = { index -> "${pager.peek(index)?.id}_${index}_${pager.peek(index)?.title}" }
        ) { index ->
            pager[index]?.let {
                MediaItem(
                    movie = it,
                    goToMovie = goToMovie,
                    type = if (title == "상영중인 영화") "nowPlayingMovies" else "upComingMovies"
                )
            }
        }
    }
}

@Composable
fun MediaItem(
    movie: Movie,
    goToMovie: (Int) -> Unit,
    type: String
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.wrapContentHeight().weight(weight = 1f).padding(end = dp5),
                text = movie.title ?: "",
                fontSize = sp10,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "%.2f".format(movie.voteAverage),
                fontSize = sp10,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (type == "upComingMovies") {
            Text(
                text = movie.releaseDate ?: "",
                fontSize = sp8,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun TodayRecommendMovieComponent(
    movies: List<Movie>,
    goToMovie: (Int) -> Unit
) {
    Column {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = dp16),
            text = "오늘의 추천 영화"
        )
        InfinitePager<Movie>(
            contentPadding = PaddingValues(horizontal = dp20),
            pageSpacing = dp12,
            modifier = Modifier.semantics { contentDescription = "todayRecommendMovies" }.fillMaxWidth(),
            isVisibleIndicator = true,
            isAutoScroll = true,
            items = movies,
            content = {
                RecommendMovie(
                    movie = it,
                    goToMovie = goToMovie,
                )
            }
        )
    }
}

@Composable
private fun RecommendMovie(
    movie: Movie,
    goToMovie: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(size = dp28))
            .aspectRatio(ratio = 0.82f)
            .clickable(onClick = { goToMovie(movie.id ?: -1) })
    ) {
        DynamicAsyncImageLoader(
            source = movie.posterPath ?: "",
            contentDescription = movie.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height = dp110)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.32f),
                            Color.Transparent
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(height = dp220)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.2f),
                            Color.Black.copy(alpha = 0.9f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(all = dp20)
        ) {
            if (movie.voteCount != null && movie.voteAverage != null) {
                Text(
                    text = "추천수 ${movie.voteCount} · 평점 ${"%.2f".format(movie.voteAverage)}",
                    color = Color(color = 0xFFD7D2DF),
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(modifier = Modifier.height(height = dp8))
            }

            movie.title?.takeIf { it.isNotEmpty() }?.let {
                Text(
                    text = it,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(height = dp8))
            }

            movie.releaseDate?.takeIf { it.isNotEmpty() }?.let {
                Text(
                    text = it,
                    color = Color(color = 0xFFD0CED6),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(height = dp16))
            }
        }
    }
}