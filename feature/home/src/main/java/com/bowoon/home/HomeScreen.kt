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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.bowoon.model.UpComingResult
import com.bowoon.model.Week
import com.bowoon.ui.dp1
import com.bowoon.ui.dp10
import com.bowoon.ui.dp130
import com.bowoon.ui.dp15
import com.bowoon.ui.dp150
import com.bowoon.ui.dp20
import com.bowoon.ui.dp30
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

    HomeScreen(
        state = homeUiState,
        onMovieClick = onMovieClick
    )
}

@Composable
fun HomeScreen(
    state: HomeUiState,
    onMovieClick: (Int) -> Unit
) {
    when (state) {
        is HomeUiState.Loading -> {
            Log.d("loading...")

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
        is HomeUiState.Success -> {
            Log.d("${state.mainMenu}")

//            LazyColumn() {
//                item {
//                    DailyBoxOfficeComponent(
//                        boxOffice = state.mainMenu.dailyBoxOffice,
//                        onMovieClick = onMovieClick
//                    )
//                    UpcomingComponent(
//                        upcoming = state.mainMenu.upcomingMovies,
//                        onMovieClick = onMovieClick
//                    )
//                    this@LazyColumn.CalendarComponent(
//                        favoriteMovies = state.mainMenu.favoriteMovies,
//                        filterFavoriteMovies = { releaseDate -> state.mainMenu.favoriteMovies.filter { it.releaseDate == releaseDate } }
//                    )
//                }
//            }
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(scrollState),
            ) {
                DailyBoxOfficeComponent(
                    boxOffice = state.mainMenu.dailyBoxOffice,
                    onMovieClick = onMovieClick
                )
                UpcomingComponent(
                    upcoming = state.mainMenu.upcomingMovies,
                    onMovieClick = onMovieClick
                )
                CalendarComponent(
                    favoriteMovies = state.mainMenu.favoriteMovies,
                    filterFavoriteMovies = { releaseDate ->
                        state.mainMenu.favoriteMovies.filter { it.releases?.countries?.find { it.iso31661.equals("KR", true) }?.releaseDate == releaseDate }
                    }
                )
            }
        }
        is HomeUiState.Error -> Log.e("${state.throwable.message}")
    }
}

@Composable
fun DailyBoxOfficeComponent(
    boxOffice: List<DailyBoxOffice>,
    onMovieClick: (Int) -> Unit
) {
    Text(
//        modifier = Modifier.align(Alignment.Start),
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
    onMovieClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .width(dp150)
            .wrapContentHeight()
            .clickable {
                onMovieClick(boxOffice.tmdbId ?: -1)
            }
    ) {
        Box(
            modifier = Modifier.wrapContentSize()
        ) {
            DynamicAsyncImageLoader(
                modifier = Modifier.width(dp150).aspectRatio(9f / 16f),
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
fun UpcomingComponent(
    upcoming: List<UpComingResult>,
    onMovieClick: (Int) -> Unit
) {
    Text(
//        modifier = Modifier.align(Alignment.Start),
        text = "개봉 예정작"
    )
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        contentPadding = PaddingValues(dp15),
        horizontalArrangement = Arrangement.spacedBy(dp15)
    ) {
        items(
            items = upcoming,
            key = { "${it.id}_${it.title}_${it.originalTitle}_${it.releaseDate}" }
        ) { upcoming ->
            UpcomingItem(
                upcoming = upcoming,
                onMovieClick = onMovieClick
            )
        }
    }
}

@Composable
fun UpcomingItem(
    upcoming: UpComingResult,
    onMovieClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .wrapContentSize()
            .clickable {
                onMovieClick(upcoming.id ?: -1)
            }
    ) {
        DynamicAsyncImageLoader(
            modifier = Modifier.width(dp130).aspectRatio(9f / 16f),
            source = "https://image.tmdb.org/t/p/original${upcoming.posterPath}",
            contentDescription = "BoxOfficePoster"
        )
        Text(
            text = upcoming.title ?: "",
            fontSize = sp10,
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = upcoming.releaseDate ?: "",
            fontSize = sp8,
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun CalendarComponent(
    favoriteMovies: List<MovieDetail>,
    filterFavoriteMovies: (String) -> List<MovieDetail>
) {
    val calendarList = listOf(LocalDate.now().minusMonths(1), LocalDate.now(), LocalDate.now().plusMonths(1))
    val horizontalPagerState = rememberPagerState(initialPage = 1) { calendarList.size }

    HorizontalPager(
        state = horizontalPagerState
    ) {index ->
        Column {
            Calendar(
                today = calendarList[index],
                favoriteMovies = favoriteMovies,
                filterFavoriteMovies = filterFavoriteMovies
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Calendar(
    today: LocalDate,
    favoriteMovies: List<MovieDetail>,
    filterFavoriteMovies: (String) -> List<MovieDetail>,
) {
    var releaseDate by remember { mutableStateOf("") }

    Row(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
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
            Spacer(modifier = Modifier.height(dp30).weight(1f).background(color = Color.Transparent))
        }

        for (day in 1 until today.lengthOfMonth() + 1) {
            if (favoriteMovies.find { it.releases?.countries?.find { it.iso31661.equals("KR", true) }?.releaseDate != null && LocalDate.parse(it.releases?.countries?.find { it.iso31661.equals("KR", true) }?.releaseDate) == today.withDayOfMonth(day) } != null) {
                Text(
                    modifier = Modifier
                        .height(dp30)
                        .weight(1f)
                        .background(color = Color.Yellow)
                        .clickable { releaseDate = today.withDayOfMonth(day).toString() },
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
                        .clickable { releaseDate = today.withDayOfMonth(day).toString() },
                    text = "$day",
                    textAlign = TextAlign.Center
                )
            }
        }

        for (i in 0 until  (5 * 7) - today.lengthOfMonth()) {
            Spacer(modifier = Modifier.height(dp30).weight(1f).background(color = Color.Transparent))
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        filterFavoriteMovies(releaseDate).forEach { movie ->
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