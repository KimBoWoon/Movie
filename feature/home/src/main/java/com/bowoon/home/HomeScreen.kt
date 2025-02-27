package com.bowoon.home

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
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.SubcomposeAsyncImage
import com.bowoon.common.Log
import com.bowoon.data.repository.LocalInitDataComposition
import com.bowoon.data.util.POSTER_IMAGE_RATIO
import com.bowoon.firebase.LocalFirebaseLogHelper
import com.bowoon.model.MainMenu
import com.bowoon.model.Movie
import com.bowoon.ui.Calendar
import com.bowoon.ui.Title
import com.bowoon.ui.bounceClick
import com.bowoon.ui.dp10
import com.bowoon.ui.dp150
import com.bowoon.ui.dp16
import com.bowoon.ui.dp300
import com.bowoon.ui.dp40
import com.bowoon.ui.image.DynamicAsyncImageLoader
import com.bowoon.ui.sp10
import com.bowoon.ui.sp20
import com.bowoon.ui.sp8
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate

@Composable
fun HomeScreen(
    onMovieClick: (Int) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    viewModel: HomeVM = hiltViewModel()
) {
    LocalFirebaseLogHelper.current.sendLog("HomeScreen", "init screen")

    val homeUiState by viewModel.mainMenu.collectAsStateWithLifecycle()
    val favoriteMoviesState by viewModel.favoriteMovies.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    var releaseMovies by remember { mutableStateOf(emptyList<Movie>()) }

    LaunchedEffect("ReleaseMovies") {
        scope.launch {
            viewModel.getReleaseMovies()
                .collect {
                    if (it.isNotEmpty()) {
                        releaseMovies = it
                    }
                }
        }
    }

    if (viewModel.isShowReleaseMoviesDialog && releaseMovies.isNotEmpty()) {
        ReleaseMoviesDialog(
            { viewModel.isShowReleaseMoviesDialog = false },
            releaseMovies = releaseMovies,
            onMovieClick = onMovieClick
        )
    }

    HomeScreen(
        isSyncing = isSyncing,
        state = homeUiState,
        favoriteMoviesState = favoriteMoviesState,
        onShowSnackbar = onShowSnackbar,
        onMovieClick = onMovieClick
    )
}

@Composable
fun HomeScreen(
    isSyncing: Boolean,
    state: MainMenuState,
    favoriteMoviesState: FavoriteMoviesState,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    onMovieClick: (Int) -> Unit
) {
    LocalFirebaseLogHelper.current.sendLog("HomeScreen", "init screen")

    val scope = rememberCoroutineScope()

    if (isSyncing) {
        LaunchedEffect("updateData") {
            scope.launch {
                onShowSnackbar("데이터를 확인하고 있습니다.", null)
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
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is MainMenuState.Success -> {
                LocalFirebaseLogHelper.current.sendLog("HomeScreen", "data load success")
                Log.d("${state.mainMenu}")
                MainComponent(
                    mainMenu = state.mainMenu,
                    onMovieClick = onMovieClick,
                    favoriteMoviesState = favoriteMoviesState
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
    onMovieClick: (Int) -> Unit,
    favoriteMoviesState: FavoriteMoviesState
) {
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val scrollEvent: () -> Unit = {
        scope.launch {
            lazyListState.animateScrollToItem(2)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Title(title = "영화 정보")
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lazyListState
        ) {
            nowPlayingComponent(
                boxOffice = mainMenu.nowPlaying,
                onMovieClick = onMovieClick
            )
            upcomingComponent(
                upcoming = mainMenu.upcomingMovies,
                onMovieClick = onMovieClick
            )
            calendarComponent(
                favoriteMoviesState = favoriteMoviesState,
                onMovieClick = onMovieClick,
                scrollEvent = scrollEvent
            )
        }
    }
}

fun LazyListScope.nowPlayingComponent(
    boxOffice: List<Movie>,
    onMovieClick: (Int) -> Unit
) {
    item {
        if (boxOffice.isNotEmpty()) {
            Text(
                modifier = Modifier
                    .padding(dp16)
                    .fillMaxWidth(),
                text = "상영중인 영화"
            )
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                contentPadding = PaddingValues(horizontal = dp16),
                horizontalArrangement = Arrangement.spacedBy(dp16)
            ) {
                items(
                    items = boxOffice,
                    key = { "${it.rank}_${it.releaseDate}_${it.title}_${it.id}" }
                ) { boxOffice ->
                    MainMovieItem(
                        movie = boxOffice,
                        onMovieClick = onMovieClick
                    )
                }
            }
        }
    }
}

fun LazyListScope.upcomingComponent(
    upcoming: List<Movie>,
    onMovieClick: (Int) -> Unit
) {
    item {
        if (upcoming.isNotEmpty()) {
            Text(
                modifier = Modifier
                    .padding(dp16)
                    .fillMaxWidth(),
                text = "개봉 예정작"
            )
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                contentPadding = PaddingValues(horizontal = dp16),
                horizontalArrangement = Arrangement.spacedBy(dp16)
            ) {
                items(
                    count = upcoming.size,
                    key = { "${upcoming[it].id}_${upcoming[it].title}_${upcoming[it].originalTitle}_${upcoming[it].releaseDate}" }
                ) { index ->
                    MainMovieItem(
                        movie = upcoming[index],
                        onMovieClick = onMovieClick
                    )
                }
            }
        }
    }
}

fun LazyListScope.calendarComponent(
    favoriteMoviesState: FavoriteMoviesState,
    onMovieClick: (Int) -> Unit,
    scrollEvent: () -> Unit
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
            modifier = Modifier
                .padding(dp16)
                .fillMaxWidth(),
            state = horizontalPagerState
        ) {index ->
            Calendar(
                today = calendarList[index],
                favoriteMovies = favoriteMovies,
                onMovieClick = onMovieClick,
                scrollEvent = scrollEvent
            )
        }
    }
}

@Composable
fun MainMovieItem(
    movie: Movie,
    onMovieClick: (Int) -> Unit
) {
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

@Composable
fun ReleaseMoviesDialog(
    onDismiss: () -> Unit,
    releaseMovies: List<Movie>,
    onMovieClick: (Int) -> Unit
) {
    val secureBaseUrl = LocalInitDataComposition.current.getImageUrl()

    SubcomposeAsyncImage(
        model = "$secureBaseUrl${releaseMovies[0].posterPath}",
        contentDescription = "ReleaseMoviesDialog",
        success = {
            Dialog(
                onDismissRequest = { onDismiss() },
                properties = DialogProperties(
                    usePlatformDefaultWidth = false,
                    dismissOnBackPress = true,
                    dismissOnClickOutside = false
                )
            ) {
                val pageState = rememberPagerState(initialPage = 0) { releaseMovies.size }
                var movieIndex = 0

                Column(
                    modifier = Modifier.width(dp300)
                        .background(color = Color.LightGray, shape = RoundedCornerShape(dp10)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    HorizontalPager(
                        modifier = Modifier.fillMaxWidth()
                            .clickable {
                                onMovieClick(releaseMovies[movieIndex].id ?: -1)
                                onDismiss()
                            },
                        state = pageState,
                    ) {
                        movieIndex = it
                        DynamicAsyncImageLoader(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(POSTER_IMAGE_RATIO),
                            source = "$secureBaseUrl${releaseMovies[it].posterPath}",
                            contentDescription = "ReleaseMovieImage"
                        )
                    }
                    Text(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        text = "영화가 곧 개봉합니다!",
                        color = Color.Black
                    )
                    Row(
                        modifier = Modifier.height(dp40),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.weight(1f)
                                .wrapContentHeight()
                                .clickable {
                                    onMovieClick(releaseMovies[movieIndex].id ?: -1)
                                    onDismiss()
                                },
                            text = "상세 정보",
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            fontSize = sp20,
                            color = Color.Black
                        )
                        Text(
                            modifier = Modifier.weight(1f)
                                .wrapContentHeight()
                                .clickable { onDismiss() },
                            text = "닫기",
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            fontSize = sp20,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    )
}