package com.bowoon.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.bowoon.common.Log
import com.bowoon.data.util.POSTER_IMAGE_RATIO
import com.bowoon.firebase.LocalFirebaseLogHelper
import com.bowoon.model.DisplayItem
import com.bowoon.model.Genre
import com.bowoon.model.MovieAppData
import com.bowoon.model.SearchType
import com.bowoon.movie.feature.search.R
import com.bowoon.ui.components.CircularProgressComponent
import com.bowoon.ui.components.FilterChipComponent
import com.bowoon.ui.components.PagingAppendErrorComponent
import com.bowoon.ui.components.TitleComponent
import com.bowoon.ui.dialog.ConfirmDialog
import com.bowoon.ui.image.DynamicAsyncImageLoader
import com.bowoon.ui.utils.animateRotation
import com.bowoon.ui.utils.bounceClick
import com.bowoon.ui.utils.dp0
import com.bowoon.ui.utils.dp1
import com.bowoon.ui.utils.dp10
import com.bowoon.ui.utils.dp100
import com.bowoon.ui.utils.dp15
import com.bowoon.ui.utils.dp16
import com.bowoon.ui.utils.dp35
import com.bowoon.ui.utils.dp40
import com.bowoon.ui.utils.dp5
import com.bowoon.ui.utils.dp60
import com.bowoon.ui.utils.dp8
import com.bowoon.ui.utils.matchedColorString
import com.bowoon.ui.utils.sp12
import com.bowoon.ui.utils.sp20
import com.bowoon.ui.utils.sp30
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(
    goToMovie: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    goToSeries: (Int) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    viewModel: SearchVM = hiltViewModel()
) {
    LocalFirebaseLogHelper.current.sendLog("SearchScreen", "search screen init")

    val searchUiState by viewModel.searchResult.collectAsStateWithLifecycle()
    val selectedGenre by viewModel.selectedGenre.collectAsStateWithLifecycle()
    val searchType by viewModel.searchType.collectAsStateWithLifecycle()
    val recommendKeyword by viewModel.recommendKeywordPaging.collectAsStateWithLifecycle()
    val inputKeyword = stringResource(id = R.string.input_keyword)
    val movieAppData by viewModel.movieAppData.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(key1 = inputKeyword) {
        viewModel.showSnackbar
            .flowWithLifecycle(lifecycle = lifecycle, minActiveState = Lifecycle.State.STARTED)
            .collect { onShowSnackbar(inputKeyword, null) }
    }

    SearchScreen(
        searchUiState = searchUiState,
        recommendKeyword = recommendKeyword,
        keyword = viewModel.searchQuery,
        searchType = searchType,
        movieAppData = movieAppData,
        selectedGenre = selectedGenre,
        goToMovie = goToMovie,
        goToPeople = goToPeople,
        goToSeries = goToSeries,
        onSearchClick = viewModel::searchMovies,
        updateKeyword = viewModel::updateKeyword,
        updateSearchType = viewModel::updateSearchType,
        updateGenre = viewModel::updateGenre
    )
}

@Composable
fun SearchScreen(
    searchUiState: SearchUiState,
    recommendKeyword: RecommendKeywordUiState,
    keyword: String,
    searchType: SearchType,
    movieAppData: MovieAppData,
    selectedGenre: Genre?,
    goToMovie: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    goToSeries: (Int) -> Unit,
    onSearchClick: () -> Unit,
    updateKeyword: (String) -> Unit,
    updateSearchType: (SearchType) -> Unit,
    updateGenre: (Genre?) -> Unit
) {
    val scrollState = rememberLazyGridState()
    val focusManager = LocalFocusManager.current
    var isVisible by remember { mutableStateOf(value = false) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TitleComponent(title = stringResource(id = R.string.title_search))
        SearchBarComponent(
            keyword = keyword,
            searchType = searchType,
            scrollState = scrollState,
            updateKeyword = updateKeyword,
            onSearchClick = onSearchClick,
            updateSearchType = updateSearchType,
            updateGenre = updateGenre,
            recommendKeywordVisible = { isVisible = it }
        )

        if (isVisible) {
            RecommendKeywordComponent(
                recommendKeyword = recommendKeyword,
                keyword = keyword,
                updateKeyword = updateKeyword,
                onSearchClick = onSearchClick,
                recommendKeywordVisible = { isVisible = it }
            )
        } else {
            focusManager.clearFocus()
            SearchResultComponent(
                searchUiState = searchUiState,
                scrollState = scrollState,
                searchType = searchType,
                goToMovie = goToMovie,
                goToPeople = goToPeople,
                goToSeries = goToSeries,
                movieAppData = movieAppData,
                selectedGenre = selectedGenre,
                updateGenre = updateGenre
            )
        }
    }
}

@Composable
fun SearchBarComponent(
    keyword: String,
    searchType: SearchType,
    scrollState: LazyGridState,
    onSearchClick: () -> Unit,
    updateKeyword: (String) -> Unit,
    updateSearchType: (SearchType) -> Unit,
    updateGenre: (Genre?) -> Unit,
    recommendKeywordVisible: (Boolean) -> Unit
) {
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search, keyboardType = KeyboardType.Text)
    val keyboardActions = KeyboardActions(
        onDone = {
            focusManager.clearFocus()
            recommendKeywordVisible(false)
        },
        onSearch = {
            scope.launch { scrollState.scrollToItem(index = 0) }
            updateGenre(null)
            onSearchClick()
            focusManager.clearFocus()
            recommendKeywordVisible(false)
        }
    )

    BasicTextField(
        modifier = Modifier
            .padding(
                top = dp10,
                bottom = if (searchType == SearchType.MOVIE) dp0 else dp10,
                start = dp16,
                end = dp16
            )
            .fillMaxWidth()
            .height(height = dp40)
            .clip(shape = RoundedCornerShape(percent = 50))
            .background(color = MaterialTheme.colorScheme.inverseOnSurface),
        value = keyword,
        onValueChange = {
            Log.d(it)
            updateKeyword(it)
            if (it.isNotEmpty()) {
                recommendKeywordVisible(true)
            }
        },
        textStyle = TextStyle(
            fontSize = sp12,
            color = MaterialTheme.colorScheme.onSurface
        ),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.padding(start = dp16, end = dp8),
                    imageVector = Icons.Filled.Search,
                    contentDescription = "searchBarIcon"
                )

                SearchTypeComponent(
                    searchType = searchType,
                    updateSearchType = updateSearchType
                )

                Spacer(
                    modifier = Modifier
                        .padding(horizontal = dp5)
                        .width(dp1)
                        .height(dp10)
                        .background(color = MaterialTheme.colorScheme.onSurface)
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                ) {
                    innerTextField()
                    if (keyword.isEmpty()) {
                        Text(
                            text = stringResource(R.string.input_search_hint),
                            fontSize = sp12,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                        )
                    }
                }

                AnimatedVisibility(
                    visible = keyword.isNotEmpty(),
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(start = dp8, end = dp8)
                                .clickable {
                                    updateKeyword("")
                                    recommendKeywordVisible(false)
                                },
                            imageVector = Icons.Filled.Clear,
                            contentDescription = "searchKeywordClear"
                        )
                        Icon(
                            modifier = Modifier
                                .padding(end = dp16)
                                .clickable {
                                    scope.launch { scrollState.scrollToItem(index = 0) }
                                    updateGenre(null)
                                    onSearchClick()
                                    focusManager.clearFocus()
                                    recommendKeywordVisible(false)
                                },
                            imageVector = Icons.Filled.Search,
                            contentDescription = "searchMovies"
                        )
                    }
                }
            }
        },
        singleLine = true,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
    )
}

@Composable
fun SearchTypeComponent(
    searchType: SearchType,
    updateSearchType: (SearchType) -> Unit
) {
    var isExpand by remember { mutableStateOf(false) }

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .testTag(tag = "searchType")
                    .clickable { isExpand = !isExpand },
                text = searchType.label,
                fontSize = sp12,
                color = MaterialTheme.colorScheme.onSurface
            )
            Icon(
                modifier = Modifier
                    .size(dp15)
                    .animateRotation(
                        expanded = isExpand,
                        startAngle = 0f,
                        endAngle = -180f,
                        animateMillis = 200
                    ),
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = "searchTypeArrow"
            )
        }
        DropdownMenu(
            modifier = Modifier.wrapContentSize(),
            expanded = isExpand,
            onDismissRequest = { isExpand = false }
        ) {
            SearchType.entries.forEach { type ->
                DropdownMenuItem(
                    modifier = Modifier.testTag(tag = type.label),
                    onClick = {
                        Log.d(type.label)
                        updateSearchType(type)
                        isExpand = false
                    },
                    text = { Text(text = type.label) }
                )
            }
        }
    }
}

@Composable
fun SearchResultComponent(
    searchUiState: SearchUiState,
    scrollState: LazyGridState,
    searchType: SearchType,
    goToMovie: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    goToSeries: (Int) -> Unit,
    movieAppData: MovieAppData,
    selectedGenre: Genre?,
    updateGenre: (Genre) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (searchUiState) {
            is SearchUiState.SearchHint -> {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = stringResource(id = R.string.do_search),
                    fontSize = sp20
                )
            }
            is SearchUiState.Success -> {
                val pagingData = searchUiState.pagingData.collectAsLazyPagingItems()

                if (pagingData.loadState.refresh is LoadState.Loading) {
                    CircularProgressComponent(modifier = Modifier.align(Alignment.Center))
                } else if (pagingData.loadState.refresh is LoadState.Error) {
                    ConfirmDialog(
                        title = stringResource(id = com.bowoon.movie.core.network.R.string.network_failed),
                        message = (pagingData.loadState.refresh as? LoadState.Error)?.error?.message ?: stringResource(id = com.bowoon.movie.core.network.R.string.something_wrong),
                        confirmPair = stringResource(id = com.bowoon.movie.core.ui.R.string.retry_message) to { pagingData.retry() },
                        dismissPair = stringResource(id = com.bowoon.movie.core.ui.R.string.confirm_message) to {}
                    )
                }

                SearchPagingComponent(
                    pagingData = pagingData,
                    scrollState = scrollState,
                    searchType = searchType,
                    movieAppData = movieAppData,
                    selectedGenre = selectedGenre,
                    updateGenre = updateGenre,
                    goToMovie = goToMovie,
                    goToPeople = goToPeople,
                    goToSeries = goToSeries
                )
            }
            is SearchUiState.Error -> {
                LocalFirebaseLogHelper.current.sendLog("SearchResultPaging", searchUiState.throwable.message ?: stringResource(com.bowoon.movie.core.network.R.string.something_wrong))

                ConfirmDialog(
                    title = stringResource(id = com.bowoon.movie.core.network.R.string.network_failed),
                    message = searchUiState.throwable.message ?: stringResource(id = com.bowoon.movie.core.network.R.string.something_wrong),
                    confirmPair = stringResource(id = com.bowoon.movie.core.ui.R.string.confirm_message) to {}
                )
            }
        }
    }
}

@Composable
fun SearchPagingComponent(
    pagingData: LazyPagingItems<DisplayItem>,
    scrollState: LazyGridState,
    searchType: SearchType,
    movieAppData: MovieAppData,
    selectedGenre: Genre?,
    updateGenre: (Genre) -> Unit,
    goToMovie: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    goToSeries: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (SearchType.MOVIE == searchType) {
            MovieFilterRowComponent(
                movieAppData = movieAppData,
                selectedGenre = selectedGenre,
                updateGenre = updateGenre
            )
        }

        if (pagingData.itemCount == 0) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.search_result_empty),
                    fontSize = sp30,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyVerticalGrid(
                modifier = Modifier
                    .semantics { contentDescription = "searchResultList" }
                    .fillMaxSize(),
                state = scrollState,
                columns = GridCells.Adaptive(minSize = dp100),
                contentPadding = PaddingValues(top = if (movieAppData.genres.all { it.name == null } || searchType != SearchType.MOVIE) dp10 else dp0, start = dp10, bottom = dp10, end = dp10),
                horizontalArrangement = Arrangement.spacedBy(space = dp10),
                verticalArrangement = Arrangement.spacedBy(space = dp10)
            ) {
                items(
                    count = pagingData.itemCount,
                    key = { index -> "${pagingData.peek(index)?.id}_${index}_${pagingData.peek(index)?.title}" }
                ) { index ->
                    pagingData[index]?.let { item ->
                        DynamicAsyncImageLoader(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(POSTER_IMAGE_RATIO)
                                .bounceClick {
                                    when (searchType) {
                                        SearchType.MOVIE -> goToMovie(item.id ?: -1)
                                        SearchType.PEOPLE -> goToPeople(item.id ?: -1)
                                        SearchType.SERIES -> goToSeries(item.id ?: -1)
                                    }
                                },
                            source = item.imagePath ?: "",
                            contentDescription = "${item.id}_${item.title}"
                        )
                    }
                }
                if (pagingData.loadState.append is LoadState.Loading) {
                    item(span = { GridItemSpan(currentLineSpan = maxLineSpan) }) {
                        CircularProgressComponent(modifier = Modifier.wrapContentSize())
                    }
                }
                if (pagingData.loadState.append is LoadState.Error) {
                    item(span = { GridItemSpan(currentLineSpan = maxLineSpan) }) {
                        PagingAppendErrorComponent(retry = { pagingData.retry() })
                    }
                }
            }
        }
    }
}

@Composable
fun RecommendKeywordComponent(
    recommendKeyword: RecommendKeywordUiState,
    keyword: String,
    updateKeyword: (String) -> Unit,
    onSearchClick: () -> Unit,
    recommendKeywordVisible: (Boolean) -> Unit
) {
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (recommendKeyword) {
            is RecommendKeywordUiState.Loading -> CircularProgressComponent(modifier = Modifier.wrapContentSize().align(alignment = Alignment.Center))
            is RecommendKeywordUiState.Success -> {
                val recommendKeyword = recommendKeyword.pagingData.collectAsLazyPagingItems()

                LazyColumn(
                    modifier = Modifier.semantics { contentDescription = "recommendKeywordList" }.fillMaxSize()
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(height = dp60),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier.padding(start = dp16),
                                text = stringResource(id = R.string.recommend_keyword),
                                fontSize = sp20,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Icon(
                                modifier = Modifier
                                    .clickable { recommendKeywordVisible(false) }
                                    .padding(end = dp16),
                                imageVector = Icons.Filled.Close,
                                contentDescription = "recommendKeywordClose"
                            )
                        }
                    }
                    items(
                        count = recommendKeyword.itemCount,
                        key = { index -> recommendKeyword.peek(index)?.id ?: -1 }
                    ) { index ->
                        recommendKeyword[index]?.let { recommendKeyword ->
                            val annotatedString = recommendKeyword.name.matchedColorString(keyword = keyword, color = MaterialTheme.colorScheme.primary)
                            Text(
                                modifier = Modifier
                                    .semantics { contentDescription = annotatedString.toString() }
                                    .padding(start = dp16, end = dp16, top = dp10)
                                    .fillMaxWidth()
                                    .height(height = dp35)
                                    .bounceClick {
                                        updateKeyword(recommendKeyword.name ?: "")
                                        onSearchClick()
                                        focusManager.clearFocus()
                                        recommendKeywordVisible(false)
                                    },
                                text = annotatedString,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    if (recommendKeyword.loadState.append is LoadState.Loading) {
                        item {
                            CircularProgressComponent(modifier = Modifier.wrapContentSize())
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MovieFilterRowComponent(
    movieAppData: MovieAppData,
    selectedGenre: Genre?,
    updateGenre: (Genre) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .testTag(tag = "FilterRow")
            .fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = dp16),
        horizontalArrangement = Arrangement.spacedBy(space = dp10)
    ) {
        items(
            items = movieAppData.genres,
            key = { it.id ?: -1 }
        ) { genre ->
            genre.name?.let { name ->
                FilterChipComponent(
                    title = name,
                    selectedFilter = selectedGenre?.id == genre.id,
                    updateFilter = { updateGenre(genre) }
                )
            }
        }
    }
}