package com.bowoon.people

import androidx.compose.animation.core.FloatExponentialDecaySpec
import androidx.compose.animation.core.animateDecay
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.bowoon.common.Log
import com.bowoon.data.util.PEOPLE_IMAGE_RATIO
import com.bowoon.data.util.POSTER_IMAGE_RATIO
import com.bowoon.model.MovieDetail
import com.bowoon.model.PeopleDetail
import com.bowoon.model.PeopleDetailTab
import com.bowoon.model.RelatedMovie
import com.bowoon.ui.ConfirmDialog
import com.bowoon.ui.Title
import com.bowoon.ui.collaps.CollapsingToolbar
import com.bowoon.ui.collaps.FixedScrollFlagState
import com.bowoon.ui.collaps.ToolbarState
import com.bowoon.ui.collaps.scrollflags.ExitUntilCollapsedState
import com.bowoon.ui.dp10
import com.bowoon.ui.dp100
import com.bowoon.ui.image.DynamicAsyncImageLoader
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch

@Composable
fun PeopleScreen(
    navController: NavController,
    onMovieClick: (Int) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    viewModel: PeopleVM = hiltViewModel()
) {
    val peopleState by viewModel.people.collectAsStateWithLifecycle()
    val favoritePeoples by viewModel.favoritePeoples.collectAsStateWithLifecycle()

    PeopleScreen(
        peopleState = peopleState,
        favoritePeoples = favoritePeoples,
        navController = navController,
        insertFavoritePeople = viewModel::insertPeople,
        deleteFavoritePeople = viewModel::deletePeople,
        onMovieClick = onMovieClick,
        onShowSnackbar = onShowSnackbar,
        restart = viewModel::restart
    )
}

@Composable
fun PeopleScreen(
    peopleState: PeopleState,
    favoritePeoples: List<PeopleDetail>,
    navController: NavController,
    insertFavoritePeople: (PeopleDetail) -> Unit,
    deleteFavoritePeople: (PeopleDetail) -> Unit,
    onMovieClick: (Int) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    restart: () -> Unit
) {
    var isLoading by remember { mutableStateOf(false) }
    var people by remember { mutableStateOf<PeopleDetail?>(null) }
    val isFavorite = { id: Int -> favoritePeoples.find { it.id == id } != null }

    when (peopleState) {
        is PeopleState.Loading -> {
            Log.d("loading...")
            isLoading = true
        }
        is PeopleState.Success -> {
            Log.d("${peopleState.data}")
            isLoading = false
            people = peopleState.data
        }
        is PeopleState.Error -> {
            Log.e("${peopleState.throwable.message}")
            isLoading = false
            ConfirmDialog(
                title = "통신 실패",
                message = "${peopleState.throwable.message}",
                confirmPair = "재시도" to { restart() },
                dismissPair = "돌아가기" to { navController.navigateUp() }
            )
        }
    }

    people?.let {
        PeopleDetailScreen(
            isLoading = isLoading,
            people = it,
            isFavorite = isFavorite,
            navController = navController,
            onMovieClick = onMovieClick,
            insertFavoritePeople = insertFavoritePeople,
            deleteFavoritePeople = deleteFavoritePeople,
            onShowSnackbar = onShowSnackbar
        )
    }
}

@Composable
fun TabComponent(
    modifier: Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    listState: LazyGridState = rememberLazyGridState(),
    people: PeopleDetail,
    onMovieClick: (Int) -> Unit,
    favoriteMovies: List<MovieDetail>,
    insertFavoriteMovie: (MovieDetail) -> Unit,
    deleteFavoriteMovie: (MovieDetail) -> Unit
) {
    val scope = rememberCoroutineScope()
    val tabList = PeopleDetailTab.entries
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabList.size })

    Column(
        modifier = modifier.padding(contentPadding)
    ) {
        TabRow(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            selectedTabIndex = pagerState.currentPage
        ) {
            tabList.forEachIndexed { index, moviePoster ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        Log.d("selected tab index > $index")
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = { Text(text = moviePoster.label) },
                    selectedContentColor = Color(0xFF7C86DF),
                    unselectedContentColor = Color.LightGray
                )
            }
        }

        HorizontalPager(
            modifier = Modifier.fillMaxWidth(),
            state = pagerState,
            userScrollEnabled = false
        ) { index ->
            when (tabList[index].label) {
                PeopleDetailTab.PEOPLE_INFO.label -> PeopleInfoComponent(people = people)
                PeopleDetailTab.RELATED_MOVIE.label -> RelatedMovieComponent(
                    listState = listState,
                    people = people,
                    onMovieClick = onMovieClick,
                    insertFavoriteMovie = insertFavoriteMovie,
                    deleteFavoriteMovie = deleteFavoriteMovie
                )
            }
        }
    }
}

@Composable
private fun rememberToolbarState(toolbarHeightRange: IntRange): ToolbarState {
    return rememberSaveable(saver = ExitUntilCollapsedState.Saver) {
        ExitUntilCollapsedState(toolbarHeightRange)
    }
}

@Composable
fun PeopleDetailScreen(
    isLoading: Boolean,
    people: PeopleDetail,
    isFavorite: (Int) -> Boolean,
    navController: NavController,
    onMovieClick: (Int) -> Unit,
    insertFavoritePeople: (PeopleDetail) -> Unit,
    deleteFavoritePeople: (PeopleDetail) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            CollapsingLayout(
                people = people,
                isFavorite = isFavorite,
                navController = navController,
                onMovieClick = onMovieClick,
                insertFavoritePeople = insertFavoritePeople,
                deleteFavoritePeople = deleteFavoritePeople,
                onShowSnackbar = onShowSnackbar
            )
        }

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun CollapsingLayout(
    people: PeopleDetail,
    isFavorite: (Int) -> Boolean,
    navController: NavController,
    onMovieClick: (Int) -> Unit,
    insertFavoritePeople: (PeopleDetail) -> Unit,
    deleteFavoritePeople: (PeopleDetail) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val maxToolbarHeight = (screenWidth / PEOPLE_IMAGE_RATIO).toInt().dp
    val minToolbarHeight = 120.dp
    val toolbarHeightRange = with(LocalDensity.current) {
        minToolbarHeight.roundToPx()..maxToolbarHeight.roundToPx()
    }
    val toolbarState = rememberToolbarState(toolbarHeightRange)
    val listState = rememberLazyGridState()
    val scope = rememberCoroutineScope()
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                toolbarState.scrollTopLimitReached = listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
                toolbarState.scrollOffset -= available.y
                return Offset(0f, toolbarState.consumed)
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                if (available.y > 0) {
                    scope.launch {
                        animateDecay(
                            initialValue = toolbarState.height + toolbarState.offset,
                            initialVelocity = available.y,
                            animationSpec = FloatExponentialDecaySpec()
                        ) { value, velocity ->
                            toolbarState.scrollTopLimitReached = listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
                            toolbarState.scrollOffset -= (value - (toolbarState.height + toolbarState.offset))
                            if (toolbarState.scrollOffset == 0f) scope.coroutineContext.cancelChildren()
                        }
                    }
                }

                return super.onPostFling(consumed, available)
            }
        }
    }

    Title(
        title = people.name ?: "인물 정보",
        onBackClick = { navController.navigateUp() }
    )

    Box(modifier = Modifier.nestedScroll(nestedScrollConnection)) {
        TabComponent(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { translationY = toolbarState.height + toolbarState.offset }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { scope.coroutineContext.cancelChildren() }
                    )
                },
            contentPadding = PaddingValues(bottom = if (toolbarState is FixedScrollFlagState) minToolbarHeight else 0.dp),
            listState = listState,
            people = people,
            onMovieClick = onMovieClick,
            favoriteMovies = emptyList(),
            insertFavoriteMovie = {},
            deleteFavoriteMovie = {}
        )
        CollapsingToolbar(
            modifier = Modifier
                .fillMaxWidth()
                .height(with(LocalDensity.current) { toolbarState.height.toDp() })
                .graphicsLayer { translationY = toolbarState.offset },
            people = people,
            isFavorite = isFavorite,
            progress = toolbarState.progress,
            insertFavoritePeople = insertFavoritePeople,
            deleteFavoritePeople = deleteFavoritePeople,
            onShowSnackbar = onShowSnackbar
        )
    }
}

@Composable
fun PeopleInfoComponent(
    people: PeopleDetail
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = people.name ?: "")
        Text(text = people.birthday ?: "")
        Text(text = people.deathday ?: "")
        Text(text = people.placeOfBirth ?: "")
    }
}

@Composable
fun RelatedMovieComponent(
    listState: LazyGridState = rememberLazyGridState(),
    people: PeopleDetail,
    onMovieClick: (Int) -> Unit,
    insertFavoriteMovie: (MovieDetail) -> Unit,
    deleteFavoriteMovie: (MovieDetail) -> Unit
) {
    val relatedMovie = people.combineCredits?.cast?.filter { it.mediaType.equals("movie", true) }?.map {
        RelatedMovie(
            adult = it.adult,
            backdropPath = it.backdropPath,
            character = it.character,
            creditId = it.creditId,
            episodeCount = it.episodeCount,
            firstAirDate = it.firstAirDate,
            genreIds = it.genreIds,
            id = it.id,
            mediaType = it.mediaType,
            name = it.name,
            order = it.order,
            originCountry = it.originCountry,
            originalLanguage = it.originalLanguage,
            originalName = it.originalName,
            originalTitle = it.originalTitle,
            overview = it.overview,
            popularity = it.popularity,
            posterPath = it.posterPath,
            releaseDate = it.releaseDate,
            title = it.title,
            video = it.video,
            voteAverage = it.voteAverage,
            voteCount = it.voteCount,
            department = "",
            job = ""
        )
    }?.plus(
        people.combineCredits?.crew?.filter { it.mediaType.equals("movie", true) }?.map {
            RelatedMovie(
                adult = it.adult,
                backdropPath = it.backdropPath,
                creditId = it.creditId,
                episodeCount = it.episodeCount,
                firstAirDate = it.firstAirDate,
                genreIds = it.genreIds,
                id = it.id,
                mediaType = it.mediaType,
                name = it.name,
                originCountry = it.originCountry,
                originalLanguage = it.originalLanguage,
                originalName = it.originalName,
                originalTitle = it.originalTitle,
                overview = it.overview,
                popularity = it.popularity,
                posterPath = it.posterPath,
                releaseDate = it.releaseDate,
                title = it.title,
                video = it.video,
                voteAverage = it.voteAverage,
                voteCount = it.voteCount,
                department = it.department,
                job = it.job
            )
        } ?: emptyList()
    )?.sortedByDescending { it.releaseDate } ?: emptyList()

    LazyVerticalGrid(
        modifier = Modifier,
        state = listState,
        columns = GridCells.Adaptive(dp100),
        contentPadding = PaddingValues(dp10),
        horizontalArrangement = Arrangement.spacedBy(dp10),
        verticalArrangement = Arrangement.spacedBy(dp10)
    ) {
        items(relatedMovie.size) { index ->
            DynamicAsyncImageLoader(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(POSTER_IMAGE_RATIO)
                    .clickable { onMovieClick(people.combineCredits?.cast?.get(index)?.id ?: -1) },
                source = people.combineCredits?.cast?.get(index)?.posterPath ?: "",
                contentDescription = "SearchPoster"
            )
        }
    }
}