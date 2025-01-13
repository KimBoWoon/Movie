package com.bowoon.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bowoon.common.Log
import com.bowoon.data.util.POSTER_IMAGE_RATIO
import com.bowoon.model.MainMenu
import com.bowoon.model.MainMovie
import com.bowoon.model.MovieDetail
import com.bowoon.model.Week
import com.bowoon.ui.BoxOfficeRank
import com.bowoon.ui.dp1
import com.bowoon.ui.dp10
import com.bowoon.ui.dp15
import com.bowoon.ui.dp150
import com.bowoon.ui.dp20
import com.bowoon.ui.dp25
import com.bowoon.ui.dp30
import com.bowoon.ui.image.DynamicAsyncImageLoader
import com.bowoon.ui.sp10
import com.bowoon.ui.sp15
import com.bowoon.ui.sp8
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

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

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

fun LazyListScope.dailyBoxOfficeComponent(
//    boxOffice: List<DailyBoxOffice>,
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

fun LazyListScope.upcomingComponent(
//    upcoming: List<UpComingResult>,
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

        HorizontalPager(
            state = horizontalPagerState
        ) {index ->
            Column {
                Calendar(
                    today = calendarList[index],
                    favoriteMoviesState = favoriteMoviesState
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Calendar(
    today: LocalDate,
    favoriteMoviesState: FavoriteMoviesState
) {
    var releaseDate by remember { mutableStateOf("") }
    var favoriteMovies by remember { mutableStateOf<List<MovieDetail>>(emptyList()) }

    when (favoriteMoviesState) {
        is FavoriteMoviesState.Loading -> Log.d("favorite movies loading...")
        is FavoriteMoviesState.Success -> favoriteMovies = favoriteMoviesState.favoriteMovies
        is FavoriteMoviesState.Error -> Log.e(favoriteMoviesState.throwable.message ?: "something wrong...")
    }

    Column {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = today.format(DateTimeFormatter.ofPattern("yyyy년 MM월")),
            fontSize = sp15,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Week.entries.forEach {
                Text(
                    modifier = Modifier.weight(1f),
                    text = it.label,
                    textAlign = TextAlign.Center
                )
            }
        }

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            maxLines = 5,
            maxItemsInEachRow = 7
        ) {
            val firstDay = today.withDayOfMonth(1)

            for (i in 0 until (firstDay.dayOfWeek.value % 7)) {
                Spacer(modifier = Modifier
                    .height(dp30)
                    .weight(1f)
                    .background(color = Color.Transparent))
            }

            for (day in 1 until today.lengthOfMonth() + 1) {
                if (favoriteMovies.find { it.releases?.countries?.find { it.iso31661.equals("KR", true) }?.releaseDate != null && LocalDate.parse(it.releases?.countries?.find { it.iso31661.equals("KR", true) }?.releaseDate) == today.withDayOfMonth(day) } != null) {
                    Text(
                        modifier = Modifier
                            .height(dp30)
                            .weight(1f)
                            .background(color = Color.Yellow)
                            .clickable { releaseDate = today
                                .withDayOfMonth(day)
                                .toString()
                            },
                        text = "$day",
                        textAlign = TextAlign.Center,
                        color = Color.Black
                    )
                } else {
                    Text(
                        modifier = Modifier
                            .height(dp30)
                            .weight(1f)
                            .background(color = Color.Transparent)
                            .clickable { releaseDate = today
                                .withDayOfMonth(day)
                                .toString()
                            },
                        text = "$day",
                        textAlign = TextAlign.Center
                    )
                }
            }

            for (i in 0 until  (5 * 7) - today.lengthOfMonth()) {
                Spacer(modifier = Modifier
                    .height(dp30)
                    .weight(1f)
                    .background(color = Color.Transparent))
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            favoriteMovies.filter { !it.releaseDate.isNullOrEmpty() && releaseDate.isNotEmpty() && it.releaseDate == releaseDate }.forEach { movie ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .border(width = dp1, color = Color.White, shape = RoundedCornerShape(dp20))
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = dp10),
                        text = movie.title ?: ""
                    )
                    Text(
                        modifier = Modifier.padding(horizontal = dp10),
                        text = movie.releaseDate ?: ""
                    )
                }
            }
        }
    }
}