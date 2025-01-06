package com.bowoon.favorite

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bowoon.model.MovieDetail
import com.bowoon.ui.FavoriteButton
import com.bowoon.ui.dp10
import com.bowoon.ui.dp15
import com.bowoon.ui.dp200
import com.bowoon.ui.dp5
import com.bowoon.ui.image.DynamicAsyncImageLoader
import kotlinx.coroutines.launch

@Composable
fun FavoriteScreen(
    onMovieClick: (Int) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    viewModel: FavoriteVM = hiltViewModel()
) {
    val state by viewModel.favoriteMovies.collectAsStateWithLifecycle()

    FavoriteScreen(
        state = state,
        onMovieClick = onMovieClick,
        onShowSnackbar = onShowSnackbar,
        deleteFavoriteMovie = viewModel::deleteMovie
    )
}

@Composable
fun FavoriteScreen(
    state: FavoriteMoviesState,
    onMovieClick: (Int) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    deleteFavoriteMovie: (MovieDetail) -> Unit
) {
    val isLoading = state is FavoriteMoviesState.Loading
    var favoriteMovies by remember { mutableStateOf<List<MovieDetail>>(emptyList()) }
    val scope = rememberCoroutineScope()

    when (state) {
        is FavoriteMoviesState.Loading -> {}
        is FavoriteMoviesState.Success -> favoriteMovies = state.data
        is FavoriteMoviesState.Error -> {}
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }

        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(dp15),
            horizontalArrangement = Arrangement.spacedBy(dp10),
            verticalArrangement = Arrangement.spacedBy(dp10)
        ) {
            items(
                items = favoriteMovies
            ) { movieDetail ->
                Box(
                    modifier = Modifier.clickable { onMovieClick(movieDetail.id ?: -1) }
                ) {
                    DynamicAsyncImageLoader(
                        modifier = Modifier.width(dp200).aspectRatio(9f / 16f),
                        source = movieDetail.posterPath ?: "",
                        contentDescription = "BoxOfficePoster"
                    )
                    FavoriteButton(
                        modifier = Modifier
                            .padding(top = dp5, end = dp5)
                            .wrapContentSize()
                            .align(Alignment.TopEnd),
                        isFavorite = favoriteMovies.contains(movieDetail),
                        onClick = {
                            deleteFavoriteMovie(movieDetail)
                            scope.launch {
                                onShowSnackbar("즐겨찾기에서 제거됐습니다.", null)
                            }
                        }
                    )
                }
            }
        }
    }
}