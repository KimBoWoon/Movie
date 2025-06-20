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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import com.bowoon.data.repository.LocalMovieAppDataComposition
import com.bowoon.data.util.POSTER_IMAGE_RATIO
import com.bowoon.home.navigation.Home
import com.bowoon.model.Movie
import com.bowoon.movie.feature.home.R
import com.bowoon.ui.components.TitleComponent
import com.bowoon.ui.image.DynamicAsyncImageLoader
import com.bowoon.ui.utils.bounceClick
import com.bowoon.ui.utils.dp150
import com.bowoon.ui.utils.dp16
import com.bowoon.ui.utils.sp10
import com.bowoon.ui.utils.sp8
import com.slack.circuit.codegen.annotations.CircuitInject
import dagger.hilt.android.components.ActivityRetainedComponent

@CircuitInject(Home::class, ActivityRetainedComponent::class)
@Composable
fun HomeScreen(
    state: HomeUiState,
    modifier: Modifier = Modifier,
) {
    val lazyListState = rememberLazyListState()
    val nowPlayingMoviesTitle = stringResource(R.string.now_playing_movies)
    val upcomingMoviesTitle = stringResource(R.string.upcoming_movies)

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TitleComponent(title = stringResource(R.string.title_movie_info))

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (state.isSyncing) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            when (state.mainMenuState) {
                is MainMenuState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is MainMenuState.Success -> {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            state = lazyListState
                        ) {
                            if (state.mainMenuState.mainMenu.nowPlaying.isNotEmpty()) {
                                horizontalMovieListComponent(
                                    title = nowPlayingMoviesTitle,
                                    movies = state.mainMenuState.mainMenu.nowPlaying,
                                    goToMovie = { id -> state.eventSink(HomeEvent.GoToMovie(id = id)) }
                                )
                            }
                            if (state.mainMenuState.mainMenu.upComingMovies.isNotEmpty()) {
                                horizontalMovieListComponent(
                                    title = upcomingMoviesTitle,
                                    movies = state.mainMenuState.mainMenu.upComingMovies,
                                    goToMovie = { id -> state.eventSink(HomeEvent.GoToMovie(id = id)) }
                                )
                            }
                        }
                    }
                }
                is MainMenuState.Error -> {}
            }
        }
    }
}

fun LazyListScope.horizontalMovieListComponent(
    title: String,
    movies: List<Movie>,
    goToMovie: (Int) -> Unit
) {
    item {
        Text(
            modifier = Modifier
                .padding(dp16)
                .fillMaxWidth(),
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
                    goToMovie = goToMovie
                )
            }
        }
    }
}

@Composable
fun MainMovieItem(
    movie: Movie,
    goToMovie: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .width(dp150)
            .wrapContentHeight()
            .bounceClick { goToMovie(movie.id ?: -1) }
    ) {
        Box(
            modifier = Modifier.wrapContentSize()
        ) {
            DynamicAsyncImageLoader(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(POSTER_IMAGE_RATIO),
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