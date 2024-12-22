package com.bowoon.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bowoon.common.Log
import com.bowoon.model.DailyBoxOffice
import com.bowoon.model.MovieDetail
import com.bowoon.ui.dp1
import com.bowoon.ui.dp10
import com.bowoon.ui.dp15
import com.bowoon.ui.dp150
import com.bowoon.ui.dp20
import com.bowoon.ui.dp200
import com.bowoon.ui.dp30
import com.bowoon.ui.image.DynamicAsyncImageLoader
import com.bowoon.ui.sp10
import com.bowoon.ui.sp8
import org.threeten.bp.LocalDate

@Composable
fun HomeScreen(
    onMovieClick: (String, String, String) -> Unit,
    viewModel: HomeVM = hiltViewModel()
) {
    val boxOfficeState by viewModel.boxOfficeState.collectAsStateWithLifecycle()
    val favoriteMoviesState by viewModel.favoriteMovies.collectAsStateWithLifecycle()

    HomeScreen(
        state = boxOfficeState,
        favoriteMoviesState = favoriteMoviesState,
        onMovieClick = onMovieClick
    )
}

@Composable
fun HomeScreen(
    state: BoxOfficeState,
    favoriteMoviesState: FavoriteMoviesState,
    onMovieClick: (String, String, String) -> Unit
) {
    when (state) {
        is BoxOfficeState.Loading -> {
            Log.d("loading...")

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
        is BoxOfficeState.Success -> {
            Log.d("${state.boxOffice}")

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                DailyBoxOfficeComponent(
                    boxOffice = state.boxOffice,
                    onMovieClick = onMovieClick
                )
                CalendarComponent(favoriteMoviesState)
            }
        }
        is BoxOfficeState.Error -> Log.e("${state.throwable.message}")
    }
}

@Composable
fun ColumnScope.DailyBoxOfficeComponent(
    boxOffice: List<DailyBoxOffice>,
    onMovieClick: (String, String, String) -> Unit
) {
    Text(
        modifier = Modifier.align(Alignment.Start),
        text = "일별 박스오피스"
    )
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        contentPadding = PaddingValues(dp15),
        horizontalArrangement = Arrangement.spacedBy(dp15)
    ) {
        items(
            items = boxOffice,
            key = { "${it.rank}_${it.rnum}_${it.openDt}_${it.movieNm}" }
        ) { boxOffice ->
            BoxOfficeItem(
                boxOffice = boxOffice,
                onMovieClick = onMovieClick
            )
        }
    }
}

@Composable
fun BoxOfficeItem(
    boxOffice: DailyBoxOffice,
    onMovieClick: (String, String, String) -> Unit
) {
    Column(
        modifier = Modifier
            .width(dp150)
            .wrapContentHeight()
            .clickable {
                onMovieClick(
                    boxOffice.openDt ?: "",
                    boxOffice.movieCd ?: "",
                    boxOffice.movieNm ?: ""
                )
            }
    ) {
        Box(
            modifier = Modifier
                .width(dp150)
                .height(dp200)
        ) {
            DynamicAsyncImageLoader(
                modifier = Modifier.fillMaxSize(),
                source = boxOffice.posterUrl ?: "",
                contentDescription = "BoxOfficePoster"
            )
            Text(
                modifier = Modifier
                    .size(dp15)
                    .background(if ((boxOffice.rank?.toInt() ?: 0) < 4) Color.Red else Color.Gray)
                    .align(Alignment.TopStart),
                text = boxOffice.rank ?: "",
                color = Color.White,
                fontSize = sp10,
                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                textAlign = TextAlign.Center
            )
        }
        Text(
            text = boxOffice.movieNm ?: "",
            fontSize = sp10,
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = boxOffice.openDt ?: "",
            fontSize = sp8,
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun CalendarComponent(
    favoriteMoviesState: FavoriteMoviesState
) {
    var favoriteMovies by remember { mutableStateOf<List<MovieDetail>>(emptyList()) }
    var openMovies by remember { mutableStateOf<List<MovieDetail>>(emptyList()) }

    when (favoriteMoviesState) {
        is FavoriteMoviesState.Loading -> {}
        is FavoriteMoviesState.Success -> favoriteMovies = favoriteMoviesState.favoriteMovies
        is FavoriteMoviesState.Error -> {}
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
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
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        columns = GridCells.Fixed(7)
    ) {
        val date = LocalDate.now()

        for (i in 0 until (date.dayOfWeek.value % 7)) {
            item {
                Box(
                    modifier = Modifier
                        .size(dp30)
                        .padding(top = dp10)
                )
            }
        }
        items(
            count = date.lengthOfMonth()
        ) { day ->
            if (favoriteMovies.find { it.openDt != null && LocalDate.parse(it.openDt) == date.withDayOfMonth(day + 1) } != null) {
                Text(
                    modifier = Modifier
                        .background(color = Color.Yellow)
                        .clickable {
                            val movies = favoriteMovies.filter { it.openDt != null && LocalDate.parse(it.openDt) == date.withDayOfMonth(day + 1) }
                            Log.d("$movies")
                            openMovies = movies

                        },
                    text = "${day + 1}",
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )
            } else {
                Text(
                    modifier = Modifier
                        .background(color = Color.Transparent)
                        .clickable {
                            Log.d("${date.withDayOfMonth(day + 1)}")
                            openMovies = emptyList()
                        },
                    text = "${day + 1}",
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        contentPadding = PaddingValues(dp10),
        verticalArrangement = Arrangement.spacedBy(dp10)
    ) {
        items(
            items = openMovies
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .border(width = dp1, color = Color.White, shape = RoundedCornerShape(dp20))
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = dp10),
                    text = it.title ?: ""
                )
                Text(
                    modifier = Modifier.padding(horizontal = dp10),
                    text = it.openDt ?: ""
                )
            }
        }
    }
}

enum class Week(val label: String) {
    SUNDAY("일"),
    MONDAY("월"),
    TUESDAY("화"),
    WEDNESDAY("수"),
    THURSDAY("목"),
    FRIDAY("금"),
    SATURDAY("토"),
}