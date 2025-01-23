package com.bowoon.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.bowoon.common.Log
import com.bowoon.data.util.POSTER_IMAGE_RATIO
import com.bowoon.model.Movie
import com.bowoon.model.PagingStatus
import com.bowoon.model.SearchType
import com.bowoon.ui.ConfirmDialog
import com.bowoon.ui.Title
import com.bowoon.ui.bounceClick
import com.bowoon.ui.dp10
import com.bowoon.ui.dp100
import com.bowoon.ui.dp16
import com.bowoon.ui.dp5
import com.bowoon.ui.image.DynamicAsyncImageLoader
import com.bowoon.ui.sp30
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(
    onMovieClick: (Int) -> Unit,
    onPeopleClick: (Int) -> Unit,
    viewModel: SearchVM = hiltViewModel()
) {
    val state = viewModel.searchMovieState.collectAsLazyPagingItems()

    SearchScreen(
        state = state,
        onMovieClick = onMovieClick,
        onPeopleClick = onPeopleClick,
        onSearchClick = viewModel::searchMovies,
        viewModel = viewModel
    )
}

@Composable
fun SearchScreen(
    state: LazyPagingItems<Movie>,
    onMovieClick: (Int) -> Unit,
    onPeopleClick: (Int) -> Unit,
    onSearchClick: (String) -> Unit,
    viewModel: SearchVM = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberLazyGridState()
    val focusManager = LocalFocusManager.current
    val keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search, keyboardType = KeyboardType.Text)
    val keyboardActions = KeyboardActions(
        onDone = { focusManager.clearFocus() },
        onSearch = {
            scope.launch { scrollState.scrollToItem(0) }
            onSearchClick(viewModel.keyword.text)
            focusManager.clearFocus()
        }
    )
    var isLoading by remember { mutableStateOf(false) }
    var isAppend by remember { mutableStateOf(false) }
    var pagingStatus by remember { mutableStateOf<PagingStatus>(PagingStatus.NONE) }

    when {
        state.loadState.refresh is LoadState.Loading -> {
            isLoading = true
            pagingStatus = PagingStatus.LOADING
        }
        state.loadState.append is LoadState.Loading -> isAppend = true
        state.loadState.refresh is LoadState.Error || state.loadState.append is LoadState.Error -> {
            isLoading = false
            isAppend = false

            ConfirmDialog(
                title = "Error",
                message = (state.loadState.refresh as? LoadState.Error)?.error?.message ?: "something wrong...",
                confirmPair = "재시도" to { state.retry() },
                dismissPair = "확인" to {}
            )
        }
        state.loadState.refresh is LoadState.NotLoading -> {
            isLoading = false
            isAppend = false
            pagingStatus = if (pagingStatus == PagingStatus.LOADING) {
                if (state.itemCount == 0) PagingStatus.EMPTY else PagingStatus.NOT_EMPTY
            } else {
                pagingStatus
            }
        }
        state.loadState.append is LoadState.NotLoading -> {
            isLoading = false
            isAppend = false
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Title(title = "영화 검색")
            Row(
                modifier = Modifier.padding(top = dp5, bottom = dp10),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ExposedDropdownSearchTypeMenu(
                    modifier = Modifier.width(dp100),
                    list = SearchType.entries.map { it.label }.toList(),
                    viewModel = viewModel
                )
                TextField(
                    modifier = Modifier
                        .wrapContentHeight()
                        .weight(1f)
                        .padding(start = dp5, end = dp5),
                    value = viewModel.keyword.text,
                    onValueChange = {
                        Log.d(it)
                        viewModel.update(TextFieldValue(it))
                    },
                    label = { Text("검색어를 입력하세요.") },
                    singleLine = true,
                    maxLines = 1,
                    keyboardOptions = keyboardOptions,
                    keyboardActions = keyboardActions
                )
                Button(
                    modifier = Modifier.padding(end = dp16),
                    onClick = {
                        scope.launch { scrollState.scrollToItem(0) }
                        onSearchClick(viewModel.keyword.text)
                        focusManager.clearFocus()
                    }
                ) {
                    Text(text = "검색")
                }
            }

            if (pagingStatus == PagingStatus.EMPTY) {
                Text(
                    modifier = Modifier.fillMaxSize(),
                    text = "검색결과가 없습니다.",
                    fontSize = sp30,
                    textAlign = TextAlign.Center
                )
            } else {
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxSize(),
                    state = scrollState,
                    columns = GridCells.Adaptive(dp100),
                    contentPadding = PaddingValues(dp10),
                    horizontalArrangement = Arrangement.spacedBy(dp10),
                    verticalArrangement = Arrangement.spacedBy(dp10)
                ) {
                    items(state.itemCount) { index ->
                        DynamicAsyncImageLoader(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(POSTER_IMAGE_RATIO)
                                .bounceClick {
                                    when (viewModel.searchType) {
                                        SearchType.MOVIE.ordinal -> onMovieClick(state[index]?.id ?: -1)
                                        SearchType.PEOPLE.ordinal -> onPeopleClick(state[index]?.id ?: -1)
                                    }
                                },
                            source = state[index]?.posterPath ?: "",
                            contentDescription = "SearchPoster"
                        )
                    }
                    if (isAppend) {
                        item {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }
            }
        }

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownSearchTypeMenu(
    modifier: Modifier,
    list: List<String>,
    viewModel: SearchVM = hiltViewModel()
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            modifier = modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
            readOnly = true,
            value = list[viewModel.searchType],
            onValueChange = {},
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
        )

        ExposedDropdownMenu(
            modifier = modifier,
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            list.forEach {
                DropdownMenuItem(
                    modifier = modifier,
                    text = {
                        Text(
                            text = it,
                            maxLines = 1
                        )
                    },
                    onClick = {
                        viewModel.searchType = list.indexOf(it)
                        expanded = false
                    }
                )
            }
        }
    }
}