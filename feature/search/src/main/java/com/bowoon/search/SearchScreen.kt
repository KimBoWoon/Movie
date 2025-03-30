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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.bowoon.common.Log
import com.bowoon.data.repository.LocalMovieAppDataComposition
import com.bowoon.data.util.POSTER_IMAGE_RATIO
import com.bowoon.firebase.LocalFirebaseLogHelper
import com.bowoon.model.Movie
import com.bowoon.model.PagingStatus
import com.bowoon.model.SearchType
import com.bowoon.ui.ConfirmDialog
import com.bowoon.ui.animateRotation
import com.bowoon.ui.bounceClick
import com.bowoon.ui.components.PagingAppendErrorComponent
import com.bowoon.ui.components.TitleComponent
import com.bowoon.ui.dp1
import com.bowoon.ui.dp10
import com.bowoon.ui.dp100
import com.bowoon.ui.dp15
import com.bowoon.ui.dp16
import com.bowoon.ui.dp5
import com.bowoon.ui.dp53
import com.bowoon.ui.dp8
import com.bowoon.ui.image.DynamicAsyncImageLoader
import com.bowoon.ui.sp12
import com.bowoon.ui.sp30
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(
    onMovieClick: (Int) -> Unit,
    onPeopleClick: (Int) -> Unit,
    viewModel: SearchVM = hiltViewModel()
) {
    LocalFirebaseLogHelper.current.sendLog("SearchScreen", "search screen init")

    val state = viewModel.searchResult.collectAsLazyPagingItems()

    SearchScreen(
        state = state,
        keyword = viewModel.searchQuery,
        searchType = viewModel.searchType,
//        selectedFilter = viewModel.selectedFilter,
        onMovieClick = onMovieClick,
        onPeopleClick = onPeopleClick,
        onSearchClick = viewModel::searchMovies,
        updateKeyword = viewModel::updateKeyword,
        updateSearchType = viewModel::updateSearchType,
//        updateFilter = viewModel::updateFilter
    )
}

@Composable
fun SearchScreen(
    state: LazyPagingItems<Movie>,
    keyword: String,
    searchType: Int,
//    selectedFilter: MovieGenre?,
    onMovieClick: (Int) -> Unit,
    onPeopleClick: (Int) -> Unit,
    onSearchClick: () -> Unit,
    updateKeyword: (String) -> Unit,
    updateSearchType: (SearchType) -> Unit,
//    updateFilter: (MovieGenre) -> Unit
) {
    val scrollState = rememberLazyGridState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TitleComponent(title = "영화 검색")
        SearchBarComponent(
            keyword = keyword,
            searchType = searchType,
            scrollState = scrollState,
            updateKeyword = updateKeyword,
            onSearchClick = onSearchClick,
            updateSearchType = updateSearchType
        )
        SearchResultPaging(
            state = state,
            scrollState = scrollState,
            searchType = searchType,
            onMovieClick = onMovieClick,
            onPeopleClick = onPeopleClick,
//            selectedFilter = selectedFilter,
//            updateFilter = updateFilter
        )
    }
}

@Composable
fun SearchBarComponent(
    keyword: String,
    searchType: Int,
    scrollState: LazyGridState,
    onSearchClick: () -> Unit,
    updateKeyword: (String) -> Unit,
    updateSearchType: (SearchType) -> Unit
) {
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search, keyboardType = KeyboardType.Text)
    val keyboardActions = KeyboardActions(
        onDone = { focusManager.clearFocus() },
        onSearch = {
            scope.launch { scrollState.scrollToItem(0) }
            onSearchClick()
            focusManager.clearFocus()
        }
    )

    BasicTextField(
        modifier = Modifier
            .fillMaxWidth()
            .height(dp53)
            .padding(top = dp10, start = dp16, end = dp16)
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
                            text = "검색어를 입력하세요.",
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
    searchType: Int,
    updateSearchType: (SearchType) -> Unit
) {
    var isExpand by remember { mutableStateOf(false) }

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.clickable { isExpand = !isExpand },
                text = SearchType.entries[searchType].label,
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
    state: LazyPagingItems<Movie>,
    scrollState: LazyGridState,
    searchType: Int,
    onMovieClick: (Int) -> Unit,
    onPeopleClick: (Int) -> Unit,
//    selectedFilter: MovieGenre?,
//    updateFilter: (MovieGenre) -> Unit
) {
    val posterUrl = LocalMovieAppDataComposition.current.getImageUrl()
//    val genreList = LocalMovieAppDataComposition.current.genres
    var isAppend by remember { mutableStateOf(false) }
    var pagingStatus by remember { mutableStateOf<PagingStatus>(PagingStatus.NONE) }

    when {
        state.loadState.refresh is LoadState.Loading -> pagingStatus = PagingStatus.LOADING
        state.loadState.append is LoadState.Loading -> isAppend = true
        state.loadState.refresh is LoadState.Error -> {
            isAppend = false

            ConfirmDialog(
                title = "Error",
                message = (state.loadState.refresh as? LoadState.Error)?.error?.message ?: "something wrong...",
                confirmPair = "재시도" to { state.retry() },
                dismissPair = "확인" to {}
            )
        }
        state.loadState.refresh is LoadState.NotLoading -> {
            isAppend = false
            pagingStatus = if (pagingStatus == PagingStatus.LOADING) {
                if (state.itemCount == 0) PagingStatus.EMPTY else PagingStatus.NOT_EMPTY
            } else {
                pagingStatus
            }
        }
        state.loadState.append is LoadState.NotLoading -> {
            isAppend = false
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (pagingStatus) {
            PagingStatus.LOADING -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            PagingStatus.EMPTY -> {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "검색결과가 없습니다.",
                    fontSize = sp30,
                    textAlign = TextAlign.Center
                )
            }
            else -> {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
//                    if (state.itemCount > 0) {
//                        LazyRow(
//                            modifier = Modifier.fillMaxWidth(),
//                            contentPadding = PaddingValues(horizontal = dp16),
//                            horizontalArrangement = Arrangement.spacedBy(space = dp10)
//                        ) {
//                            items(
//                                items = genreList ?: emptyList(),
//                                key = { it.id ?: -1 }
//                            ) { genre ->
//                                genre.name?.let { name ->
//                                    MovieGenreChipComponent(
//                                        title = name,
//                                        selectedFilter = selectedFilter?.id == genre.id,
//                                        updateFilter = { updateFilter(genre) }
//                                    )
//                                }
//                            }
//                        }
//                    }

                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LazyVerticalGrid(
                            modifier = Modifier
                                .semantics { contentDescription = "searchResultList" }
                                .fillMaxSize()
                                .padding(top = dp10),
                            state = scrollState,
                            columns = GridCells.Adaptive(dp100),
                            contentPadding = PaddingValues(dp10),
                            horizontalArrangement = Arrangement.spacedBy(dp10),
                            verticalArrangement = Arrangement.spacedBy(dp10)
                        ) {
                            items(
                                count = state.itemCount,
                                key = { index -> "${state.peek(index)?.id}_${index}_${state.peek(index)?.title}" }
                            ) { index ->
                                DynamicAsyncImageLoader(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(POSTER_IMAGE_RATIO)
                                        .bounceClick {
                                            when (searchType) {
                                                SearchType.MOVIE.ordinal -> onMovieClick(state[index]?.id ?: -1)
                                                SearchType.PEOPLE.ordinal -> onPeopleClick(state[index]?.id ?: -1)
                                            }
                                        },
                                    source = "$posterUrl${state[index]?.posterPath}",
                                    contentDescription = "${state[index]?.id}_${state[index]?.title}"
                                )
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
                            if (state.loadState.append is LoadState.Error) {
                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    PagingAppendErrorComponent({ state.retry() })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

//@Composable
//fun MovieGenreChipComponent(
//    title: String,
//    selectedFilter: Boolean,
//    updateFilter: () -> Unit
//) {
//    FilterChip(
//        onClick = { updateFilter() },
//        label = { Text(text = title) },
//        selected = selectedFilter,
//        leadingIcon = if (selectedFilter) {
//            {
//                Icon(
//                    imageVector = Icons.Filled.Done,
//                    contentDescription = "Done icon",
//                    modifier = Modifier.size(FilterChipDefaults.IconSize)
//                )
//            }
//        } else {
//            null
//        },
//    )
//}