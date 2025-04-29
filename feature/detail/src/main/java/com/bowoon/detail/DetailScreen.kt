package com.bowoon.detail

import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.bowoon.common.Log
import com.bowoon.data.repository.LocalMovieAppDataComposition
import com.bowoon.data.util.PEOPLE_IMAGE_RATIO
import com.bowoon.data.util.POSTER_IMAGE_RATIO
import com.bowoon.data.util.VIDEO_RATIO
import com.bowoon.firebase.LocalFirebaseLogHelper
import com.bowoon.model.Cast
import com.bowoon.model.Crew
import com.bowoon.model.Favorite
import com.bowoon.model.Movie
import com.bowoon.model.MovieDetail
import com.bowoon.model.MovieDetailTab
import com.bowoon.model.MovieSeries
import com.bowoon.model.PagingStatus
import com.bowoon.movie.feature.detail.R
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
import com.bowoon.ui.utils.dp10
import com.bowoon.ui.utils.dp100
import com.bowoon.ui.utils.dp16
import com.bowoon.ui.utils.dp20
import com.bowoon.ui.utils.dp200
import com.bowoon.ui.utils.dp300
import com.bowoon.ui.utils.dp32
import com.bowoon.ui.utils.dp5
import com.bowoon.ui.utils.dp8
import com.bowoon.ui.utils.sp10
import com.bowoon.ui.utils.sp12
import com.bowoon.ui.utils.sp15
import com.bowoon.ui.utils.sp20
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

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

    DetailScreen(
        detailState = detailState,
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
    goToMovie: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    goToBack: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    insertFavoriteMovie: (Favorite) -> Unit,
    deleteFavoriteMovie: (Favorite) -> Unit,
    restart: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var detail by remember { mutableStateOf(DetailState.Success(null, null, emptyFlow())) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (detailState) {
            is DetailState.Loading -> {
                Log.d("loading...")
                CircularProgressIndicator(
                    modifier = Modifier
                        .testTag(tag = "detailScreenLoading")
                        .align(Alignment.Center)
                )
            }
            is DetailState.Success -> {
                Log.d("${detailState.detail}")
                detail = detailState
            }
            is DetailState.Error -> {
                Log.e("${detailState.throwable.message}")

                ConfirmDialog(
                    title = stringResource(com.bowoon.movie.core.network.R.string.network_failed),
                    message = "${detailState.throwable.message}",
                    confirmPair = stringResource(com.bowoon.movie.core.ui.R.string.retry_message) to { restart() },
                    dismissPair = stringResource(com.bowoon.movie.core.ui.R.string.back_message) to goToBack
                )
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        detail.detail?.let {
            val favoriteMessage = if (it.isFavorite) stringResource(R.string.add_favorite_movie) else stringResource(R.string.remove_favorite_movie)

            TitleComponent(
                title = it.title ?: "",
                isFavorite = it.isFavorite,
                goToBack = goToBack,
                onFavoriteClick = {
                    val favorite = Favorite(
                        id = it.id,
                        title = it.title,
                        imagePath = it.posterPath
                    )
                    if (it.isFavorite) deleteFavoriteMovie(favorite) else insertFavoriteMovie(favorite)
                    scope.launch {
                        onShowSnackbar(favoriteMessage, null)
                    }
                }
            )

            MovieDetailComponent(
                movieDetail = it,
                movieSeries = detail.series,
                similarMovieState = detail.similarMovies.collectAsLazyPagingItems(),
                goToMovie = { id -> goToMovie(id) },
                goToPeople = { id -> goToPeople(id) }
            )
        }
    }
}

@Composable
fun MovieDetailComponent(
    movieDetail: MovieDetail,
    movieSeries: MovieSeries?,
    similarMovieState: LazyPagingItems<Movie>,
    goToMovie: (Int) -> Unit,
    goToPeople: (Int) -> Unit
) {
    Column {
        VideosComponent(movieDetail)

        val tabList = if (movieDetail.belongsToCollection == null) {
            MovieDetailTab.entries.map { it.label }.filter { it != MovieDetailTab.SERIES.label }
        } else {
            MovieDetailTab.entries.map { it.label }
        }
        val pagerState = rememberPagerState(
            initialPage = 0,
            pageCount = { tabList.size }
        )
        val scope = rememberCoroutineScope()
        val tabClickEvent: (Int, Int) -> Unit = { current, index ->
            scope.launch {
                pagerState.animateScrollToPage(index)
            }
        }

        TabComponent(
            tabs = tabList,
            pagerState = pagerState,
            tabClickEvent = tabClickEvent
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
                        MovieDetailTab.MOVIE_INFO.label -> MovieInfoComponent(movie = movieDetail)
                        MovieDetailTab.SERIES.label -> MovieSeriesComponent(
                            movieSeries = movieSeries,
                            goToMovie = goToMovie
                        )
                        MovieDetailTab.ADDITIONAL_INFO.label -> MovieAdditionalInfoComponent(
                            movie = movieDetail
                        )
                        MovieDetailTab.ACTOR_AND_CREW.label -> ActorAndCrewComponent(
                            movie = movieDetail,
                            goToPeople = goToPeople
                        )
                        MovieDetailTab.IMAGES.label -> ImageComponent(movie = movieDetail)
                        MovieDetailTab.SIMILAR.label -> SimilarMovieComponent(
                            similarMovieState = similarMovieState,
                            goToMovie = goToMovie
                        )
                    }
                }
            }
        }
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
                .aspectRatio(VIDEO_RATIO)
                .background(color = Color.Black)
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = stringResource(R.string.trailer_video_not_found),
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
                        (screenWidth / (VIDEO_RATIO)).toInt()
                    )
                    view.addYouTubePlayerListener(
                        object : AbstractYouTubePlayerListener() {
                            override fun onReady(youTubePlayer: YouTubePlayer) {
                                super.onReady(youTubePlayer)

                                when (movie.autoPlayTrailer) {
                                    true -> youTubePlayer.loadVideo(vodList[index], 0f)
                                    false -> youTubePlayer.cueVideo(vodList[index], 0f)
                                    else -> youTubePlayer.cueVideo(vodList[index], 0f)
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

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageComponent(
    movie: MovieDetail
) {
    val posterUrl = LocalMovieAppDataComposition.current.getImageUrl()
    var isShowing by remember { mutableStateOf(false) }
    var index by remember { mutableIntStateOf(0) }
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val items = ((movie.images?.posters ?: emptyList()) + (movie.images?.backdrops ?: emptyList())).map {
        it.copy(filePath = "$posterUrl${it.filePath}")
    }

    if (items.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = stringResource(R.string.movie_image_not_found))
        }
    } else {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(dp100),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(dp10),
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
                        .bounceClick {
                            index = items.indexOf(it)
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
fun MovieSeriesComponent(
    movieSeries: MovieSeries?,
    goToMovie: (Int) -> Unit,
) {
    val posterUrl = LocalMovieAppDataComposition.current.getImageUrl()

    LazyColumn(
        modifier = Modifier
            .semantics { contentDescription = "seriesList" }
            .fillMaxSize(),
        contentPadding = PaddingValues(horizontal = dp16, vertical = dp10),
        verticalArrangement = Arrangement.spacedBy(dp10)
    ) {
        movieSeries?.let {
            seriesInfoComponent(
                series = movieSeries
            )
            movieSeriesListComponent(
                series = movieSeries.parts ?: emptyList(),
                posterUrl = posterUrl,
                goToMovie = goToMovie
            )
        }
    }
}

@Composable
fun MovieAdditionalInfoComponent(
    movie: MovieDetail
) {
    val posterUrl = LocalMovieAppDataComposition.current.getImageUrl()
    var expanded by remember { mutableStateOf(false) }
    val titles = movie.alternativeTitles?.titles?.fold("") { acc, title -> if (acc.isEmpty()) "${title.title}" else "$acc\n${title.title}" } ?: ""

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            movie.productionCompanies.takeIf { !it.isNullOrEmpty() }?.let { production ->
                Text(
                    modifier = Modifier.fillMaxWidth().padding(vertical = dp10, horizontal = dp16),
                    text = stringResource(R.string.movie_production_companies),
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
                                modifier = Modifier.fillMaxWidth().height(dp300),
                                contentScale = ContentScale.Fit,
                                source = "$posterUrl$it",
                                contentDescription = "$posterUrl$it"
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
                        modifier = Modifier.animateRotation(expanded, 0f, 90f, 500),
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = "expandedArrow"
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        text = stringResource(R.string.movie_alternative_title),
                        fontSize = sp15
                    )
                }
                Text(
                    modifier = Modifier
                        .padding(top = dp5, start = dp16, end = dp16)
                        .animateContentSize()
                        .fillMaxWidth()
                        .height(if (expanded) Int.MAX_VALUE.dp else dp0),
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
fun MovieInfoComponent(
    movie: MovieDetail
) {
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
                    textAlign = TextAlign.Center
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
                    text = it.fold("") { acc, tmdbMovieDetailGenre -> if (acc.isEmpty()) "${tmdbMovieDetailGenre.name}" else "$acc, ${tmdbMovieDetailGenre.name}" },
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
                        text = stringResource(R.string.movie_runtime, it),
                        fontSize = sp10,
                        textAlign = TextAlign.Center
                    )
                }
                if (movie.runtime != null && movie.voteAverage != null) {
                    Text(
                        modifier = Modifier.padding(horizontal = dp5),
                        text = stringResource(R.string.movie_info_separator),
                        fontSize = sp10
                    )
                }
                movie.voteAverage?.let {
                    Text(
                        text = stringResource(R.string.movie_vote_average, it),
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
    }
}

@Composable
fun ActorAndCrewComponent(
    movie: MovieDetail,
    goToPeople: (Int) -> Unit
) {
    if (movie.credits?.crew.isNullOrEmpty() && movie.credits?.cast.isNullOrEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = stringResource(R.string.movie_crew_not_found)
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
                        text = stringResource(R.string.movie_actor),
                        fontSize = sp20,
                        textAlign = TextAlign.Center
                    )
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        contentPadding = PaddingValues(horizontal = dp16),
                        horizontalArrangement = Arrangement.spacedBy(dp10)
                    ) {
                        items(
                            items = casts
                        ) { cast ->
                            StaffComponent(tmdbMovieDetailCast = cast, goToPeople = goToPeople)
                        }
                    }
                }
                movie.credits?.crew.takeIf { !it.isNullOrEmpty() }?.let { crews ->
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = dp16),
                        text = stringResource(R.string.movie_staff),
                        fontSize = sp20,
                        textAlign = TextAlign.Center
                    )
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        contentPadding = PaddingValues(horizontal = dp16),
                        horizontalArrangement = Arrangement.spacedBy(dp10)
                    ) {
                        items(
                            items = crews
                        ) { crew ->
                            StaffComponent(tmdbMovieDetailCrew = crew, goToPeople = goToPeople)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StaffComponent(
    tmdbMovieDetailCast: Cast,
    goToPeople: (Int) -> Unit
) {
    val posterUrl = LocalMovieAppDataComposition.current.getImageUrl()

    Column(
        modifier = Modifier
            .width(dp200)
            .wrapContentHeight()
            .bounceClick { goToPeople(tmdbMovieDetailCast.id ?: -1) }
    ) {
        DynamicAsyncImageLoader(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(PEOPLE_IMAGE_RATIO),
            source = "$posterUrl${tmdbMovieDetailCast.profilePath}",
            contentDescription = "ProfileImage"
        )
        Text(
            text = tmdbMovieDetailCast.character ?: "",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = sp12,
        )
        Text(
            text = tmdbMovieDetailCast.name ?: "",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = sp15
        )
        Text(
            text = tmdbMovieDetailCast.originalName ?: "",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = sp15
        )
    }
}

@Composable
fun StaffComponent(
    tmdbMovieDetailCrew: Crew,
    goToPeople: (Int) -> Unit
) {
    val posterUrl = LocalMovieAppDataComposition.current.getImageUrl()

    Column(
        modifier = Modifier
            .width(dp200)
            .wrapContentHeight()
            .bounceClick { goToPeople(tmdbMovieDetailCrew.id ?: -1) }
    ) {
        DynamicAsyncImageLoader(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(PEOPLE_IMAGE_RATIO),
            source = "$posterUrl${tmdbMovieDetailCrew.profilePath}",
            contentDescription = "ProfileImage"
        )
        Text(
            text = tmdbMovieDetailCrew.department ?: "",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = sp12
        )
        Text(
            text = tmdbMovieDetailCrew.name ?: "",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = sp15
        )
        Text(
            text = tmdbMovieDetailCrew.originalName ?: "",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = sp15
        )
        Text(
            text = tmdbMovieDetailCrew.job ?: "",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = sp12
        )
    }
}

@Composable
fun SimilarMovieComponent(
    similarMovieState: LazyPagingItems<Movie>,
    goToMovie: (Int) -> Unit
) {
    val posterUrl = LocalMovieAppDataComposition.current.getImageUrl()
    var isAppend by remember { mutableStateOf(false) }
    var pagingStatus by remember { mutableStateOf<PagingStatus>(PagingStatus.NONE) }

    when {
        similarMovieState.loadState.refresh is LoadState.Loading -> pagingStatus = PagingStatus.LOADING
        similarMovieState.loadState.append is LoadState.Loading -> isAppend = true
        similarMovieState.loadState.refresh is LoadState.Error -> {
            isAppend = false

            val message = (similarMovieState.loadState.refresh as? LoadState.Error)?.error?.message
                ?: (similarMovieState.loadState.append as? LoadState.Error)?.error?.message
                ?: stringResource(com.bowoon.movie.core.network.R.string.something_wrong)

            ConfirmDialog(
                title = stringResource(com.bowoon.movie.core.network.R.string.network_failed),
                message = message,
                confirmPair = stringResource(com.bowoon.movie.core.ui.R.string.retry_message) to { similarMovieState.retry() },
                dismissPair = stringResource(com.bowoon.movie.core.ui.R.string.confirm_message) to {}
            )
        }
        similarMovieState.loadState.refresh is LoadState.NotLoading -> {
            isAppend = false
            pagingStatus = if (pagingStatus == PagingStatus.NONE) {
                if (similarMovieState.itemCount == 0) PagingStatus.EMPTY else PagingStatus.NOT_EMPTY
            } else {
                pagingStatus
            }
        }
        similarMovieState.loadState.append is LoadState.NotLoading -> isAppend = false
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (pagingStatus) {
            PagingStatus.LOADING -> CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
            PagingStatus.EMPTY -> Text(
                modifier = Modifier.align(Alignment.Center),
                text = stringResource(R.string.similar_movie_not_found)
            )
            else ->  {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(dp100),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(dp10),
                    horizontalArrangement = Arrangement.spacedBy(dp10),
                    verticalArrangement = Arrangement.spacedBy(dp10)
                ) {
                    items(
                        count = similarMovieState.itemCount
                    ) { index ->
                        Box(
                            modifier = Modifier
                                .width(dp200)
                                .wrapContentHeight()
                                .bounceClick { goToMovie(similarMovieState[index]?.id ?: -1) }
                        ) {
                            DynamicAsyncImageLoader(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(POSTER_IMAGE_RATIO),
                                source = "$posterUrl${similarMovieState[index]?.posterPath}",
                                contentDescription = "SimilarMoviePoster"
                            )
                        }
                    }

                    if (isAppend) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .wrapContentSize()
                                    .align(Alignment.Center)
                            )
                        }
                    }
                    if (similarMovieState.loadState.append is LoadState.Error) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            PagingAppendErrorComponent({ similarMovieState.retry() })
                        }
                    }
                }
            }
        }
    }
}