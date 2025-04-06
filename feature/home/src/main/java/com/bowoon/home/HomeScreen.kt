package com.bowoon.home

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
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bowoon.common.Log
import com.bowoon.data.repository.LocalMovieAppDataComposition
import com.bowoon.data.util.POSTER_IMAGE_RATIO
import com.bowoon.firebase.LocalFirebaseLogHelper
import com.bowoon.model.MainMenu
import com.bowoon.model.Movie
import com.bowoon.movie.feature.home.R
import com.bowoon.ui.utils.bounceClick
import com.bowoon.ui.components.TitleComponent
import com.bowoon.ui.utils.dp150
import com.bowoon.ui.utils.dp16
import com.bowoon.ui.image.DynamicAsyncImageLoader
import com.bowoon.ui.utils.sp10
import com.bowoon.ui.utils.sp8
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onMovieClick: (Int) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    viewModel: HomeVM = hiltViewModel()
) {
    LocalFirebaseLogHelper.current.sendLog("HomeScreen", "init screen")

    val homeUiState by viewModel.mainMenu.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()

    HomeScreen(
        isSyncing = isSyncing,
        state = homeUiState,
        onShowSnackbar = onShowSnackbar,
        onMovieClick = onMovieClick
    )
}

@Composable
fun HomeScreen(
    isSyncing: Boolean,
    state: MainMenuState,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    onMovieClick: (Int) -> Unit
) {
    LocalFirebaseLogHelper.current.sendLog("HomeScreen", "init screen")

    val scope = rememberCoroutineScope()
    val checkingMainData = stringResource(R.string.check_main_data)

    if (isSyncing) {
        LaunchedEffect(isSyncing) {
            scope.launch {
                onShowSnackbar(checkingMainData, null)
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (state) {
            is MainMenuState.Loading -> {
                Log.d("loading...")
                LocalFirebaseLogHelper.current.sendLog("HomeScreen", "data loading...")
                CircularProgressIndicator(
                    modifier = Modifier
                        .semantics { contentDescription = "homeLoading" }
                        .align(Alignment.Center)
                )
            }
            is MainMenuState.Success -> {
                LocalFirebaseLogHelper.current.sendLog("HomeScreen", "data load success")
                Log.d("${state.mainMenu}")
                MainComponent(
                    mainMenu = state.mainMenu,
                    onMovieClick = onMovieClick
                )
            }
            is MainMenuState.Error -> {
                LocalFirebaseLogHelper.current.sendLog("HomeScreen", "data load Error > ${state.throwable.message}")
                Log.e("${state.throwable.message}")
            }
        }
    }
}

@Composable
fun MainComponent(
    mainMenu: MainMenu,
    onMovieClick: (Int) -> Unit
) {
    val lazyListState = rememberLazyListState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        val nowPlayingMoviesTitle = stringResource(R.string.now_playing_movies)
        val upcomingMoviesTitle = stringResource(R.string.upcoming_movies)

        TitleComponent(title = stringResource(R.string.title_movie_info))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lazyListState
        ) {
            if (mainMenu.nowPlaying.isNotEmpty()) {
                horizontalMovieListComponent(
                    title = nowPlayingMoviesTitle,
                    movies = mainMenu.nowPlaying,
                    onMovieClick = onMovieClick
                )
            }
            if (mainMenu.upComingMovies.isNotEmpty()) {
                horizontalMovieListComponent(
                    title = upcomingMoviesTitle,
                    movies = mainMenu.upComingMovies,
                    onMovieClick = onMovieClick
                )
            }
        }
    }
}

fun LazyListScope.horizontalMovieListComponent(
    title: String,
    movies: List<Movie>,
    onMovieClick: (Int) -> Unit
) {
    item {
        Text(
            modifier = Modifier.padding(dp16).fillMaxWidth(),
            text = title
        )
        LazyRow(
            modifier = Modifier.wrapContentSize(),
            contentPadding = PaddingValues(horizontal = dp16),
            horizontalArrangement = Arrangement.spacedBy(dp16)
        ) {
            items(
                count = movies.size,
                key = { index -> "${movies[index].id}_${index}_${movies[index].title}" }
            ) { index ->
                MainMovieItem(
                    movie = movies[index],
                    onMovieClick = onMovieClick
                )
            }
        }
    }
}

@Composable
fun MainMovieItem(
    movie: Movie,
    onMovieClick: (Int) -> Unit
) {
    val posterPath = LocalMovieAppDataComposition.current.getImageUrl()

    Column(
        modifier = Modifier
            .width(dp150)
            .wrapContentHeight()
            .bounceClick { onMovieClick(movie.id ?: -1) }
    ) {
        Box(
            modifier = Modifier.wrapContentSize()
        ) {
            DynamicAsyncImageLoader(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(POSTER_IMAGE_RATIO),
                source = "$posterPath${movie.posterPath}",
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