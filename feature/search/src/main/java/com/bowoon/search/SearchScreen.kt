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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.bowoon.common.Log
import com.bowoon.data.repository.LocalMovieAppDataComposition
import com.bowoon.data.util.POSTER_IMAGE_RATIO
import com.bowoon.firebase.LocalFirebaseLogHelper
import com.bowoon.model.Genre
import com.bowoon.model.PagingStatus
import com.bowoon.model.SearchKeyword
import com.bowoon.model.SearchType
import com.bowoon.movie.feature.search.R
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
import com.bowoon.ui.utils.dp8
import com.bowoon.ui.utils.sp12
import com.bowoon.ui.utils.sp20
import com.bowoon.ui.utils.sp30
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(
    goToMovie: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    goToSeries: (Int) -> Unit,
    viewModel: SearchVM = hiltViewModel()
) {
    LocalFirebaseLogHelper.current.sendLog("SearchScreen", "search screen init")

    val searchState by viewModel.searchResult.collectAsStateWithLifecycle()
    val selectedGenre by viewModel.selectedGenre.collectAsStateWithLifecycle()
    val searchType by viewModel.searchType.collectAsStateWithLifecycle()
    val recommendedKeyword = viewModel.recommendedKeywordPaging.collectAsLazyPagingItems()

    SearchScreen(
        searchState = searchState,
        recommendedKeyword = recommendedKeyword,
        keyword = viewModel.searchQuery,
        searchType = searchType,
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
    searchState: SearchState,
    recommendedKeyword: LazyPagingItems<SearchKeyword>,
    keyword: String,
    searchType: SearchType,
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
    val isVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TitleComponent(title = stringResource(R.string.title_search))
        SearchBarComponent(
            keyword = keyword,
            searchType = searchType,
            scrollState = scrollState,
            updateKeyword = updateKeyword,
            onSearchClick = onSearchClick,
            updateSearchType = updateSearchType,
            updateGenre = updateGenre
        )

        if (isVisible) {
            RecommendedKeywordComponent(
                recommendedKeyword = recommendedKeyword,
                keyword = keyword,
                updateKeyword = updateKeyword,
                onSearchClick = onSearchClick
            )
        } else {
            focusManager.clearFocus()
            SearchResultPaging(
                searchState = searchState,
                recommendedKeyword = recommendedKeyword,
                scrollState = scrollState,
                searchType = searchType,
                goToMovie = goToMovie,
                goToPeople = goToPeople,
                goToSeries = goToSeries,
                selectedGenre = selectedGenre,
                updateGenre = updateGenre,
                updateKeyword = updateKeyword,
                onSearchClick = onSearchClick,
                keyword = keyword
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
    updateGenre: (Genre?) -> Unit
) {
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search, keyboardType = KeyboardType.Text)
    val keyboardActions = KeyboardActions(
        onDone = { focusManager.clearFocus() },
        onSearch = {
            scope.launch { scrollState.scrollToItem(0) }
            updateGenre(null)
            onSearchClick()
            focusManager.clearFocus()
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
            .height(dp40)
            .clip(shape = RoundedCornerShape(50))
            .background(color = MaterialTheme.colorScheme.inverseOnSurface),
        value = keyword,
        onValueChange = {
            Log.d(it)
            updateKeyword(it)
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
                                .clickable { updateKeyword("") },
                            imageVector = Icons.Filled.Clear,
                            contentDescription = "searchKeywordClear"
                        )
                        Icon(
                            modifier = Modifier
                                .padding(end = dp16)
                                .clickable {
                                    scope.launch { scrollState.scrollToItem(0) }
                                    updateGenre(null)
                                    onSearchClick()
                                    focusManager.clearFocus()
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
fun SearchResultPaging(
    searchState: SearchState,
    recommendedKeyword: LazyPagingItems<SearchKeyword>,
    scrollState: LazyGridState,
    searchType: SearchType,
    goToMovie: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    goToSeries: (Int) -> Unit,
    selectedGenre: Genre?,
    updateGenre: (Genre) -> Unit,
    updateKeyword: (String) -> Unit,
    onSearchClick: () -> Unit,
    keyword: String
) {
    val posterUrl = LocalMovieAppDataComposition.current.getImageUrl()
    val genres = LocalMovieAppDataComposition.current.genres
    var isAppend by remember { mutableStateOf(false) }
    var pagingStatus by remember { mutableStateOf<PagingStatus>(PagingStatus.NONE) }

    when (searchState) {
        is SearchState.SearchEntry -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.do_search),
                    fontSize = sp20
                )
            }
        }
        is SearchState.SearchResult -> {
            val pagingData = searchState.pagingData.collectAsLazyPagingItems()

            when {
                pagingData.loadState.refresh is LoadState.Loading -> pagingStatus = PagingStatus.LOADING
                pagingData.loadState.append is LoadState.Loading -> isAppend = true
                pagingData.loadState.refresh is LoadState.Error -> {
                    isAppend = false

                    ConfirmDialog(
                        title = stringResource(com.bowoon.movie.core.network.R.string.network_failed),
                        message = (pagingData.loadState.refresh as? LoadState.Error)?.error?.message ?: stringResource(com.bowoon.movie.core.network.R.string.something_wrong),
                        confirmPair = stringResource(com.bowoon.movie.core.ui.R.string.retry_message) to { pagingData.retry() },
                        dismissPair = stringResource(com.bowoon.movie.core.ui.R.string.confirm_message) to {}
                    )
                }
                pagingData.loadState.refresh is LoadState.NotLoading -> {
                    isAppend = false
                    pagingStatus = if (pagingStatus == PagingStatus.LOADING) {
                        if (pagingData.itemCount == 0) PagingStatus.EMPTY else PagingStatus.NOT_EMPTY
                    } else {
                        pagingStatus
                    }
                }
                pagingData.loadState.append is LoadState.NotLoading -> {
                    isAppend = false
                }
            }

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                when (pagingStatus) {
                    PagingStatus.LOADING -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    PagingStatus.EMPTY -> {
                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            text = stringResource(R.string.search_result_empty),
                            fontSize = sp30,
                            textAlign = TextAlign.Center
                        )
                    }
                    else -> {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            if (pagingStatus == PagingStatus.NOT_EMPTY && SearchType.MOVIE == searchType) {
                                MovieFilterRowComponent(
                                    selectedGenre = selectedGenre,
                                    updateGenre = updateGenre
                                )
                            }

                            Box(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                if (pagingData.itemCount == 0) {
                                    Text(
                                        modifier = Modifier.align(Alignment.Center),
                                        text = stringResource(R.string.filter_not_found),
                                        fontSize = sp30,
                                        textAlign = TextAlign.Center
                                    )
                                }
                                LazyVerticalGrid(
                                    modifier = Modifier
                                        .semantics { contentDescription = "searchResultList" }
                                        .fillMaxSize(),
                                    state = scrollState,
                                    columns = GridCells.Adaptive(dp100),
                                    contentPadding = PaddingValues(top = if (genres.all { it.name == null } || searchType != SearchType.MOVIE) dp10 else dp0, start = dp10, bottom = dp10, end = dp10),
                                    horizontalArrangement = Arrangement.spacedBy(dp10),
                                    verticalArrangement = Arrangement.spacedBy(dp10)
                                ) {
                                    items(
                                        count = pagingData.itemCount,
                                        key = { index -> "${pagingData.peek(index)?.id}_${index}_${pagingData.peek(index)?.name}" }
                                    ) { index ->
                                        pagingData[index]?.let { item ->
                                            DynamicAsyncImageLoader(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .aspectRatio(POSTER_IMAGE_RATIO)
                                                    .bounceClick {
                                                        when (searchType) {
                                                            SearchType.MOVIE -> goToMovie(
                                                                item.id ?: -1
                                                            )

                                                            SearchType.PEOPLE -> goToPeople(
                                                                item.id ?: -1
                                                            )

                                                            SearchType.SERIES -> goToSeries(
                                                                item.id ?: -1
                                                            )
                                                        }
                                                    },
                                                source = "$posterUrl${item.imagePath}",
                                                contentDescription = "${item.id}_${item.name}"
                                            )
                                        }
                                    }
                                    if (isAppend) {
                                        item(span = { GridItemSpan(maxLineSpan) }) {
                                            CircularProgressIndicator(
                                                modifier = Modifier
                                                    .wrapContentSize()
                                                    .align(Alignment.Center)
                                            )
                                        }
                                    }
                                    if (pagingData.loadState.append is LoadState.Error) {
                                        item(span = { GridItemSpan(maxLineSpan) }) {
                                            PagingAppendErrorComponent({ pagingData.retry() })
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        is SearchState.Error -> LocalFirebaseLogHelper.current.sendLog("SearchResultPaging", searchState.message)
        is SearchState.EmptyKeyword -> {
            ConfirmDialog(
                title = "",
                message = stringResource(R.string.input_keyword),
                confirmPair = stringResource(com.bowoon.movie.core.ui.R.string.confirm_message) to {}
            )
        }
    }
}

@Composable
fun RecommendedKeywordComponent(
    recommendedKeyword: LazyPagingItems<SearchKeyword>,
    keyword: String,
    updateKeyword: (String) -> Unit,
    onSearchClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    var isAppend by remember { mutableStateOf(false) }
    var pagingStatus by remember { mutableStateOf<PagingStatus>(PagingStatus.NONE) }

    when {
        recommendedKeyword.loadState.append is LoadState.Loading -> isAppend = true
        recommendedKeyword.loadState.append is LoadState.NotLoading -> isAppend = false
        recommendedKeyword.loadState.refresh is LoadState.Loading -> pagingStatus = PagingStatus.LOADING
        recommendedKeyword.loadState.refresh is LoadState.NotLoading -> pagingStatus = PagingStatus.NOT_EMPTY
    }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        when (pagingStatus) {
            PagingStatus.LOADING -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            PagingStatus.EMPTY -> {}
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Text(
                            modifier = Modifier
                                .padding(start = dp16, end = dp16, top = dp10)
                                .fillMaxWidth(),
                            text = stringResource(R.string.recommended_keyword),
                            fontSize = sp20,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                    items(
                        count = recommendedKeyword.itemCount,
                        key = { index -> recommendedKeyword.peek(index)?.id ?: -1 }
                    ) { index ->
                        recommendedKeyword[index]?.let { recommendedKeyword ->
                            val annotatedString = buildAnnotatedString {
                                append(recommendedKeyword.name ?: "")
                                recommendedKeyword.name?.let { searchKeyword ->
                                    if (searchKeyword.contains(other = keyword)) {
                                        searchKeyword.indexOf(string = keyword).also { start ->
                                            addStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary), start = start, end = start + keyword.length)
                                        }
                                    }
                                }
                            }
                            Text(
                                modifier = Modifier
                                    .padding(start = dp16, end = dp16, top = dp10)
                                    .fillMaxWidth()
                                    .height(dp35)
                                    .bounceClick {
                                        updateKeyword(recommendedKeyword.name ?: "")
                                        onSearchClick()
                                        focusManager.clearFocus()
                                    },
                                text = annotatedString,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    if (isAppend) {
                        item {
                            CircularProgressIndicator(modifier = Modifier.wrapContentSize())
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MovieFilterRowComponent(
    selectedGenre: Genre?,
    updateGenre: (Genre) -> Unit
) {
    val genres = LocalMovieAppDataComposition.current.genres

    LazyRow(
        modifier = Modifier
            .testTag(tag = "FilterRow")
            .fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = dp16),
        horizontalArrangement = Arrangement.spacedBy(space = dp10)
    ) {
        items(
            items = genres,
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