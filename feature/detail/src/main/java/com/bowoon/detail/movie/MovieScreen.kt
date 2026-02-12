package com.bowoon.detail.movie

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.bowoon.common.Log
import com.bowoon.domain.MovieWithFavorite
import com.bowoon.firebase.LocalFirebaseLogHelper
import com.bowoon.model.Movie
import com.bowoon.model.ReviewDataModel
import com.bowoon.model.Series
import com.bowoon.movie.feature.detail.R
import com.bowoon.ui.components.ActorAndCrewComponent
import com.bowoon.ui.components.CircularProgressComponent
import com.bowoon.ui.components.ImageComponent
import com.bowoon.ui.components.ReviewComponent
import com.bowoon.ui.components.SimilarMediaComponent
import com.bowoon.ui.components.TabComponent
import com.bowoon.ui.components.TitleComponent
import com.bowoon.ui.components.VideosComponent
import com.bowoon.ui.components.movieSeriesListComponent
import com.bowoon.ui.components.seriesInfoComponent
import com.bowoon.ui.dialog.ConfirmDialog
import com.bowoon.ui.image.DynamicAsyncImageLoader
import com.bowoon.ui.utils.dp0
import com.bowoon.ui.utils.dp10
import com.bowoon.ui.utils.dp16
import com.bowoon.ui.utils.dp20
import com.bowoon.ui.utils.dp300
import com.bowoon.ui.utils.dp32
import com.bowoon.ui.utils.dp5
import com.bowoon.ui.utils.dp8
import com.bowoon.ui.utils.sp10
import com.bowoon.ui.utils.sp15
import com.bowoon.ui.utils.sp20
import kotlinx.coroutines.launch

@Composable
fun MovieScreen(
    goToBack: () -> Unit,
    goToMovie: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    viewModel: MovieVM = hiltViewModel()
) {
    LocalFirebaseLogHelper.current.sendLog("DetailScreen", "detail screen start!")

    val movieState by viewModel.movie.collectAsStateWithLifecycle()
    val tabIndex by viewModel.tabIndex.collectAsStateWithLifecycle()
    val similarMovies = viewModel.similarMovies.collectAsLazyPagingItems()
    val movieReviews = viewModel.movieReviews.collectAsLazyPagingItems()

    MovieScreen(
        movieState = movieState,
        similarMovies = similarMovies,
        movieReviews = movieReviews,
        tabIndex = tabIndex,
        goToMovie = goToMovie,
        goToPeople = goToPeople,
        goToBack = goToBack,
        onShowSnackbar = onShowSnackbar,
        updateTabIndex = viewModel::updateTabIndex,
        insertFavoriteMovie = viewModel::insertMovie,
        deleteFavoriteMovie = viewModel::deleteMovie,
        restart = viewModel::restart
    )
}

@Composable
fun MovieScreen(
    movieState: MovieState,
    similarMovies: LazyPagingItems<Movie>,
    movieReviews: LazyPagingItems<ReviewDataModel>,
    tabIndex: Int,
    goToMovie: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    goToBack: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    updateTabIndex: (Int) -> Unit,
    insertFavoriteMovie: (Movie) -> Unit,
    deleteFavoriteMovie: (Movie) -> Unit,
    restart: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (movieState) {
            is MovieState.Loading -> {
                Log.d("loading...")
                LocalFirebaseLogHelper.current.sendLog(name = "DetailScreen", message = "loading...")

                CircularProgressComponent(
                    modifier = Modifier
                        .testTag(tag = "detailScreenLoading")
                        .align(Alignment.Center)
                )
            }
            is MovieState.Success -> {
                Log.d("$movieState")
                LocalFirebaseLogHelper.current.sendLog(name = "DetailScreen", message = "$movieState")

                MovieDetailComponent(
                    movieState = movieState.movie,
                    similarMovies = similarMovies,
                    movieReviews = movieReviews,
                    tabIndex = tabIndex,
                    goToMovie = goToMovie,
                    goToPeople = goToPeople,
                    goToBack = goToBack,
                    onShowSnackbar = onShowSnackbar,
                    updateTabIndex = updateTabIndex,
                    insertFavoriteMovie = insertFavoriteMovie,
                    deleteFavoriteMovie = deleteFavoriteMovie
                )
            }
            is MovieState.Error -> {
                Log.e("${movieState.throwable.message}")
                LocalFirebaseLogHelper.current.sendLog(name = "DetailScreen", message = "${movieState.throwable.message}")

                ConfirmDialog(
                    title = stringResource(id = com.bowoon.movie.core.network.R.string.network_failed),
                    message = "${movieState.throwable.message}",
                    confirmPair = stringResource(id = com.bowoon.movie.core.ui.R.string.retry_message) to { restart() },
                    dismissPair = stringResource(id = com.bowoon.movie.core.ui.R.string.back_message) to goToBack
                )
            }
        }
    }
}

@Composable
fun MovieDetailComponent(
    movieState: MovieWithFavorite,
    similarMovies: LazyPagingItems<Movie>,
    movieReviews: LazyPagingItems<ReviewDataModel>,
    tabIndex: Int,
    goToMovie: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    goToBack: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    updateTabIndex: (Int) -> Unit,
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
    if (movieState.movie.belongsToCollection == null) {
        tabList.remove(element = stringResource(id = R.string.movie_series))
    }
    if (movieReviews.itemCount == 0) {
        tabList.remove(element = stringResource(id = R.string.movie_reviews))
    }
    val pagerState = rememberPagerState(
        initialPage = tabIndex,
        pageCount = { tabList.size }
    )
    val tabClickEvent: (Int, Int) -> Unit = { current, index ->
        scope.launch {
            pagerState.animateScrollToPage(page = index)
            updateTabIndex(index)
        }
    }
    val favoriteMessage = if (movieState.isFavorite) stringResource(id = R.string.add_favorite_movie) else stringResource(id = R.string.remove_favorite_movie)

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TitleComponent(
            title = movieState.movie.title ?: "",
            isFavorite = movieState.isFavorite,
            goToBack = goToBack,
            onFavoriteClick = {
                if (movieState.isFavorite) {
                    deleteFavoriteMovie(movieState.movie)
                } else {
                    insertFavoriteMovie(movieState.movie)
                }
                scope.launch {
                    onShowSnackbar(favoriteMessage, null)
                }
            }
        )

        VideosComponent(
            vodList = movieState.movie.videos?.results?.mapNotNull { it.key } ?: emptyList(),
            autoPlayTrailer = movieState.autoPlayTrailer
        )

        TabComponent(
            tabs = tabList,
            pagerState = pagerState,
            tabClickEvent = tabClickEvent
        ) { tabs ->
            HorizontalPager(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                state = pagerState,
                userScrollEnabled = false
            ) { index ->
                when (tabs[index]) {
                    stringResource(id = R.string.movie_detail) -> MovieInfoComponent(movie = movieState.movie)
                    stringResource(id = R.string.movie_series) -> MovieSeriesComponent(
                        movieSeries = movieState.movie.series,
                        goToMovie = goToMovie
                    )
                    stringResource(id = R.string.movie_reviews) -> ReviewComponent(
                        reviews = movieReviews,
                    )
                    stringResource(id = R.string.movie_actor_and_crew) -> ActorAndCrewComponent(
                        credits = movieState.movie.credits,
                        goToPeople = goToPeople
                    )
                    stringResource(id = R.string.movie_images) -> {
                        val posters = movieState.movie.images?.posters ?: emptyList()
                        val backdrops = movieState.movie.images?.backdrops ?: emptyList()
                        ImageComponent(images = posters + backdrops)
                    }
                    stringResource(id = R.string.movie_similar_movies) -> SimilarMediaComponent(
                        similarMedia = similarMovies,
                        goToDestination = goToMovie
                    )
                }
            }
        }
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
fun MovieInfoComponent(
    movie: Movie
) {
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
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dp16)
                        .wrapContentHeight(),
                    text = stringResource(id = R.string.movie_alternative_title),
                    fontSize = sp15
                )
                Text(
                    modifier = Modifier
                        .padding(top = dp5, start = dp16, end = dp16)
                        .animateContentSize()
                        .fillMaxWidth(),
                    text = titles,
                    fontSize = sp10,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                )
            }
        }
    }
}