package com.bowoon.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bowoon.common.Log
import com.bowoon.model.DailyBoxOffice
import com.bowoon.ui.dp15
import com.bowoon.ui.dp150
import com.bowoon.ui.dp200
import com.bowoon.ui.image.DynamicAsyncImageLoader
import com.bowoon.ui.sp10
import com.bowoon.ui.sp8

@Composable
fun HomeScreen(
    onMovieClick: (String, String, String) -> Unit,
    viewModel: HomeVM = hiltViewModel()
) {
    val boxOfficeState by viewModel.boxOfficeState.collectAsStateWithLifecycle()

    HomeScreen(
        state = boxOfficeState,
        onMovieClick = onMovieClick
    )
}

@Composable
fun HomeScreen(
    state: BoxOfficeState,
    onMovieClick: (String, String, String) -> Unit,
    viewModel: HomeVM = hiltViewModel()
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
            }
        }
        is BoxOfficeState.Error -> Log.d("${state.throwable.message}")
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
    onMovieClick: (String, String, String) -> Unit,
    viewModel: HomeVM = hiltViewModel()
) {
    var posterUrl = "https://i.namu.wiki/i/oLWTEwoMCrj9EdiaQBUvgh6pj-4dOOOKZK3XHNXm2C4ues9ehba06JjHNW88zNRlM6kcDmr4xC2e-Ndc30Bxvh8KhDJU28zVdkHruwVkeXvdwsoi2FBn_8t09LtJQTWq8WNmA_5orKI99nrsKFFJfQ.webp"

//    var posterUrl by remember { mutableStateOf("") }
//    val scope = rememberCoroutineScope()
//
//    LaunchedEffect(key1 = "posterUrl") {
//        scope.launch {
//            posterUrl = viewModel.getPosterUrl(boxOffice.detailUrl ?: "")
//        }
//    }

    Column(
        modifier = Modifier.clickable { onMovieClick(boxOffice.openDt ?: "", boxOffice.movieCd ?: "", boxOffice.movieNm ?: "") }
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
                modifier = Modifier.size(dp15)
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
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
        )
        Text(
            text = boxOffice.openDt ?: "",
            fontSize = sp8,
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
        )
    }
}

//@Composable
//@Preview
//fun DailyBoxOfficePreview() {
//    MovieTheme {
//        Column {
//            DailyBoxOfficeComponent(emptyList(), { _, _, _ ->})
//        }
//    }
//}
//
//@Composable
//@Preview
//fun BoxOfficeItemPreview() {
//    MovieTheme {
//        BoxOfficeItem(
//            KOBISDailyBoxOffice(
//                movieNm = "movie",
//                openDt = "20241214",
//                rank = "1"
//            ),
//            { _, _, _ ->}
//        )
//    }
//}