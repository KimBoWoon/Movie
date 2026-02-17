package com.bowoon.home

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
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
import com.bowoon.common.Log
import com.bowoon.common.isSystemInDarkTheme
import com.bowoon.data.util.PEOPLE_IMAGE_RATIO
import com.bowoon.data.util.POSTER_IMAGE_RATIO
import com.bowoon.firebase.LocalFirebaseLogHelper
import com.bowoon.model.Media
import com.bowoon.model.Movie
import com.bowoon.model.TrendingMovieResult
import com.bowoon.model.TrendingPeopleResult
import com.bowoon.model.TrendingTvResult
import com.bowoon.movie.feature.home.R
import com.bowoon.ui.components.CircularProgressComponent
import com.bowoon.ui.image.DynamicAsyncImageLoader
import com.bowoon.ui.utils.bounceClick
import com.bowoon.ui.utils.dp0
import com.bowoon.ui.utils.dp1
import com.bowoon.ui.utils.dp10
import com.bowoon.ui.utils.dp150
import com.bowoon.ui.utils.dp16
import com.bowoon.ui.utils.dp230
import com.bowoon.ui.utils.dp30
import com.bowoon.ui.utils.dp60
import com.bowoon.ui.utils.sp10
import com.bowoon.ui.utils.sp8

@Composable
fun HomeScreen(
    goToMovie: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    goToTv: (Int) -> Unit,
    viewModel: HomeVM = hiltViewModel()
) {
    LocalFirebaseLogHelper.current.sendLog("HomeScreen", "init screen")

    val mainMenuState by viewModel.mainMenu.collectAsStateWithLifecycle()
    val trendingMovieTimeWindow by viewModel.trendingMovieTimeWindow.collectAsStateWithLifecycle()
    val trendingPeopleTimeWindow by viewModel.trendingPeopleTimeWindow.collectAsStateWithLifecycle()
    val trendingTvTimeWindow by viewModel.trendingTvTimeWindow.collectAsStateWithLifecycle()

    HomeScreen(
        mainMenuState = mainMenuState,
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
    mainMenuState: MainMenuState,
    trendingMovieTimeWindow: TimeWindow,
    updateTrendingMovieTimeWindow: (TimeWindow) -> Unit,
    trendingPeopleTimeWindow: TimeWindow,
    updateTrendingPeopleTimeWindow: (TimeWindow) -> Unit,
    trendingTvTimeWindow: TimeWindow,
    updateTrendingTvTimeWindow: (TimeWindow) -> Unit,
    goToMovie: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    goToTv: (Int) -> Unit,
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

                val lazyListState = rememberLazyListState()

                MainComponent(
                    lazyListState = lazyListState,
                    nowPlayingMovies = mainMenuState.nowPlayingMoviePager.collectAsLazyPagingItems(),
                    upComingMovies = mainMenuState.upComingMoviePager.collectAsLazyPagingItems(),
                    trendingMovie = mainMenuState.trendingMoviePager.collectAsLazyPagingItems(),
                    trendingMovieTimeWindow = trendingMovieTimeWindow,
                    updateTrendingMovieTimeWindow = updateTrendingMovieTimeWindow,
                    trendingPeople = mainMenuState.trendingPeoplePager.collectAsLazyPagingItems(),
                    trendingPeopleTimeWindow = trendingPeopleTimeWindow,
                    updateTrendingPeopleTimeWindow = updateTrendingPeopleTimeWindow,
                    trendingTv = mainMenuState.trendingTvPager.collectAsLazyPagingItems(),
                    trendingTvTimeWindow = trendingTvTimeWindow,
                    updateTrendingTvTimeWindow = updateTrendingTvTimeWindow,
                    goToMovie = goToMovie,
                    goToPeople = goToPeople,
                    goToTv = goToTv
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
    lazyListState: LazyListState,
    nowPlayingMovies: LazyPagingItems<Movie>,
    upComingMovies: LazyPagingItems<Movie>,
    trendingMovie: LazyPagingItems<TrendingMovieResult>,
    trendingMovieTimeWindow: TimeWindow,
    updateTrendingMovieTimeWindow: (TimeWindow) -> Unit,
    trendingPeople: LazyPagingItems<TrendingPeopleResult>,
    trendingPeopleTimeWindow: TimeWindow,
    updateTrendingPeopleTimeWindow: (TimeWindow) -> Unit,
    trendingTv: LazyPagingItems<TrendingTvResult>,
    trendingTvTimeWindow: TimeWindow,
    updateTrendingTvTimeWindow: (TimeWindow) -> Unit,
    goToMovie: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    goToTv: (Int) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        val nowPlayingMoviesTitle = stringResource(id = R.string.now_playing_movies)
        val upcomingMoviesTitle = stringResource(id = R.string.upcoming_movies)
        val trendingMovieTitle = stringResource(id = R.string.trending_movie)
        val trendingPeopleTitle = stringResource(id = R.string.trending_people)
        val trendingTvTitle = stringResource(id = R.string.trending_tv)

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
            trendingList(
                title = trendingMovieTitle,
                timeWindow = trendingMovieTimeWindow,
                onChangeTimeWindow = { updateTrendingMovieTimeWindow(it) },
                goToDestination = goToMovie,
                trending = trendingMovie
            )
            trendingList(
                title = trendingPeopleTitle,
                timeWindow = trendingPeopleTimeWindow,
                onChangeTimeWindow = { updateTrendingPeopleTimeWindow(it) },
                goToDestination = goToPeople,
                trending = trendingPeople
            )
            trendingList(
                title = trendingTvTitle,
                timeWindow = trendingTvTimeWindow,
                onChangeTimeWindow = { updateTrendingTvTimeWindow(it) },
                goToDestination = goToTv,
                trending = trendingTv
            )
        }
    }
}

@Composable
fun TimeWindowSwitch(
    timeWindow: TimeWindow,
    onChangeTimeWindow: (TimeWindow) -> Unit
) {
    var width by remember { mutableIntStateOf(value = 0) }
    val timeWindowAnimation by animateDpAsState(
        targetValue = if (timeWindow == TimeWindow.DAY) dp0 else width.dp,
        animationSpec = tween(durationMillis = 200),
        label = "TimeWindowAnimation",
    )
    val isDarkMode = LocalContext.current.resources.configuration.isSystemInDarkTheme

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
                    .onSizeChanged { width = it.width },
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

fun LazyListScope.trendingList(
    title: String,
    timeWindow: TimeWindow,
    onChangeTimeWindow: (TimeWindow) -> Unit,
    goToDestination: (Int) -> Unit,
    trending: LazyPagingItems<out Media>
) {
    item {
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
                    modifier = Modifier.wrapContentSize(),
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
                        goToMovie = goToMovie
                    )
                }
            }
        }
    }
}

@Composable
fun MediaItem(
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