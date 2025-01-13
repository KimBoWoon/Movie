package com.bowoon.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bowoon.common.Log
import com.bowoon.data.util.POSTER_IMAGE_RATIO
import com.bowoon.model.MainMenu
import com.bowoon.model.MainMovie
import com.bowoon.ui.BoxOfficeRank
import com.bowoon.ui.Calendar
import com.bowoon.ui.Title
import com.bowoon.ui.dp15
import com.bowoon.ui.dp150
import com.bowoon.ui.dp25
import com.bowoon.ui.image.DynamicAsyncImageLoader
import com.bowoon.ui.sp10
import com.bowoon.ui.sp8
import org.threeten.bp.LocalDate

@Composable
fun HomeScreen(
    onMovieClick: (Int) -> Unit,
    viewModel: HomeVM = hiltViewModel()
) {
    val homeUiState by viewModel.mainMenu.collectAsStateWithLifecycle()
    val favoriteMoviesState by viewModel.favoriteMovies.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()

    HomeScreen(
        isSyncing = isSyncing,
        state = homeUiState,
        favoriteMoviesState = favoriteMoviesState,
        onMovieClick = onMovieClick
    )
}

@Composable
fun HomeScreen(
    isSyncing: Boolean,
    state: MainMenuState,
    favoriteMoviesState: FavoriteMoviesState,
    onMovieClick: (Int) -> Unit
) {
    val isLoading = state is MainMenuState.Loading
    var mainMenu by remember { mutableStateOf<MainMenu>(MainMenu()) }

    when (state) {
        is MainMenuState.Loading -> Log.d("loading...")
        is MainMenuState.Success -> {
            Log.d("${state.mainMenu}")
            mainMenu = state.mainMenu
        }
        is MainMenuState.Error -> Log.e("${state.throwable.message}")
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Title(title = "영화 정보")
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                dailyBoxOfficeComponent(
                    boxOffice = mainMenu.dailyBoxOffice,
                    onMovieClick = onMovieClick
                )
                upcomingComponent(
                    upcoming = mainMenu.upcomingMovies.sortedBy { it.releaseDate },
                    onMovieClick = onMovieClick
                )
                calendarComponent(
                    favoriteMoviesState = favoriteMoviesState
                )
            }
        }

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

fun LazyListScope.dailyBoxOfficeComponent(
    boxOffice: List<MainMovie>,
    onMovieClick: (Int) -> Unit
) {
    item {
        if (boxOffice.isNotEmpty()) {
            Text(text = "일별 박스오피스")
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                contentPadding = PaddingValues(dp15),
                horizontalArrangement = Arrangement.spacedBy(dp15)
            ) {
                items(
                    items = boxOffice,
                    key = { "${it.rank}_${it.releaseDate}_${it.title}_${it.id}" }
                ) { boxOffice ->
                    MainMovieItem(
                        movie = boxOffice,
                        isVisibleRank = true,
                        onMovieClick = onMovieClick
                    )
                }
            }
        }
    }
}

fun LazyListScope.upcomingComponent(
    upcoming: List<MainMovie>,
    onMovieClick: (Int) -> Unit
) {
    item {
        if (upcoming.isNotEmpty()) {
            Text(text = "개봉 예정작")
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                contentPadding = PaddingValues(dp15),
                horizontalArrangement = Arrangement.spacedBy(dp15)
            ) {
                items(
                    count = upcoming.size,
                    key = { "${upcoming[it].id}_${upcoming[it].title}_${upcoming[it].originalTitle}_${upcoming[it].releaseDate}" }
                ) { index ->
                    MainMovieItem(
                        movie = upcoming[index],
                        isVisibleRank = false,
                        onMovieClick = onMovieClick
                    )
                }
            }
        }
    }
}

fun LazyListScope.calendarComponent(
    favoriteMoviesState: FavoriteMoviesState
) {
    item {
        val calendarList = listOf(LocalDate.now().minusMonths(1), LocalDate.now(), LocalDate.now().plusMonths(1))
        val horizontalPagerState = rememberPagerState(initialPage = 1) { calendarList.size }
        val favoriteMovies = when (favoriteMoviesState) {
            is FavoriteMoviesState.Loading -> emptyList()
            is FavoriteMoviesState.Success -> favoriteMoviesState.favoriteMovies
            is FavoriteMoviesState.Error -> emptyList()
        }

        HorizontalPager(
            state = horizontalPagerState
        ) {index ->
            Calendar(
                today = calendarList[index],
                favoriteMovies = favoriteMovies
            )
        }
    }
}

@Composable
fun MainMovieItem(
    movie: MainMovie,
    isVisibleRank: Boolean,
    onMovieClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .width(dp150)
            .wrapContentHeight()
            .clickable {
                onMovieClick(movie.id ?: -1)
            }
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
            if (isVisibleRank) {
                BoxOfficeRank(
                    modifier = Modifier
                        .size(dp25)
                        .background(if ((movie.rank?.toInt() ?: 0) < 4) Color.Red else Color.Gray)
                        .align(Alignment.TopStart),
                    rank = movie.rank?.toInt() ?: 0
                )
            }
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